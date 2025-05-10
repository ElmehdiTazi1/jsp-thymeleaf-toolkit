package com.cybernostics.jsp2thymeleaf.reporting.api;

import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface that provides a façade to the reporting system.
 * Follows Single Responsibility Principle by focusing on orchestrating the reporting process.
 */
public interface ReportManager {
    
    /**
     * Gets the report collector to gather conversion information.
     * 
     * @return The report collector
     */
    ReportCollector getReportCollector();
    
    /**
     * Registers a report generator to be used when generating reports.
     * 
     * @param generator The report generator to register
     */
    void registerReportGenerator(ReportGenerator generator);
    
    /**
     * Generates reports in all registered formats.
     * 
     * @param report The conversion report to generate reports for
     * @param outputDirectory The directory where reports should be saved
     * @return List of paths to the generated reports
     * @throws IOException If an error occurs during report generation
     */
    List<Path> generateReports(ConversionReport report, Path outputDirectory) throws IOException;
    
    /**
     * Generates a report in a specific format.
     * 
     * @param report The conversion report to generate a report for
     * @param outputDirectory The directory where the report should be saved
     * @param format The format extension (e.g., "html", "json", "xml")
     * @return Path to the generated report
     * @throws IOException If an error occurs during report generation
     * @throws IllegalArgumentException If the specified format is not supported
     */
    Path generateReport(ConversionReport report, Path outputDirectory, String format) 
            throws IOException, IllegalArgumentException;
    
    /**
     * Checks if a specific report format is supported.
     * 
     * @param format The format to check
     * @return true if supported, false otherwise
     */
    boolean isFormatSupported(String format);
    
    /**
     * Lists all supported report formats.
     * 
     * @return List of supported format extensions
     */
    List<String> getSupportedFormats();
}