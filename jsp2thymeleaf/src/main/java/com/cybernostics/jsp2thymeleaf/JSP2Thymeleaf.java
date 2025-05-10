/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeleafFileConverter;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue;
import com.cybernostics.jsp2thymeleaf.reporting.service.ReportingService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;

/**
 * Application entry point for JSP2Thymeleaf when called from
 * the command line.
 */
public class JSP2Thymeleaf
{
    /**
     * Enum defining the different types of report generators available.
     * Each type corresponds to a specific output format.
     */
    public enum ReportGeneratorType {
        
        /**
         * HTML report format
         */
        HTML("html"),
        
        /**
         * JSON report format
         */
        JSON("json"),
        
        /**
         * Summary report format (text-based)
         */
        SUMMARY("txt");
        
        private final String fileExtension;
        
        /**
         * Creates a new report generator type
         * 
         * @param fileExtension The file extension for this report type
         */
        ReportGeneratorType(String fileExtension) {
            this.fileExtension = fileExtension;
        }
        
        /**
         * Gets the file extension associated with this report type
         * 
         * @return The file extension (without the dot)
         */
        public String getFileExtension() {
            return fileExtension;
        }
    }

    public static void main(String[] args) {
        final JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.parse(args);
        final JSP2Thymeleaf jsP2Thymeleaf = new JSP2Thymeleaf(config);
        List<JSP2ThymeLeafException> exceptions = jsP2Thymeleaf.run();
        
        // Generate reports if configured
        if (config.isGenerateReports()) {
            try {
                System.out.println("Generating conversion reports...");
                jsP2Thymeleaf.generateReports();
                System.out.println("Reports generated successfully in " + 
                    config.getReportsOutputDir().toAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error generating reports: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Print summary of conversion results
        if (!exceptions.isEmpty()) {
            System.err.println("\nJSP2Thymeleaf encountered " + exceptions.size() + " errors during conversion:");
            for (JSP2ThymeLeafException exception : exceptions) {
                System.err.println("- " + exception.getMessage());
            }
            System.exit(1);
        } else {
            System.out.println("\nJSP2Thymeleaf converted all files successfully.");
        }
    }

    private static final Logger logger = Logger.getLogger(JSP2Thymeleaf.class.getName());
    private JSP2ThymeleafConfiguration configuration;
    private Set<TokenisedFile> alreadyProcessed = new TreeSet<>();
    private List<JSP2ThymeLeafException> exceptions;
    private JSP2ThymeleafFileConverter converter;
    // For collecting conversion statistics
    private ConversionReport.Builder reportBuilder;
    private ConversionReport conversionReport;
    // Track counters separately to avoid accessing builder internal state
    private int successfullyConvertedFiles = 0;
    private int failedConversions = 0;

    public JSP2Thymeleaf(JSP2ThymeleafConfiguration configuration)
    {
        this.configuration = configuration;
        converter = new JSP2ThymeleafFileConverter(configuration);
        converter.setShowBanner(configuration.isShowBanner());
        exceptions = new ArrayList<>();
        
        // Initialize conversion report using Builder pattern
        reportBuilder = ConversionReport.builder()
            .withProjectName("JSP to Thymeleaf Conversion")
            .withProjectVersion(DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDateTime.now()))
            .withStartTime(LocalDateTime.now());
    }

    public List<JSP2ThymeLeafException> run()
    {
        long startTime = System.currentTimeMillis();
        
        tokenisedFilesMap = configuration
                .getFilesToProcess()
                .stream()
                .map(path -> new TokenisedFile(path, configuration.getRootFolder()))
                .sorted()
                .collect(toMap(TokenisedFile::getPath, it -> it));

        // Record basic statistics in the report builder
        reportBuilder.withTotalFilesProcessed(tokenisedFilesMap.size())
                    .addConverterConfigurationItem("sourceDirectory", configuration.getSrcFolder().toString())
                    .addConverterConfigurationItem("destinationDirectory", configuration.getDestFolder().toString());
        
        List<JSP2ThymeLeafException> exceptions = tokenisedFilesMap
                .values()
                .stream()
                .sorted()
                .flatMap(eachInputFile -> convertFile(eachInputFile).stream())
                .collect(Collectors.toList());
                
        // Record exceptions and completion time
        reportBuilder.withEndTime(LocalDateTime.now())
                    .withFilesWithErrors(exceptions.size())
                    .withSuccessfullyConvertedFiles(successfullyConvertedFiles)
                    .withFailedConversions(failedConversions)
                    .withTotalDuration(java.time.Duration.ofMillis(System.currentTimeMillis() - startTime));
        
        // Build the final report
        conversionReport = reportBuilder.build();
        
        // Save collected data for reporting if enabled
        if (configuration.isGenerateReports()) {
            saveConversionData();
        }
        
        return exceptions;
    }

    /**
     * Generates reports based on the collected conversion data
     */
    public void generateReports() {
        if (configuration.getReportsOutputDir() == null) {
            logger.log(Level.WARNING, "Reports output directory not configured. Cannot generate reports.");
            return;
        }
        
        ReportingService reportingService = new ReportingService();
        
        for (String reportType : configuration.getReportTypes()) {
            try {
                ReportGeneratorType type = ReportGeneratorType.valueOf(reportType.toUpperCase());
                Path outputPath = Paths.get(configuration.getReportsOutputDir().toString(), 
                        "conversion-report." + type.getFileExtension());
                
                logger.log(Level.INFO, "Generating " + type + " report to: " + outputPath);
                reportingService.generateReport(conversionReport, outputPath, configuration.isVerboseReports());
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Unknown report type: " + reportType + ". Skipping.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error generating " + reportType + " report", e);
            }
        }
    }
    
    /**
     * Saves conversion data to a file for later report generation
     */
    private void saveConversionData() {
        try {
            ReportingService reportingService = new ReportingService();
            Path dataPath = Paths.get(configuration.getReportsOutputDir().toString(), "conversion-data.json");
            reportingService.writeReportToJson(conversionReport, dataPath);
            logger.log(Level.INFO, "Saved conversion data to: " + dataPath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to save conversion data", e);
        }
    }

    private Map<Path, TokenisedFile> tokenisedFilesMap;

    private List<JSP2ThymeLeafException> convertFile(TokenisedFile eachInputFile)
    {
        return convertFile(eachInputFile, null);
    }

    private List<JSP2ThymeLeafException> convertFile(TokenisedFile fileToConvert, ScopedJSPConverters parentScope)
    {
        ScopedJSPConverters myConverter = new ScopedJSPConverters(parentScope);
        List<JSP2ThymeLeafException> exceptions = new ArrayList<>();
        if (!alreadyProcessed.contains(fileToConvert))
        {
            final File outputFilePath = configuration.getOutputPathFor(fileToConvert.getFilePath()).toFile();
            try
            {
                logger.log(Level.INFO, "JSP2Thymeleaf processing:" + fileToConvert.toString());
                exceptions.addAll(converter.convert(fileToConvert, outputFilePath, myConverter));
                List<TokenisedFile> includedFiles = fileToConvert.getIncludedPaths()
                        .stream()
                        .map(path -> tokenisedFilesMap.get(path))
                        .collect(Collectors.toList());
                exceptions.addAll(
                        includedFiles.stream()
                                .flatMap(includedFile -> convertFile(includedFile, myConverter).stream())
                                .collect(Collectors.toList()));
                                
                // Record file conversion result in the report
                if (exceptions.isEmpty()) {
                    successfullyConvertedFiles++;
                } else {
                    failedConversions++;
                    // Record file-specific exceptions
                    for (JSP2ThymeLeafException ex : exceptions) {
                        // Create conversion issue using builder pattern
                        ConversionIssue issue = ConversionIssue.builder()
                            .withFilePath(Paths.get(fileToConvert.getPath().toString()))
                            .withMessage(ex.getMessage())
                            .withSeverity(ConversionIssue.Severity.ERROR)
                            .build();
                        reportBuilder.addConversionIssue(issue);
                    }
                }
            } catch (Throwable exception)
            {
                exceptions.add(JSP2ThymeLeafException.jsp2ThymeLeafExceptionBuilder(exception).build());
                // Record unexpected exception in report
                failedConversions++;
                // Create conversion issue using builder pattern for unhandled exceptions
                ConversionIssue issue = ConversionIssue.builder()
                    .withFilePath(Paths.get(fileToConvert.getPath().toString()))
                    .withMessage(exception.getMessage())
                    .withSeverity(ConversionIssue.Severity.CRITICAL)
                    .withIssueType("FATAL")
                    .build();
                reportBuilder.addConversionIssue(issue);
            } finally
            {
                alreadyProcessed.add(fileToConvert);
            }
        }
        return exceptions;
    }
}
