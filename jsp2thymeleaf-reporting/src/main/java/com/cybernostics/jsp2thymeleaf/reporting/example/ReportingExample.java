package com.cybernostics.jsp2thymeleaf.reporting.example;

import com.cybernostics.jsp2thymeleaf.reporting.factory.ReportGeneratorFactory;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.cybernostics.jsp2thymeleaf.reporting.model.FileConversionSummary;
import com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletSummary;
import com.cybernostics.jsp2thymeleaf.reporting.model.TagConversionSummary;
import com.cybernostics.jsp2thymeleaf.reporting.service.ReportingService;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Example class demonstrating how to use the reporting system.
 * This class shows how to create sample data and generate reports
 * in different formats.
 * 
 * @author elmehdi.tazi
 */
public class ReportingExample {
    
    public static void main(String[] args) {
        // Create a sample conversion report with mock data
        ConversionReport report = createSampleReport();
        
        // Initialize reporting service with all available generators
        ReportingService reportingService = new ReportingService();
        ReportGeneratorFactory.createAllGenerators()
                .forEach(reportingService::registerGenerator);
        
        // Define output directory for reports
        Path outputDir = Paths.get("target/reports");
        
        // Generate all reports
        System.out.println("Generating reports in " + outputDir.toAbsolutePath());
        List<Path> generatedReports = reportingService.generateReportsParallel(report, outputDir);
        
        // Display generated report paths
        System.out.println("\nGenerated reports:");
        generatedReports.forEach(path -> System.out.println("- " + path));
        
        // Clean up resources
        reportingService.shutdown();
    }
    
    /**
     * Creates a sample ConversionReport with mock data for demonstration purposes
     * 
     * @return A sample conversion report
     */
    private static ConversionReport createSampleReport() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(5);
        LocalDateTime endTime = LocalDateTime.now();
        
        // Create script summary
        ScriptletSummary scriptletSummary = new ScriptletSummary();
        scriptletSummary.incrementTotal();
        scriptletSummary.incrementTotal();
        scriptletSummary.incrementTotal();
        scriptletSummary.incrementSuccessful();
        scriptletSummary.incrementSuccessful();
        scriptletSummary.incrementFailed();
        scriptletSummary.incrementByType(ScriptletSummary.ScriptletType.EXPRESSION);
        scriptletSummary.incrementByType(ScriptletSummary.ScriptletType.EXPRESSION);
        scriptletSummary.incrementByType(ScriptletSummary.ScriptletType.SCRIPTLET);
        scriptletSummary.incrementByStrategy(ScriptletSummary.ScriptletHandlingStrategy.CONVERT_TO_THYMELEAF);
        scriptletSummary.incrementByStrategy(ScriptletSummary.ScriptletHandlingStrategy.CONVERT_TO_THYMELEAF);
        scriptletSummary.incrementByStrategy(ScriptletSummary.ScriptletHandlingStrategy.HTML_COMMENT);
        
        // Create a sample conversion report
        ConversionReport.Builder reportBuilder = ConversionReport.builder()
                .withProjectName("Sample JSP to Thymeleaf Conversion")
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withTotalDuration(Duration.between(startTime, endTime))
                .withTotalFilesProcessed(3)
                .withSuccessfullyConvertedFiles(2)
                .withFilesWithErrors(1)
                .withScriptletSummary(scriptletSummary);
        
        // Add file summaries
        reportBuilder.addFileSummary(
                FileConversionSummary.builder()
                        .withSourcePath(Paths.get("src/main/webapp/WEB-INF/views/home.jsp"))
                        .withDestinationPath(Paths.get("src/main/webapp/WEB-INF/views/home.html"))
                        .withConversionStartTime(startTime)
                        .withConversionEndTime(startTime.plusSeconds(30))
                        .withConversionSuccessful(true)
                        .withTagsConverted(5)
                        .withScriptletsProcessed(1)
                        .build()
        );
        
