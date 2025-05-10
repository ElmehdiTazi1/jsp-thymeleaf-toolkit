package com.cybernostics.jsp2thymeleaf.reporting.examples;

import com.cybernostics.jsp2thymeleaf.reporting.ReportingFramework;
import com.cybernostics.jsp2thymeleaf.reporting.api.ReportCollector;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.cybernostics.jsp2thymeleaf.reporting.model.FileConversionSummary;
import com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletInfo;
import com.cybernostics.jsp2thymeleaf.reporting.model.TagConversionInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Example demonstrating how to use the reporting framework.
 * This class shows all the steps of collecting and generating conversion reports.
 */
public class ReportingExample {
    
    public static void main(String[] args) {
        try {
            // Sample conversion scenario
            runSampleConversion();
            
            System.out.println("Reports generated successfully. Check the 'reports' directory.");
        } catch (IOException e) {
            System.err.println("Error generating reports: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Simulates a JSP to Thymeleaf conversion and generates reports.
     * 
     * @throws IOException If an error occurs during report generation
     */
    private static void runSampleConversion() throws IOException {
        // Step 1: Initialize the report
        ConversionReport report = ReportingFramework.initializeReport("Sample Project", "1.0.0");
        report.setConverterVersion("2.0.0");
        
        // Add configuration information
        report.addConverterConfigurationItem("taglib.mapping.jstl", "th");
        report.addConverterConfigurationItem("scriptlet.mode", "extract");
        report.addConverterConfigurationItem("output.directory", "./converted");
        
        // Get the report collector for convenience
        ReportCollector collector = ReportingFramework.getReportCollector();
        
        // Step 2: Process some sample files
        processUserPage(collector);
        processProductListPage(collector);
        processCheckoutPage(collector);
        
        // Step 3: Complete the report
        report = collector.completeReport();
        
        // Step 4: Generate reports in all supported formats
        List<Path> generatedReports = ReportingFramework.generateReports(report, "./reports");
        
        // Print report paths
        System.out.println("Generated reports:");
        for (Path path : generatedReports) {
            System.out.println("- " + path);
        }
    }
    
    /**
     * Simulates processing a user profile page.
     * 
     * @param collector The report collector
     */
    private static void processUserPage(ReportCollector collector) {
        String filePath = "src/main/webapp/WEB-INF/views/user/profile.jsp";
        
        // Start file conversion
        FileConversionSummary summary = collector.startFileConversion(filePath);
        
        // Record some tag conversions
        TagConversionInfo formTag = new TagConversionInfo(filePath, 25, "form", "form");
        formTag.setStatus(TagConversionInfo.ConversionStatus.SUCCESS);
        formTag.setThymeleafEquivalent("th:action, th:object");
        formTag.setOriginalMarkup("<form:form method=\"post\" action=\"/user/update\" modelAttribute=\"user\">");
        formTag.setConvertedMarkup("<form method=\"post\" th:action=\"@{/user/update}\" th:object=\"${user}\">");
        collector.recordTagConversion(formTag);
        
        TagConversionInfo inputTag = new TagConversionInfo(filePath, 28, "input", "form");
        inputTag.setStatus(TagConversionInfo.ConversionStatus.SUCCESS);
        inputTag.setThymeleafEquivalent("th:field");
        inputTag.setOriginalMarkup("<form:input path=\"username\" />"); 
        inputTag.setConvertedMarkup("<input type=\"text\" th:field=\"*{username}\" />");
        collector.recordTagConversion(inputTag);
        
        // Record a successful scriptlet conversion
        ScriptletInfo scriptlet = new ScriptletInfo(filePath, 15, 
                "<% if (user.isAdmin()) { %>", ScriptletInfo.ScriptletAction.CONVERTED);
        scriptlet.setProcessedContent("<div th:if=\"${user.admin}\">");
        collector.recordScriptletProcessing(scriptlet);
        
        // Complete file conversion
        summary.setOriginalSize("4.2 KB");
        summary.setConvertedSize("3.8 KB");
        collector.completeFileConversion(summary);
    }
    
    /**
     * Simulates processing a product list page.
     * 
     * @param collector The report collector
     */
    private static void processProductListPage(ReportCollector collector) {
        String filePath = "src/main/webapp/WEB-INF/views/product/list.jsp";
        
        // Start file conversion
        FileConversionSummary summary = collector.startFileConversion(filePath);
        
        // Record some tag conversions
        TagConversionInfo foreachTag = new TagConversionInfo(filePath, 32, "forEach", "c");
        foreachTag.setStatus(TagConversionInfo.ConversionStatus.SUCCESS);
        foreachTag.setThymeleafEquivalent("th:each");
        foreachTag.setOriginalMarkup("<c:forEach items=\"${products}\" var=\"product\">");
        foreachTag.setConvertedMarkup("<tr th:each=\"product : ${products}\">");
        collector.recordTagConversion(foreachTag);
        
        TagConversionInfo ifTag = new TagConversionInfo(filePath, 40, "if", "c");
        ifTag.setStatus(TagConversionInfo.ConversionStatus.SUCCESS);
        ifTag.setThymeleafEquivalent("th:if");
        ifTag.setOriginalMarkup("<c:if test=\"${product.inStock}\">");
        ifTag.setConvertedMarkup("<span th:if=\"${product.inStock}\">");
        collector.recordTagConversion(ifTag);
        
        // Record an issue
        ConversionIssue issue = new ConversionIssue(filePath, 45, 
                "Unsupported custom tag: <xyz:paginate>", ConversionIssue.Severity.WARNING);
        collector.recordIssue(issue);
        
        // Complete file conversion
        summary.setOriginalSize("6.7 KB");
        summary.setConvertedSize("5.9 KB");
        collector.completeFileConversion(summary);
    }
    
    /**
     * Simulates processing a checkout page with some conversion failures.
     * 
     * @param collector The report collector
     */
    private static void processCheckoutPage(ReportCollector collector) {
        String filePath = "src/main/webapp/WEB-INF/views/checkout/payment.jsp";
        
        // Start file conversion
        FileConversionSummary summary = collector.startFileConversion(filePath);
        
        // Record some tag conversions
        TagConversionInfo outTag = new TagConversionInfo(filePath, 18, "out", "c");
        outTag.setStatus(TagConversionInfo.ConversionStatus.SUCCESS);
        outTag.setThymeleafEquivalent("th:text");
        outTag.setOriginalMarkup("<c:out value=\"${order.total}\" />");
        outTag.setConvertedMarkup("<span th:text=\"${order.total}\"></span>");
        collector.recordTagConversion(outTag);
        
        // Record a failed tag conversion
        TagConversionInfo customTag = new TagConversionInfo(filePath, 55, "paymentMethod", "payment");
        customTag.setStatus(TagConversionInfo.ConversionStatus.UNSUPPORTED);
        customTag.setOriginalMarkup("<payment:paymentMethod options=\"${paymentOptions}\" />");
        collector.recordTagConversion(customTag);
        
        // Record a conversion issue
        ConversionIssue issue = new ConversionIssue(filePath, 55, 
                "Unsupported tag: <payment:paymentMethod>", ConversionIssue.Severity.ERROR);
        collector.recordIssue(issue);
        
        // Record a failed scriptlet
        ScriptletInfo scriptlet = new ScriptletInfo(filePath, 65, 
                "<% PaymentProcessor.validate(request); %>", ScriptletInfo.ScriptletAction.FAILED);
        scriptlet.setConversionNotes("Contains direct Java code that cannot be automatically converted.");
        collector.recordScriptletProcessing(scriptlet);
        
        // Issue related to scriptlet
        ConversionIssue scriptletIssue = new ConversionIssue(filePath, 65, 
                "Complex scriptlet requires manual conversion", ConversionIssue.Severity.ERROR);
        collector.recordIssue(scriptletIssue);
        
        // Complete file conversion
        summary.setOriginalSize("8.3 KB");
        summary.setConvertedSize("7.1 KB");
        collector.completeFileConversion(summary);
    }
}