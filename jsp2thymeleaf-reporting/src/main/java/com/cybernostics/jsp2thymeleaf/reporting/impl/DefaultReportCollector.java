package com.cybernostics.jsp2thymeleaf.reporting.impl;

import com.cybernostics.jsp2thymeleaf.reporting.api.ReportCollector;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.cybernostics.jsp2thymeleaf.reporting.model.FileConversionSummary;
import com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletInfo;
import com.cybernostics.jsp2thymeleaf.reporting.model.TagConversionInfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Implémentation par défaut du collecteur de rapports.
 * Capture les informations de conversion pour générer des rapports.
 * Respecte le principe Single Responsibility en se concentrant uniquement sur la collecte de données.
 */
public class DefaultReportCollector implements ReportCollector {
    
    private static final Logger logger = Logger.getLogger(DefaultReportCollector.class.getName());
    
    private ConversionReport report;
    private ConversionReport.Builder reportBuilder;
    private Map<String, FileConversionSummary> fileConversionSummaries;
    
    /**
     * Crée un nouveau collecteur de rapports avec un rapport vide.
     */
    public DefaultReportCollector() {
        this.reportBuilder = ConversionReport.builder()
                .withProjectName("JSP to Thymeleaf Conversion") // Default name
                .withStartTime(LocalDateTime.now());
        this.fileConversionSummaries = new HashMap<>();
    }
    
    /**
     * Crée un collecteur avec un rapport existant.
     * 
     * @param report Le rapport à utiliser
     */
    public DefaultReportCollector(ConversionReport report) {
        this.report = report;
        // Create a builder with the existing report data for future modifications
        this.reportBuilder = ConversionReport.builder()
                .withProjectName(report.getProjectName())
                .withStartTime(report.getStartTime())
                .withEndTime(report.getEndTime())
                .withTotalDuration(report.getTotalDuration())
                .withTotalFilesProcessed(report.getTotalFilesProcessed())
                .withSuccessfullyConvertedFiles(report.getSuccessfullyConvertedFiles())
                .withFilesWithErrors(report.getFilesWithErrors());
        
        // Copy existing summaries
        this.fileConversionSummaries = new HashMap<>(report.getFileConversionSummaries());
    }
    
    @Override
    public ConversionReport initializeReport(String projectName, String projectVersion) {
        reportBuilder.withProjectName(projectName);
        // Store project version in the report builder
        // (Assuming projectVersion can be stored somewhere in the report)
        logger.info("Rapport initialisé pour le projet " + projectName + " v" + projectVersion);
        
        // Build the report to provide an initial snapshot
        report = reportBuilder.build();
        return report;
    }

    @Override
    public FileConversionSummary startFileConversion(String filePath) {
        FileConversionSummary.Builder builder = FileConversionSummary.builder()
                .withSourcePath(Paths.get(filePath))
                .withConversionStartTime(LocalDateTime.now());
        
        FileConversionSummary summary = builder.build();
        fileConversionSummaries.put(filePath, summary);
        
        logger.fine("Début de conversion du fichier: " + filePath);
        return summary;
    }

    @Override
    public void completeFileConversion(FileConversionSummary summary) {
        String filePath = summary.getSourcePath().toString();
        logger.fine("Fin de conversion du fichier: " + filePath 
                  + " (statut: " + summary.getConversionStatus() + ")");
        
        // Update the file summary in our map
        fileConversionSummaries.put(filePath, summary);
        
        // Update the report builder with all summaries
        reportBuilder.withFileSummaries(fileConversionSummaries.values().stream().toList());
    }

    @Override
    public void recordTagConversion(TagConversionInfo tagInfo) {
        // Since we can't modify the report directly, we'll need to rebuild it
        // when we generate the final report
        
        // Get the file summary for this tag, if it exists
        String filePath = tagInfo.getFilePath().toString();
        FileConversionSummary existingSummary = fileConversionSummaries.get(filePath);
        
        if (existingSummary != null) {
            // Create a new builder with the existing data
            FileConversionSummary.Builder summaryBuilder = FileConversionSummary.builder()
                    .withSourcePath(existingSummary.getSourcePath())
                    .withDestinationPath(existingSummary.getDestinationPath())
                    .withConversionStartTime(existingSummary.getConversionStartTime())
                    .withConversionEndTime(existingSummary.getConversionEndTime())
                    .withConversionDuration(existingSummary.getConversionDuration())
                    .withConversionSuccessful(existingSummary.isConversionSuccessful())
                    .withConversionStatus(existingSummary.getConversionStatus())
                    .withIssues(existingSummary.getIssues())
                    .withScriptletsProcessed(existingSummary.getScriptletsProcessed())
                    .withTagsConverted(existingSummary.getTagsConverted() + 1); // Increment converted tags
            
            // Update the summary in our map
            FileConversionSummary updatedSummary = summaryBuilder.build();
            fileConversionSummaries.put(filePath, updatedSummary);
        }
    }