        reportBuilder.addFileSummary(
                FileConversionSummary.builder()
                        .withSourcePath(Paths.get("src/main/webapp/WEB-INF/views/user/profile.jsp"))
                        .withDestinationPath(Paths.get("src/main/webapp/WEB-INF/views/user/profile.html"))
                        .withConversionStartTime(startTime.plusSeconds(35))
                        .withConversionEndTime(startTime.plusSeconds(50))
                        .withConversionSuccessful(true)
                        .withTagsConverted(8)
                        .withScriptletsProcessed(2)
                        .build()
        );
        
        // Add a file with conversion errors
        FileConversionSummary errorFileSummary = FileConversionSummary.builder()
                .withSourcePath(Paths.get("src/main/webapp/WEB-INF/views/admin/dashboard.jsp"))
                .withDestinationPath(Paths.get("src/main/webapp/WEB-INF/views/admin/dashboard.html"))
                .withConversionStartTime(startTime.plusSeconds(55))
                .withConversionEndTime(startTime.plusSeconds(65))
                .withConversionSuccessful(false)
                .withTagsConverted(3)
                .withScriptletsProcessed(0)
                .build();
        
        // Add issues
        ConversionIssue error = ConversionIssue.builder()
                .withIssueId("ERR-001")
                .withFilePath(Paths.get("src/main/webapp/WEB-INF/views/admin/dashboard.jsp"))
                .withLineNumber(23)
                .withColumnNumber(5)
                .withSeverity(ConversionIssue.Severity.ERROR)
                .withMessage("Unable to convert custom tag 'admin:panel'")
                .withSuggestion("Create a Thymeleaf fragment or component to replace this custom tag")
                .withIssueType("CustomTagConversion")
                .build();
        
        reportBuilder.addConversionIssue(error);
        
        // Add warning
        ConversionIssue warning = ConversionIssue.builder()
                .withIssueId("WARN-001")
                .withFilePath(Paths.get("src/main/webapp/WEB-INF/views/user/profile.jsp"))
                .withLineNumber(15)
                .withColumnNumber(10)
                .withSeverity(ConversionIssue.Severity.WARNING)
                .withMessage("Complex EL expression detected in JSP")
                .withSuggestion("Consider simplifying expression or moving logic to controller")
                .withIssueType("ComplexExpression")
                .build();
        
        reportBuilder.addConversionIssue(warning);
        
        // Add info level issue
        ConversionIssue info = ConversionIssue.builder()
                .withIssueId("INFO-001")
                .withFilePath(Paths.get("src/main/webapp/WEB-INF/views/home.jsp"))
                .withLineNumber(8)
                .withColumnNumber(3)
                .withSeverity(ConversionIssue.Severity.INFO)
                .withMessage("JSP include directive converted to Thymeleaf fragment")
                .withIssueType("IncludeDirective")
                .build();
        
        reportBuilder.addConversionIssue(info);
        
        // Add tag conversion summaries
        reportBuilder.addTagConversionSummary(
                "c:forEach",
                TagConversionSummary.builder()
                        .withTagName("forEach")
                        .withTagNamespace("c")
                        .withTargetTagName("each")
                        .withTargetTagNamespace("th")
                        .withConversionCount(7)
                        .withSuccessfulConversions(7)
                        .withFailedConversions(0)
                        .build()
        );
        
        reportBuilder.addTagConversionSummary(
                "c:if",
                TagConversionSummary.builder()
                        .withTagName("if")
                        .withTagNamespace("c")
                        .withTargetTagName("if")
                        .withTargetTagNamespace("th")
                        .withConversionCount(5)
                        .withSuccessfulConversions(4)
                        .withFailedConversions(1)
                        .build()
        );
        
        reportBuilder.addTagConversionSummary(
                "admin:panel",
                TagConversionSummary.builder()
                        .withTagName("panel")
                        .withTagNamespace("admin")
                        .withTargetTagName("replace")
                        .withTargetTagNamespace("th")
                        .withConversionCount(1)
                        .withSuccessfulConversions(0)
                        .withFailedConversions(1)
                        .build()
        );
        
        return reportBuilder.build();
    }
}