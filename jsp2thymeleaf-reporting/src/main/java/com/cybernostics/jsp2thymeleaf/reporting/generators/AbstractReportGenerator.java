package com.cybernostics.jsp2thymeleaf.reporting.generators;

import com.cybernostics.jsp2thymeleaf.reporting.api.ReportGenerator;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class for report generators that provides common functionality.
 * Follows Template Method pattern for generating reports with specific implementations.
 * 
 */
public abstract class AbstractReportGenerator implements ReportGenerator {
    
    private static final Logger LOG = Logger.getLogger(AbstractReportGenerator.class.getName());
    protected boolean verboseOutput = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVerboseOutput(boolean verbose) {
        this.verboseOutput = verbose;
    }

    @Override
    public Optional<Path> generateReport(ConversionReport report, Path outputPath) {
        try {
            // Create directories if they don't exist
            Files.createDirectories(outputPath.getParent());
            
            // Generate the report content
            byte[] reportContent = generateReportContent(report);
            
            // Write the content to the file
            Files.write(outputPath, reportContent);
            
            LOG.log(Level.INFO, "Report generated successfully at {0}", outputPath);
            return Optional.of(outputPath);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to generate report: " + e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Template method to be implemented by concrete generators
     * for creating the actual report content.
     * 
     * @param report The conversion report data
     * @return The report content as a byte array
     * @throws IOException If an I/O error occurs
     */
    protected abstract byte[] generateReportContent(ConversionReport report) throws IOException;
}