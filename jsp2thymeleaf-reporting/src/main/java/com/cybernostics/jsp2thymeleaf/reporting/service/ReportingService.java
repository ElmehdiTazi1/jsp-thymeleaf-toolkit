package com.cybernostics.jsp2thymeleaf.reporting.service;

import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for processing, saving, and generating conversion reports.
 */
public class ReportingService {
    
    private static final Logger LOG = Logger.getLogger(ReportingService.class.getName());
    private final ObjectMapper objectMapper;

    /**
     * Creates a new ReportingService
     */
    public ReportingService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Load conversion data from a JSON file
     *
     * @param jsonPath Path to the conversion data JSON file
     * @return The loaded conversion report, or null if not found/invalid
     */
    public ConversionReport loadConversionData(Path jsonPath) {
        try {
            if (Files.isDirectory(jsonPath)) {
                // If path is a directory, look for conversion-data.json inside it
                jsonPath = jsonPath.resolve("conversion-data.json");
            }
            
            if (Files.exists(jsonPath)) {
                LOG.info("Loading conversion data from: " + jsonPath);
                return objectMapper.readValue(jsonPath.toFile(), ConversionReport.class);
            } else {
                LOG.warning("Conversion data file not found: " + jsonPath);
                return null;
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error loading conversion data: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generate a report from conversion data
     *
     * @param report The conversion report data
     * @param outputPath Path where to write the report
     * @param verbose Whether to include verbose details in the report
     * @throws IOException If there's an error writing the report
     */
    public void generateReport(ConversionReport report, Path outputPath, boolean verbose) throws IOException {
        // Create parent directory if it doesn't exist
        Files.createDirectories(outputPath.getParent());
        
        String extension = getExtension(outputPath.toString());
        ReportGeneratorType type = determineReportType(extension);
        
        LOG.info("Generating " + type + " report to: " + outputPath);
        
        switch (type) {
            case HTML:
                generateHtmlReport(report, outputPath, verbose);
                break;
            case JSON:
                generateJsonReport(report, outputPath, verbose);
                break;
            case SUMMARY:
                generateSummaryReport(report, outputPath, verbose);
                break;
            default:
                LOG.warning("Unknown report type: " + type + ". Defaulting to JSON.");
                generateJsonReport(report, outputPath, verbose);
        }
    }
    
    /**
     * Write report data to JSON file
     *
     * @param report The report data to write
     * @param outputPath Path to write the JSON file
     * @throws IOException If there's an error writing the file
     */
    public void writeReportToJson(ConversionReport report, Path outputPath) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), report);
        LOG.info("Report data saved to: " + outputPath);
    }
    
    private void generateHtmlReport(ConversionReport report, Path outputPath, boolean verbose) throws IOException {
        // Basic HTML report implementation
        try (Writer writer = new FileWriter(outputPath.toFile(), StandardCharsets.UTF_8)) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("  <title>JSP to Thymeleaf Conversion Report</title>\n");
            writer.write("  <style>\n");
            writer.write("    body { font-family: Arial, sans-serif; margin: 20px; }\n");
            writer.write("    h1 { color: #2c3e50; }\n");
            writer.write("    .summary { background-color: #f8f9fa; padding: 15px; border-radius: 5px; }\n");
            writer.write("    .success { color: green; }\n");
            writer.write("    .error { color: red; }\n");
            writer.write("  </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("  <h1>JSP to Thymeleaf Conversion Report</h1>\n");
            writer.write("  <div class=\"summary\">\n");
            writer.write("    <h2>Summary</h2>\n");
            writer.write("    <p><strong>Project:</strong> " + report.getProjectName() + " " + report.getProjectVersion() + "</p>\n");
            writer.write("    <p><strong>Files Processed:</strong> " + report.getTotalFilesProcessed() + "</p>\n");
            writer.write("    <p><strong>Successfully Converted:</strong> " + report.getSuccessfullyConvertedFiles() + "</p>\n");
            writer.write("    <p><strong>Files with Errors:</strong> " + report.getFilesWithErrors() + "</p>\n");
            writer.write("    <p><strong>Failed Conversions:</strong> " + report.getFailedConversions() + "</p>\n");
            writer.write("  </div>\n");
            
            if (verbose && report.getIssues() != null && !report.getIssues().isEmpty()) {
                writer.write("  <h2>Issues</h2>\n");
                writer.write("  <ul>\n");
                report.getIssues().forEach(issue -> {
                    try {
                        writer.write("    <li class=\"error\">" + issue.getMessage() + " (" + issue.getFilePath() + ")</li>\n");
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "Error writing issue to report", e);
                    }
                });
                writer.write("  </ul>\n");
            }
            
            writer.write("</body>\n");
            writer.write("</html>\n");
        }
    }
    
    private void generateJsonReport(ConversionReport report, Path outputPath, boolean verbose) throws IOException {
        writeReportToJson(report, outputPath);
    }
    
    private void generateSummaryReport(ConversionReport report, Path outputPath, boolean verbose) throws IOException {
        try (Writer writer = new FileWriter(outputPath.toFile(), StandardCharsets.UTF_8)) {
            writer.write("JSP TO THYMELEAF CONVERSION SUMMARY\n");
            writer.write("=================================\n\n");
            writer.write("Project: " + report.getProjectName() + " " + report.getProjectVersion() + "\n");
            writer.write("Files Processed: " + report.getTotalFilesProcessed() + "\n");
            writer.write("Successfully Converted: " + report.getSuccessfullyConvertedFiles() + "\n");
            writer.write("Files with Errors: " + report.getFilesWithErrors() + "\n");
            writer.write("Failed Conversions: " + report.getFailedConversions() + "\n\n");
            
            if (verbose && report.getIssues() != null && !report.getIssues().isEmpty()) {
                writer.write("ISSUES:\n");
                writer.write("-------\n");
                report.getIssues().forEach(issue -> {
                    try {
                        writer.write(" - " + issue.getMessage() + " (" + issue.getFilePath() + ")\n");
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "Error writing issue to report", e);
                    }
                });
            }
        }
    }
    
    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    private ReportGeneratorType determineReportType(String extension) {
        switch (extension) {
            case "html":
                return ReportGeneratorType.HTML;
            case "json":
                return ReportGeneratorType.JSON;
            case "txt":
                return ReportGeneratorType.SUMMARY;
            default:
                // Default to JSON for unknown extensions
                return ReportGeneratorType.JSON;
        }
    }
}