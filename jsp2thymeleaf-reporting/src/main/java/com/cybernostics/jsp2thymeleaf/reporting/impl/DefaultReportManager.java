package com.cybernostics.jsp2thymeleaf.reporting.impl;

import com.cybernostics.jsp2thymeleaf.reporting.api.ReportCollector;
import com.cybernostics.jsp2thymeleaf.reporting.api.ReportGenerator;
import com.cybernostics.jsp2thymeleaf.reporting.api.ReportManager;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of the ReportManager interface that orchestrates the entire reporting process.
 * This class follows the Dependency Inversion Principle by depending on abstractions rather than concretions.
 */
public class DefaultReportManager implements ReportManager {
    
    private final ReportCollector reportCollector;
    private final Map<String, ReportGenerator> reportGenerators;
    
    /**
     * Creates a new DefaultReportManager with default implementations.
     */
    public DefaultReportManager() {
        this(new DefaultReportCollector());
    }
    
    /**
     * Creates a new DefaultReportManager with the specified report collector.
     * 
     * @param reportCollector The report collector to use
     */
    public DefaultReportManager(ReportCollector reportCollector) {
        this.reportCollector = reportCollector;
        this.reportGenerators = new HashMap<>();
        
        // Register default generators
        registerReportGenerator(new JsonReportGenerator());
        registerReportGenerator(new HtmlReportGenerator());
    }
    
    @Override
    public ReportCollector getReportCollector() {
        return reportCollector;
    }
    
    @Override
    public void registerReportGenerator(ReportGenerator generator) {
        reportGenerators.put(generator.getFileExtension(), generator);
    }
    
    @Override
    public List<Path> generateReports(ConversionReport report, Path outputDirectory) throws IOException {
        List<Path> generatedReports = new ArrayList<>();
        
        for (ReportGenerator generator : reportGenerators.values()) {
            Path reportPath = generateReport(report, outputDirectory, generator.getFileExtension());
            generatedReports.add(reportPath);
        }
        
        return generatedReports;
    }
    
    @Override
    public Path generateReport(ConversionReport report, Path outputDirectory, String format) throws IOException {
        // Ensure the output directory exists
        outputDirectory.toFile().mkdirs();
        
        // Get the appropriate generator
        ReportGenerator generator = reportGenerators.get(format.toLowerCase());
        if (generator == null) {
            throw new IllegalArgumentException("Unsupported report format: " + format);
        }
        
        // Generate a filename
        String filename = "conversion-report-" + 
                (report.getReportId() != null ? report.getReportId() : UUID.randomUUID().toString()) + 
                "." + format;
        Path outputPath = outputDirectory.resolve(filename);
        
        // Generate the report
        generator.generateReport(report, outputPath);
        
        return outputPath;
    }
    
    @Override
    public boolean isFormatSupported(String format) {
        return reportGenerators.containsKey(format.toLowerCase());
    }
    
    @Override
    public List<String> getSupportedFormats() {
        return new ArrayList<>(reportGenerators.keySet());
    }
}