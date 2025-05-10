package com.cybernostics.jsp2thymeleaf.reporting.generators;

import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Generates HTML reports for JSP to Thymeleaf conversion data.
 * Uses Velocity template engine for HTML generation with embedded charts.
 * Follows Open/Closed principle by extending AbstractReportGenerator.
 * 
 * @author elmehdi.tazi
 */
public class HtmlReportGenerator extends AbstractReportGenerator {
    
    private static final String MAIN_TEMPLATE = "templates/report/main-report.vm";
    private final VelocityEngine velocityEngine;
    
    public HtmlReportGenerator() {
        this.velocityEngine = initializeVelocityEngine();
    }
    
    /**
     * Initialize the Velocity engine with needed settings
     * 
     * @return Configured VelocityEngine
     */
    private VelocityEngine initializeVelocityEngine() {
        Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        properties.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        properties.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        
        VelocityEngine engine = new VelocityEngine();
        engine.init(properties);
        return engine;
    }

    @Override
    protected byte[] generateReportContent(ConversionReport report) throws IOException {
        try {
            Template template = velocityEngine.getTemplate(MAIN_TEMPLATE, "UTF-8");
            VelocityContext context = createVelocityContext(report);
            
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IOException("Failed to generate HTML report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create a Velocity context with the report data and helper methods
     * 
     * @param report The conversion report
     * @return Populated VelocityContext
     */
    private VelocityContext createVelocityContext(ConversionReport report) {
        VelocityContext context = new VelocityContext();
        context.put("report", report);
        
        // Add formatter utilities
        context.put("formatter", new HtmlFormatUtils());
        
        // Add data for charts
        context.put("chartData", generateChartData(report));
        
        return context;
    }
    
    /**
     * Generate chart data in JavaScript format for the report charts
     * 
     * @param report The conversion report
     * @return JavaScript chart configurations as a string
     */
    private String generateChartData(ConversionReport report) {
        StringBuilder chartData = new StringBuilder();
        
        // Success Rate Pie Chart
        chartData.append("const successRateData = {\n")
                .append("  labels: ['Success', 'Failure'],\n")
                .append("  datasets: [{\n")
                .append("    data: [")
                .append(report.getSuccessfullyConvertedFiles()).append(", ")
                .append(report.getFilesWithErrors())
                .append("],\n")
                .append("    backgroundColor: ['#4CAF50', '#F44336']\n")
                .append("  }]\n")
                .append("};\n\n");
        
        // Scriptlet Types Pie Chart
        chartData.append("const scriptletTypesData = {\n")
                .append("  labels: ['Expression', 'Scriptlet', 'Declaration', 'Comment'],\n")
                .append("  datasets: [{\n")
                .append("    data: [")
                .append(report.getScriptletSummary().getCountForType(
                        com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletSummary.ScriptletType.EXPRESSION)).append(", ")
                .append(report.getScriptletSummary().getCountForType(
                        com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletSummary.ScriptletType.SCRIPTLET)).append(", ")
                .append(report.getScriptletSummary().getCountForType(
                        com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletSummary.ScriptletType.DECLARATION)).append(", ")
                .append(report.getScriptletSummary().getCountForType(
                        com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletSummary.ScriptletType.COMMENT))
                .append("],\n")
                .append("    backgroundColor: ['#2196F3', '#FFC107', '#9C27B0', '#607D8B']\n")
                .append("  }]\n")
                .append("};\n\n");
        
        // Issues by Severity
        chartData.append("const issuesBySeverityData = {\n")
                .append("  labels: ['Critical', 'Error', 'Warning', 'Info'],\n")
                .append("  datasets: [{\n")
                .append("    data: [")
                .append(countIssuesBySeverity(report, 
                        com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue.Severity.CRITICAL)).append(", ")
                .append(countIssuesBySeverity(report,
                        com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue.Severity.ERROR)).append(", ")
                .append(countIssuesBySeverity(report,
                        com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue.Severity.WARNING)).append(", ")
                .append(countIssuesBySeverity(report,
                        com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue.Severity.INFO))
                .append("],\n")
                .append("    backgroundColor: ['#d32f2f', '#f57c00', '#ffeb3b', '#2196F3']\n")
                .append("  }]\n")
                .append("};\n");
        
        return chartData.toString();
    }
    
    /**
     * Count issues by severity in the report
     * 
     * @param report The conversion report
     * @param severity The severity level to count
     * @return Number of issues with that severity
     */
    private int countIssuesBySeverity(ConversionReport report, 
            com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue.Severity severity) {
        return (int) report.getConversionIssues().stream()
                .filter(issue -> issue.getSeverity() == severity)
                .count();
    }

    @Override
    public String getFileExtension() {
        return "html";
    }

    @Override
    public String getReportName() {
        return "HTML Report";
    }
    
    /**
     * Utility class to format values in the HTML template
     */
    public static class HtmlFormatUtils {
        /**
         * Format a duration in milliseconds to a human-readable string
         * 
         * @param durationMillis Duration in milliseconds
         * @return Formatted duration string
         */
        public String formatDuration(long durationMillis) {
            long seconds = durationMillis / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            
            if (minutes > 0) {
                return minutes + " min " + seconds + " sec";
            } else {
                return seconds + " seconds";
            }
        }
        
        /**
         * Format a percentage with specified decimal places
         * 
         * @param value The percentage value (0-100)
         * @param decimalPlaces Number of decimal places
         * @return Formatted percentage string
         */
        public String formatPercentage(double value, int decimalPlaces) {
            if (decimalPlaces <= 0) {
                return String.format("%.0f%%", value);
            }
            String format = "%." + decimalPlaces + "f%%";
            return String.format(format, value);
        }
        
        /**
         * Escape HTML special characters
         * 
         * @param text The text to escape
         * @return HTML-safe text
         */
        public String escapeHtml(String text) {
            if (text == null) {
                return "";
            }
            return text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
        }
        
        /**
         * Get CSS class for an issue severity
         * 
         * @param severity The issue severity
         * @return CSS class name
         */
        public String getSeverityClass(com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue.Severity severity) {
            switch (severity) {
                case CRITICAL: return "severity-critical";
                case ERROR: return "severity-error";
                case WARNING: return "severity-warning";
                case INFO: return "severity-info";
                default: return "";
            }
        }
    }
}