    @Override
    public void recordScriptletProcessing(ScriptletInfo scriptletInfo) {
        // Similar to recordTagConversion, we need to update the file summary
        String filePath = scriptletInfo.getFilePath().toString();
        FileConversionSummary existingSummary = fileConversionSummaries.get(filePath);
        
        if (existingSummary != null) {
            // Create a new builder with the existing data
            FileConversionSummary.Builder summaryBuilder = FileConversionSummary.builder()
                    .withSourcePath(existingSummary.getSourcePath())
                    .withDestinationPath(existingSummary.getDestinationPath())
                    .withConversionStartTime(existingSummary.getConversionStartTime())
                    .withConversionEndTime(existingSummary.getConversionEndTime())
                    .withConversionDuration(existingSummary.getConversionDuration())
                    .withConversionSuccessful(existingSummary.isConversionSuccessful())
                    .withConversionStatus(existingSummary.getConversionStatus())
                    .withIssues(existingSummary.getIssues())
                    .withScriptletsProcessed(existingSummary.getScriptletsProcessed() + 1) // Increment scriptlets
                    .withTagsConverted(existingSummary.getTagsConverted());
            
            // Update the summary in our map
            FileConversionSummary updatedSummary = summaryBuilder.build();
            fileConversionSummaries.put(filePath, updatedSummary);
        }
    }

    @Override
    public void recordIssue(ConversionIssue issue) {
        // Add issue to the report builder
        reportBuilder.addConversionIssue(issue);
        
        // Update the file summary with this issue
        String filePath = issue.getFilePath().toString();
        FileConversionSummary existingSummary = fileConversionSummaries.get(filePath);
        
        if (existingSummary != null) {
            // Create new builder with existing data
            FileConversionSummary.Builder summaryBuilder = FileConversionSummary.builder()
                    .withSourcePath(existingSummary.getSourcePath())
                    .withDestinationPath(existingSummary.getDestinationPath())
                    .withConversionStartTime(existingSummary.getConversionStartTime())
                    .withConversionEndTime(existingSummary.getConversionEndTime())
                    .withConversionDuration(existingSummary.getConversionDuration())
                    .withConversionSuccessful(existingSummary.isConversionSuccessful())
                    .withTagsConverted(existingSummary.getTagsConverted())
                    .withScriptletsProcessed(existingSummary.getScriptletsProcessed())
                    .withIssues(existingSummary.getIssues());
            
            // Add the new issue
            summaryBuilder.addIssue(issue);
            
            // Update conversion status if needed
            if (issue.getSeverity() == ConversionIssue.Severity.ERROR && 
                existingSummary.getConversionStatus() != FileConversionSummary.ConversionStatus.FAILED) {
                summaryBuilder.withConversionStatus(FileConversionSummary.ConversionStatus.PARTIAL);
            }
            
            // Update the summary in our map
            FileConversionSummary updatedSummary = summaryBuilder.build();
            fileConversionSummaries.put(filePath, updatedSummary);
        }
    }

    @Override
    public ConversionReport completeReport() {
        logger.info("Finalisation du rapport de conversion");
        
        // Set the end time and calculate duration
        reportBuilder.withEndTime(LocalDateTime.now());
        
        // Update the file summaries
        reportBuilder.withFileSummaries(fileConversionSummaries.values().stream().toList());
        
        // Calculate success rates and other stats
        int totalFiles = fileConversionSummaries.size();
        int successfulFiles = (int) fileConversionSummaries.values().stream()
                .filter(s -> s.getConversionStatus() == FileConversionSummary.ConversionStatus.SUCCESS)
                .count();
        int filesWithErrors = totalFiles - successfulFiles;
        
        reportBuilder.withTotalFilesProcessed(totalFiles)
                .withSuccessfullyConvertedFiles(successfulFiles)
                .withFilesWithErrors(filesWithErrors);
        
        // Build and return the final report
        report = reportBuilder.build();
        return report;
    }
    
    @Override
    public ConversionReport getCurrentReport() {
        // Return the current state of the report by building it from the builder
        if (report == null) {
            report = reportBuilder.build();
        }
        return report;
    }
}