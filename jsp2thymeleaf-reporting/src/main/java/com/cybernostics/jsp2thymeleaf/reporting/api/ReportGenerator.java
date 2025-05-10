package com.cybernostics.jsp2thymeleaf.reporting.api;

import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Interface for report generators following the Interface Segregation Principle.
 * 
 * @author elmehdi.tazi
 */
public interface ReportGenerator {
    
    /**
     * Generates a report based on the conversion data
     * 
     * @param report The conversion report data
     * @param outputPath The path where the report should be saved
     * @return An Optional containing the actual file path if successful, empty if failed
     */
    Optional<Path> generateReport(ConversionReport report, Path outputPath);
    
    /**
     * Get the file extension for this report type
     * 
     * @return The file extension (without the dot)
     */
    String getFileExtension();
    
    /**
     * Get a human-readable name for this report type
     * 
     * @return The report name
     */
    String getReportName();
    
    /**
     * Sets whether the report should include verbose details
     * 
     * @param verbose True to include detailed information, false for a summary
     */
    void setVerboseOutput(boolean verbose);
}