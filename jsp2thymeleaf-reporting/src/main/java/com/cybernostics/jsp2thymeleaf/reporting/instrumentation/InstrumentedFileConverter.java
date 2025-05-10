package com.cybernostics.jsp2thymeleaf.reporting.instrumentation;

import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.exception.FileErrorLocation;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeleafFileConverter;
import com.cybernostics.jsp2thymeleaf.reporting.api.ReportCollector;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue;
import com.cybernostics.jsp2thymeleaf.reporting.model.FileConversionSummary;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convertisseur de fichier instrumenté pour collecter des métriques pendant le processus de conversion.
 * Applique le principe de Liskov Substitution en se substituant à JSP2ThymeleafFileConverter sans changer son comportement.
 */
public class InstrumentedFileConverter extends JSP2ThymeleafFileConverter {

    private static final Logger LOGGER = Logger.getLogger(InstrumentedFileConverter.class.getName());
    private final ReportCollector reportCollector;
    
    public InstrumentedFileConverter(JSP2ThymeleafConfiguration configuration, ReportCollector reportCollector) {
        super(configuration);
        this.reportCollector = reportCollector;
    }
    
    @Override
    public List<JSP2ThymeLeafException> convert(TokenisedFile file, File toWrite, ScopedJSPConverters converterScope) {
        // Créer un résumé de conversion pour ce fichier
        FileConversionSummary initialSummary = reportCollector.startFileConversion(file.getFilePath().toString());
        
        // Start building a new summary with the initial data
        FileConversionSummary.Builder summaryBuilder = FileConversionSummary.builder()
                .withSourcePath(initialSummary.getSourcePath())
                .withConversionStartTime(LocalDateTime.now());
        
        // Mesurer le temps de conversion
        Instant start = Instant.now();
        
        // Appel à la méthode originale avec un convertisseur de portée instrumenté
        List<JSP2ThymeLeafException> exceptions = super.convert(file, toWrite, 
                new InstrumentedScopedJSPConverters(converterScope, reportCollector, file.getFilePath()));
        
        // Calculer la durée
        Duration duration = Duration.between(start, Instant.now());
        
        // Mettre à jour le résumé de la conversion
        summaryBuilder
                .withConversionDuration(duration)
                .withConversionEndTime(LocalDateTime.now());
        
        // Set destination path if toWrite is not null
        if (toWrite != null) {
            summaryBuilder.withDestinationPath(toWrite.toPath());
        }
        
        // Ajouter les éventuelles exceptions comme problèmes
        for (JSP2ThymeLeafException exception : exceptions) {
            ConversionIssue issue = mapExceptionToIssue(exception, file.getFilePath());
            summaryBuilder.addIssue(issue);
            reportCollector.recordIssue(issue);
        }
        
        // Définir le statut de conversion
        if (exceptions.isEmpty()) {
            summaryBuilder.withConversionStatus(FileConversionSummary.ConversionStatus.SUCCESS)
                        .withConversionSuccessful(true);
        } else if (exceptions.size() >= 5) {
            summaryBuilder.withConversionStatus(FileConversionSummary.ConversionStatus.FAILED)
                        .withConversionSuccessful(false);
        } else {
            summaryBuilder.withConversionStatus(FileConversionSummary.ConversionStatus.PARTIAL)
                        .withConversionSuccessful(false);
        }
        
        // Finalize the summary
        FileConversionSummary completedSummary = summaryBuilder.build();
        
        // Log the result
        LOGGER.log(Level.INFO, "Fichier {0} converti en {1}ms (statut: {2})", 
                new Object[]{file.getFilePath(), duration.toMillis(), completedSummary.getConversionStatus()});
        
        // Compléter le résumé dans le collecteur
        reportCollector.completeFileConversion(completedSummary);
        
        return exceptions;
    }
    
    /**
     * Convertit une exception JSP2ThymeLeaf en un problème de conversion pour le rapport.
     * 
     * @param exception L'exception à convertir
     * @param filePath Le chemin du fichier concerné
     * @return Un objet ConversionIssue
     */
    private ConversionIssue mapExceptionToIssue(JSP2ThymeLeafException exception, Path filePath) {
        ConversionIssue.Severity severity;
        
        // Déterminer la sévérité en fonction du type d'exception
        severity = detectSeverity(exception);
        
        // Extraire de manière sécurisée les informations d'emplacement
        FileErrorLocation location = null;
        try {
            location = exception.getLocation();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Failed to get location from exception", e);
        }
        
        int lineNumber = 0;
        int columnNumber = 0;
        String suggestion = "";
        
        if (location != null) {
            lineNumber = location.getLine();
            columnNumber = location.getColumn();
        }
        
        // Construire l'objet ConversionIssue
        ConversionIssue.Builder issueBuilder = ConversionIssue.builder()
                .withFilePath(filePath)
                .withLineNumber(lineNumber)
                .withColumnNumber(columnNumber)
                .withSeverity(severity)
                .withMessage(exception.getMessage())
                .withIssueType(exception.getClass().getSimpleName());
                
        // Ajouter la suggestion si disponible
        try {
            // If this throws an exception, we'll just ignore it
            suggestion = getSuggestion(exception);
            if (suggestion != null && !suggestion.isEmpty()) {
                issueBuilder.withSuggestion(suggestion);
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Failed to get suggestion from exception", e);
        }
        
        return issueBuilder.build();
    }
    
    /**
     * Safely detect severity from the exception
     */
    private ConversionIssue.Severity detectSeverity(JSP2ThymeLeafException exception) {
        try {
            // Here we'll use the class name to infer severity
            String className = exception.getClass().getSimpleName().toLowerCase();
            if (className.contains("fatal")) {
                return ConversionIssue.Severity.CRITICAL;
            } else if (className.contains("error")) {
                return ConversionIssue.Severity.ERROR;
            } else {
                return ConversionIssue.Severity.WARNING;
            }
        } catch (Exception e) {
            return ConversionIssue.Severity.ERROR; // Default to ERROR
        }
    }
    
    /**
     * Safely get suggestion from the exception
     */
    private String getSuggestion(JSP2ThymeLeafException exception) {
        // We can't directly call getSuggestion(), so we'll use a heuristic approach
        String message = exception.getMessage();
        if (message != null && message.toLowerCase().contains("suggested:")) {
            int index = message.toLowerCase().indexOf("suggested:");
            if (index != -1 && index + 10 < message.length()) {
                return message.substring(index + 10).trim();
            }
        }
        return "";
    }
}