package com.cybernostics.jsp2thymeleaf.maven;

import com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf;
import com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf.ReportGeneratorType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which generates reports for JSP to Thymeleaf conversion.
 * Can be used separately after conversion to analyze results.
 */
@Mojo(name = "report", defaultPhase = LifecyclePhase.SITE, requiresProject = true)
public class JSPReportMojo extends AbstractMojo {

    /**
     * Directory containing conversion data to report on
     */
    @Parameter(defaultValue = "${project.build.directory}/jsp2thymeleaf-data", property = "conversionDataDir", required = true)
    private File conversionDataDir;

    /**
     * Directory where reports will be generated
     */
    @Parameter(defaultValue = "${project.build.directory}/jsp2thymeleaf-reports", property = "reportsOutputDir", required = true)
    private File reportsOutputDir;

    /**
     * Types of reports to generate: html, json, summary
     */
    @Parameter(property = "reportTypes", required = false)
    private String[] reportTypes = {"html", "json", "summary"};

    /**
     * Project name to use in reports
     */
    @Parameter(defaultValue = "${project.name}", property = "projectName", required = false)
    private String projectName;

    /**
     * Project version to use in reports
     */
    @Parameter(defaultValue = "${project.version}", property = "projectVersion", required = false)
    private String projectVersion;

    /**
     * Include verbose details in reports
     */
    @Parameter(defaultValue = "false", property = "verboseReports", required = false)
    private Boolean verboseReports;

    public void execute() throws MojoExecutionException {
        final Log log = getLog();
        log.info("Generating JSP to Thymeleaf conversion reports");
        log.info("Report types: " + Arrays.toString(reportTypes));
        log.info("Reports output directory: " + reportsOutputDir.getAbsolutePath());
        log.info("Conversion data directory: " + conversionDataDir.getAbsolutePath());
        log.info("Project: " + projectName + " " + projectVersion);
        log.info("Verbose mode: " + verboseReports);
        
        // Create reports directory if it doesn't exist
        if (!reportsOutputDir.exists()) {
            reportsOutputDir.mkdirs();
        }

        try {
            // Check if data file exists
            Path jsonPath = Paths.get(conversionDataDir.toString(), "conversion-data.json");
            File jsonFile = jsonPath.toFile();
            
            if (!jsonFile.exists()) {
                throw new MojoExecutionException("No conversion data found in " + conversionDataDir.getAbsolutePath());
            }
            
            // Generate reports using the main JSP2Thymeleaf class
            for (String reportTypeName : reportTypes) {
                try {
                    ReportGeneratorType type = ReportGeneratorType.valueOf(reportTypeName.toUpperCase());
                    Path outputPath = Paths.get(reportsOutputDir.toString(), 
                            "conversion-report." + getFileExtension(type));
                    
                    log.info("Generating " + type + " report to: " + outputPath);
                    generateReport(jsonFile, outputPath, type, log);
                    
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown report type: " + reportTypeName + ". Skipping.");
                } catch (Exception e) {
                    log.error("Error generating " + reportTypeName + " report: " + e.getMessage(), e);
                }
            }
            
            log.info("Report generation completed successfully");
        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during report generation", e);
            throw new MojoExecutionException("Failed to generate reports: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get file extension for a report type
     */
    private String getFileExtension(ReportGeneratorType type) {
        switch (type) {
            case HTML:
                return "html";
            case JSON:
                return "json";
            case SUMMARY:
                return "txt";
            default:
                return "json";
        }
    }
    
    /**
     * Generate a report based on the conversion data and type
     */
    private void generateReport(File dataFile, Path outputPath, ReportGeneratorType type, Log log) throws IOException {
        try {
            // Create parent directory if it doesn't exist
            outputPath.getParent().toFile().mkdirs();
            
            // Use simple command line call to the main JSP2Thymeleaf class
            String[] args = {
                "-r", // generate reports flag
                "-rt", type.name().toLowerCase(), // report type
                "-ro", outputPath.getParent().toString(), // report output directory
                "--data-file", dataFile.getAbsolutePath(), // data file path
                "--project-name", projectName, // project name
                "--project-version", projectVersion, // project version
                verboseReports ? "-v" : "" // verbose flag if enabled
            };
            
            // Invoke main class or use direct processing depending on type
            switch (type) {
                case JSON:
                    // For JSON, just copy the data file
                    java.nio.file.Files.copy(dataFile.toPath(), outputPath);
                    break;
                default:
                    // For other types, use JSP2Thymeleaf's reporting mechanisms
                    ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", 
                        System.getProperty("java.class.path"), 
                        "com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf", 
                        String.join(" ", args));
                    
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();
                    
                    if (exitCode != 0) {
                        log.warn("Report generation process exited with code: " + exitCode);
                    }
                    break;
            }
        } catch (IOException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Report generation was interrupted", e);
        }
    }
}