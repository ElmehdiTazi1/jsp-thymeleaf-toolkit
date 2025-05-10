package com.cybernostics.jsp2thymeleaf.reporting.impl;

import com.cybernostics.jsp2thymeleaf.reporting.api.ReportGenerator;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generates JSON reports from conversion data.
 * Follows Single Responsibility Principle by focusing only on JSON report generation.
 */
public class JsonReportGenerator implements ReportGenerator {
    
    private static final Logger LOG = Logger.getLogger(JsonReportGenerator.class.getName());
    private final ObjectMapper objectMapper;
    private boolean verboseOutput = false;
    
    /**
     * Creates a new JSON report generator.
     */
    public JsonReportGenerator() {
        this.objectMapper = new ObjectMapper();
        // Configure for pretty printing and readable dates
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }
    
    @Override
    public void setVerboseOutput(boolean verbose) {
        this.verboseOutput = verbose;
    }
    
    @Override
    public Optional<Path> generateReport(ConversionReport report, Path outputPath) {
        try {
            // Ensure the destination directory exists
            Files.createDirectories(outputPath.getParent());
            
            // Write the JSON report
            objectMapper.writeValue(outputPath.toFile(), report);
            
            LOG.log(Level.INFO, "JSON report generated successfully at {0}", outputPath);
            return Optional.of(outputPath);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to generate JSON report: " + e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Get the content type for JSON reports
     * 
     * @return The content type string
     */
    public String getContentType() {
        return "application/json";
    }
    
    @Override
    public String getFileExtension() {
        return "json";
    }
    
    @Override
    public String getReportName() {
        return "JSON Report";
    }
}