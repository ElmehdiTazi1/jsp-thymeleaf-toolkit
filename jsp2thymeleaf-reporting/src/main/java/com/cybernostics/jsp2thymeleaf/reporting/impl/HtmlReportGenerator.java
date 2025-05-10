package com.cybernostics.jsp2thymeleaf.reporting.impl;

import com.cybernostics.jsp2thymeleaf.reporting.api.ReportGenerator;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.cybernostics.jsp2thymeleaf.reporting.model.FileConversionSummary;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Generates HTML reports from conversion data.
 * Follows Single Responsibility Principle by focusing only on HTML report generation.
 */
public class HtmlReportGenerator implements ReportGenerator {
    
    private static final Logger LOG = Logger.getLogger(HtmlReportGenerator.class.getName());
    private final VelocityEngine velocityEngine;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private boolean verboseOutput = false;
    
    /**
     * Creates a new HTML report generator.
     */
    public HtmlReportGenerator() {
        Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        properties.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        
        this.velocityEngine = new VelocityEngine(properties);
        this.velocityEngine.init();
    }
    
    @Override
    public Optional<Path> generateReport(ConversionReport report, Path outputPath) {
        try {
            // Ensure the destination directory exists
            Files.createDirectories(outputPath.getParent());
            
            // Setup the Velocity context with the report data
            VelocityContext context = createVelocityContext(report);
            
            // Get the template
            Template template = velocityEngine.getTemplate("templates/report.html.vm", StandardCharsets.UTF_8.name());
            
            // Render the template to the output file
            try (Writer writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
                template.merge(context, writer);
            }
            
            LOG.log(Level.INFO, "HTML report generated successfully at {0}", outputPath);
            return Optional.of(outputPath);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to generate HTML report: " + e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Creates the Velocity context with all the data needed for the template.
     * 
     * @param report The conversion report
     * @return A context with all necessary data
     */
    private VelocityContext createVelocityContext(ConversionReport report) {
        VelocityContext context = new VelocityContext();
        context.put("report", report);
        context.put("dateFormatter", DATE_FORMATTER);
        
        // Group issues by severity for easier display
        Map<String, Long> issuesBySeverity = report.getIssues().stream()
                .collect(Collectors.groupingBy(
                        issue -> issue.getSeverity().name(),
                        Collectors.counting()));
        context.put("issuesBySeverity", issuesBySeverity);
        
        // Group files by conversion status
        Map<String, Long> filesByStatus = report.getFileConversionSummaries().values().stream()
                .collect(Collectors.groupingBy(
                        summary -> summary.getConversionStatus().name(),
                        Collectors.counting()));
        context.put("filesByStatus", filesByStatus);
        
        // Calculate conversion success rate
        double successRate = calculateSuccessRate(report);
        context.put("conversionSuccessRate", successRate);
        
        // Provide helper functions
        context.put("helper", new TemplateHelper());
        
        return context;
    }
    
    /**
     * Calculates the overall conversion success rate.
     * 
     * @param report The conversion report
     * @return Success rate as a percentage
     */
    private double calculateSuccessRate(ConversionReport report) {
        int totalTags = 0;
        int successfulTags = 0;
        
        for (FileConversionSummary summary : report.getFileConversionSummaries().values()) {
            totalTags += summary.getTotalJspTags();
            successfulTags += summary.getConvertedTags();
        }
        
        return totalTags > 0 ? (double) successfulTags / totalTags * 100.0 : 100.0;
    }
    
    /**
     * Inner helper class to provide utility methods for templates.
     */
    public static class TemplateHelper {
        
        /**
         * Converts a duration to a human-readable string.
         * 
         * @param millis Duration in milliseconds
         * @return Formatted duration string
         */
        public String formatDuration(long millis) {
            if (millis < 1000) {
                return millis + " ms";
            } 
            
            long seconds = millis / 1000;
            if (seconds < 60) {
                return seconds + " sec";
            }
            
            long minutes = seconds / 60;
            seconds = seconds % 60;
            if (minutes < 60) {
                return minutes + " min " + seconds + " sec";
            }
            
            long hours = minutes / 60;
            minutes = minutes % 60;
            return hours + " h " + minutes + " min";
        }
        
        /**
         * Returns a CSS class based on conversion status.
         * 
         * @param status The status string
         * @return CSS class name
         */
        public String getStatusClass(String status) {
            switch (status) {
                case "SUCCESS":
                    return "success";
                case "PARTIAL":
                    return "warning";
                case "FAILED":
                    return "danger";
                default:
                    return "secondary";
            }
        }
        
        /**
         * Returns a CSS class based on issue severity.
         * 
         * @param severity The severity string
         * @return CSS class name
         */
        public String getSeverityClass(String severity) {
            switch (severity) {
                case "INFO":
                    return "info";
                case "WARNING":
                    return "warning";
                case "ERROR":
                    return "danger";
                case "CRITICAL":
                    return "dark";
                default:
                    return "secondary";
            }
        }
    }
    
    public String getContentType() {
        return "text/html";
    }
    
    @Override
    public String getFileExtension() {
        return "html";
    }
    
    @Override
    public String getReportName() {
        return "HTML Report";
    }
    
    @Override
    public void setVerboseOutput(boolean verbose) {
        this.verboseOutput = verbose;
    }
}