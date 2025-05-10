package com.cybernostics.jsp2thymeleaf.reporting.instrumentation;

import com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.exception.FileErrorLocation;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.reporting.ReportingFramework;
import com.cybernostics.jsp2thymeleaf.reporting.api.ReportCollector;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instrumente la classe JSP2Thymeleaf pour collecter des métriques sur le processus de conversion.
 * Respecte le principe Open/Closed en étendant la fonctionnalité sans modifier le code source original.
 */
public class InstrumentedJSP2Thymeleaf {
    
    private static final Logger LOGGER = Logger.getLogger(InstrumentedJSP2Thymeleaf.class.getName());
    private final JSP2Thymeleaf delegate;
    private final ReportCollector reportCollector;
    private final JSP2ThymeleafConfiguration configuration;
    
    public InstrumentedJSP2Thymeleaf(JSP2ThymeleafConfiguration configuration) {
        this.configuration = configuration;
        this.delegate = new JSP2Thymeleaf(configuration);
        this.reportCollector = ReportingFramework.getReportCollector();
        
        // Initialiser le rapport avec les métadonnées du projet
        ConversionReport report = reportCollector.initializeReport("JSP2Thymeleaf Conversion", "1.0");
        
        // Configuration de convertisseur
        report.setConverterVersion(getClass().getPackage().getImplementationVersion());
        populateConfiguration(report);
    }
    
    private void populateConfiguration(ConversionReport report) {
        // Ajouter les paramètres de configuration importants au rapport
        report.addConverterConfigurationItem("sourceFolder", configuration.getSrcFolder().toString());
        report.addConverterConfigurationItem("destinationFolder", configuration.getDestFolder().toString());
        report.addConverterConfigurationItem("includePatterns", String.join(",", configuration.getIncludes()));
        report.addConverterConfigurationItem("excludePatterns", String.join(",", configuration.getExcludes()));
        report.addConverterConfigurationItem("converterPackages", String.join(",", configuration.getConverterPackages()));
        report.addConverterConfigurationItem("showBanner", String.valueOf(configuration.isShowBanner()));
    }
    
    /**
     * Exécute la conversion JSP et collecte des métriques pour le rapport.
     * Encapsule la méthode run() de JSP2Thymeleaf avec des appels d'instrumentation.
     * 
     * @return Liste d'exceptions rencontrées pendant la conversion
     */
    public List<JSP2ThymeLeafException> run() {
        LOGGER.info("Début du processus de conversion JSP vers Thymeleaf avec instrumentation");
        LocalDateTime startTime = LocalDateTime.now();
        
        // Exécuter la conversion via la classe originale
        List<JSP2ThymeLeafException> exceptions = delegate.run();
        
        // Enregistrer les erreurs globales
        for (JSP2ThymeLeafException exception : exceptions) {
            reportCollector.recordIssue(mapExceptionToIssue(exception));
        }
        
        // Compléter le rapport
        ConversionReport report = reportCollector.completeReport();
        LOGGER.log(Level.INFO, "Conversion terminée. {0} fichiers traités, {1} convertis avec succès, {2} échecs.",
                new Object[]{report.getTotalFilesProcessed(), report.getSuccessfullyConverted(), report.getFailedConversions()});
        
        return exceptions;
    }
    
    /**
     * Convertit une exception JSP2ThymeLeaf en ConversionIssue pour le rapport.
     */
    private ConversionIssue mapExceptionToIssue(JSP2ThymeLeafException exception) {
        String filePath = "unknown";
        int lineNumber = 0;
        
        try {
            // Essayer d'obtenir le chemin du fichier à partir de l'emplacement de l'erreur
            FileErrorLocation location = exception.getLocation();
            if (location != null) {
                filePath = location.getFilename();
                lineNumber = location.getLine();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to extract location details from exception", e);
        }
        
        return new ConversionIssue(
            filePath,
            lineNumber,
            exception.getMessage(),
            ConversionIssue.Severity.ERROR
        );
    }
    
    /**
     * Helper method to safely get the file path from an exception
     */
    private Path getExceptionFilePath(JSP2ThymeLeafException exception) {
        try {
            String path = exception.getLocation().getFilename();
            return path != null ? Paths.get(path) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Helper method to safely get the line number from an exception
     */
    private int getExceptionLineNumber(JSP2ThymeLeafException exception) {
        try {
            return exception.getLocation().getLine();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Génère les rapports dans le répertoire spécifié.
     * 
     * @param outputDir Le chemin vers le répertoire de sortie des rapports
     * @return Liste des chemins vers les rapports générés
     * @throws IOException Si une erreur se produit pendant la génération des rapports
     */
    public List<Path> generateReports(String outputDir) throws IOException {
        return ReportingFramework.generateReports(reportCollector.getCurrentReport(), outputDir);
    }
}