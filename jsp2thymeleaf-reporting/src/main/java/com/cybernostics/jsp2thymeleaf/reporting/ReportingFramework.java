package com.cybernostics.jsp2thymeleaf.reporting;

import com.cybernostics.jsp2thymeleaf.reporting.api.ReportCollector;
import com.cybernostics.jsp2thymeleaf.reporting.api.ReportGenerator;
import com.cybernostics.jsp2thymeleaf.reporting.api.ReportManager;
import com.cybernostics.jsp2thymeleaf.reporting.impl.DefaultReportManager;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Factory class that provides entry points to the reporting framework.
 * This follows the Factory Pattern to encapsulate the creation of complex objects.
 */
public class ReportingFramework {
    
    private static volatile ReportManager reportManager;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Gets the report manager instance (singleton).
     * 
     * @return The report manager instance
     */
    public static ReportManager getReportManager() {
        ReportManager result = reportManager;
        if (result == null) {
            lock.writeLock().lock();
            try {
                result = reportManager;
                if (result == null) {
                    reportManager = result = new DefaultReportManager();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        return result;
    }
    
    /**
     * Sets a custom report manager.
     * 
     * @param customReportManager The custom report manager
     */
    public static void setReportManager(ReportManager customReportManager) {
        lock.writeLock().lock();
        try {
            reportManager = customReportManager;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Gets the report collector for gathering conversion data.
     * 
     * @return The report collector
     */
    public static ReportCollector getReportCollector() {
        return getReportManager().getReportCollector();
    }
    
    /**
     * Registers a new report generator.
     * 
     * @param generator The report generator
     */
    public static void registerReportGenerator(ReportGenerator generator) {
        getReportManager().registerReportGenerator(generator);
    }
    
    /**
     * Initializes a new conversion report.
     * 
     * @param projectName Name of the project
     * @param projectVersion Version of the project
     * @return The initialized report
     */
    public static ConversionReport initializeReport(String projectName, String projectVersion) {
        return getReportCollector().initializeReport(projectName, projectVersion);
    }
    
    /**
     * Completes the current report.
     * 
     * @return The completed report
     */
    public static ConversionReport completeReport() {
        return getReportCollector().completeReport();
    }
    
    /**
     * Generates all supported report formats.
     * 
     * @param report The report to generate
     * @param outputDirectory Directory for the generated reports
     * @return List of paths to the generated reports
     * @throws IOException If an error occurs during report generation
     */
    public static List<Path> generateReports(ConversionReport report, String outputDirectory) throws IOException {
        return getReportManager().generateReports(report, Paths.get(outputDirectory));
    }
    
    /**
     * Generates a report in a specific format.
     * 
     * @param report The report to generate
     * @param outputDirectory Directory for the generated report
     * @param format The report format
     * @return Path to the generated report
     * @throws IOException If an error occurs during report generation
     */
    public static Path generateReport(ConversionReport report, String outputDirectory, String format) throws IOException {
        return getReportManager().generateReport(report, Paths.get(outputDirectory), format);
    }
    
    /**
     * Checks if a specific report format is supported.
     * 
     * @param format The format to check
     * @return true if supported, false otherwise
     */
    public static boolean isFormatSupported(String format) {
        return getReportManager().isFormatSupported(format);
    }
    
    /**
     * Lists all supported report formats.
     * 
     * @return List of supported format extensions
     */
    public static List<String> getSupportedFormats() {
        return getReportManager().getSupportedFormats();
    }
}