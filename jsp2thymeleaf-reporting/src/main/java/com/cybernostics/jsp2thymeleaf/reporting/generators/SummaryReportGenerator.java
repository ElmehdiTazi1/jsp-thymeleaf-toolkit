package com.cybernostics.jsp2thymeleaf.reporting.generators;

import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletSummary;
import com.cybernostics.jsp2thymeleaf.reporting.model.TagConversionSummary;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates a consolidated summary report for JSP to Thymeleaf conversion data.
 * Follows Interface Segregation and Dependency Inversion principles by
 * implementing a specialized report generator that focuses on providing
 * high-level insights and recommendations.
 * 
 * @author elmehdi.tazi
 */
public class SummaryReportGenerator extends AbstractReportGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_ISSUES_PER_CATEGORY = 5;

    @Override
    protected byte[] generateReportContent(ConversionReport report) throws IOException {
        StringBuilder content = new StringBuilder();
        appendHeader(content, report);
        appendOverallStatistics(content, report);
        appendIssuesSummary(content, report);
        appendTagConversionSummary(content, report);
        appendScriptletSummary(content, report);
        appendRecommendations(content, report);
        appendFooter(content);
        
        return content.toString().getBytes(StandardCharsets.UTF_8);
    }
    
    private void appendHeader(StringBuilder sb, ConversionReport report) {
        sb.append("# JSP to Thymeleaf Conversion Summary Report\n\n");
        sb.append("Generated: ").append(DATE_FORMATTER.format(report.getEndTime())).append("\n\n");
        sb.append("## Overview\n\n");
    }
    
    private void appendOverallStatistics(StringBuilder sb, ConversionReport report) {
        sb.append("* **Total Files Processed**: ").append(report.getTotalFilesProcessed()).append("\n");
        sb.append("* **Successfully Converted Files**: ").append(report.getSuccessfullyConvertedFiles())
                .append(" (").append(String.format("%.1f", report.getSuccessRate())).append("%)\n");
        sb.append("* **Files with Errors**: ").append(report.getFilesWithErrors()).append("\n");
        sb.append("* **Total Processing Time**: ").append(formatDuration(report.getTotalDuration().toMillis())).append("\n");
        sb.append("* **Total Tags Converted**: ").append(report.getTotalTagsConverted()).append("\n");
        sb.append("* **Total Scriptlets Processed**: ").append(report.getTotalScriptletsProcessed()).append("\n");
        sb.append("* **Total Issues Found**: ").append(report.getTotalIssuesFound()).append("\n\n");
    }
    
    private void appendIssuesSummary(StringBuilder sb, ConversionReport report) {
        sb.append("## Issues Summary\n\n");
        
        // Group issues by severity
        Map<ConversionIssue.Severity, List<ConversionIssue>> issuesBySeverity = report.getConversionIssues().stream()
                .collect(Collectors.groupingBy(ConversionIssue::getSeverity));
        
        // Display counts by severity
        sb.append("### Issues by Severity\n\n");
        for (ConversionIssue.Severity severity : ConversionIssue.Severity.values()) {
            List<ConversionIssue> issues = issuesBySeverity.getOrDefault(severity, new ArrayList<>());
            sb.append("* **").append(severity).append("**: ")
                    .append(issues.size()).append(" issues\n");
        }
        sb.append("\n");
        
        // Display top issues for each severity
        for (ConversionIssue.Severity severity : ConversionIssue.Severity.values()) {
            List<ConversionIssue> issues = issuesBySeverity.getOrDefault(severity, new ArrayList<>());
            if (!issues.isEmpty()) {
                sb.append("### Top ").append(severity).append(" Issues\n\n");
                
                List<ConversionIssue> topIssues = issues.stream()
                        .limit(MAX_ISSUES_PER_CATEGORY)
                        .collect(Collectors.toList());
                
                for (ConversionIssue issue : topIssues) {
                    sb.append("* **").append(issue.getFormattedLocation()).append("**: ")
                            .append(issue.getMessage());
                    
                    if (issue.getSuggestion() != null && !issue.getSuggestion().isEmpty()) {
                        sb.append(" - *Suggestion: ").append(issue.getSuggestion()).append("*");
                    }
                    
                    sb.append("\n");
                }
                
                if (issues.size() > MAX_ISSUES_PER_CATEGORY) {
                    sb.append("* ... and ").append(issues.size() - MAX_ISSUES_PER_CATEGORY)
                            .append(" more ").append(severity).append(" issues\n");
                }
                
                sb.append("\n");
            }
        }
    }
    
    private void appendTagConversionSummary(StringBuilder sb, ConversionReport report) {
        sb.append("## Tag Conversion Summary\n\n");
        
        // Sort tag summaries by conversion count (most frequent first)
        List<Map.Entry<String, TagConversionSummary>> sortedTags = report.getTagConversionSummaries().entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, TagConversionSummary>>comparingInt(
                        entry -> entry.getValue().getConversionCount()).reversed())
                .collect(Collectors.toList());
        
        if (sortedTags.isEmpty()) {
            sb.append("No tags were converted during this process.\n\n");
            return;
        }
        
        sb.append("| Tag | Target | Count | Success Rate |\n");
        sb.append("|-----|--------|-------|-------------|\n");
        
        for (Map.Entry<String, TagConversionSummary> entry : sortedTags) {
            TagConversionSummary summary = entry.getValue();
            sb.append("| ").append(summary.getQualifiedTagName())
                    .append(" | ").append(summary.getQualifiedTargetTagName())
                    .append(" | ").append(summary.getConversionCount())
                    .append(" | ").append(String.format("%.1f%%", summary.getSuccessRate()))
                    .append(" |\n");
        }
        
        sb.append("\n");
    }
    
    private void appendScriptletSummary(StringBuilder sb, ConversionReport report) {
        ScriptletSummary scriptletSummary = report.getScriptletSummary();
        sb.append("## Scriptlet Handling Summary\n\n");
        
        sb.append("* **Total Scriptlets Processed**: ").append(scriptletSummary.getTotalProcessed()).append("\n");
        sb.append("* **Successfully Handled**: ").append(scriptletSummary.getSuccessfullyHandled())
                .append(" (").append(String.format("%.1f%%", scriptletSummary.getSuccessRate())).append(")\n");
        sb.append("* **Failed to Handle**: ").append(scriptletSummary.getFailedToHandle()).append("\n\n");
        
        // By type
        sb.append("### Scriptlet Types\n\n");
        for (ScriptletSummary.ScriptletType type : ScriptletSummary.ScriptletType.values()) {
            int count = scriptletSummary.getCountForType(type);
            sb.append("* **").append(type).append("**: ").append(count).append(" scriptlet(s)\n");
        }
        sb.append("\n");
        
        // By strategy
        sb.append("### Handling Strategies Used\n\n");
        for (ScriptletSummary.ScriptletHandlingStrategy strategy : ScriptletSummary.ScriptletHandlingStrategy.values()) {
            int count = scriptletSummary.getCountForStrategy(strategy);
            if (count > 0) {
                sb.append("* **").append(strategy).append("**: ").append(count).append(" scriptlet(s)\n");
            }
        }
        sb.append("\n");
    }
    
    private void appendRecommendations(StringBuilder sb, ConversionReport report) {
        sb.append("## Recommendations\n\n");
        
        // Generate recommendations based on conversion statistics
        List<String> recommendations = generateRecommendations(report);
        
        for (String recommendation : recommendations) {
            sb.append("* ").append(recommendation).append("\n");
        }
        
        sb.append("\n");
    }
    
    private List<String> generateRecommendations(ConversionReport report) {
        List<String> recommendations = new ArrayList<>();
        
        // Overall success rate recommendations
        double successRate = report.getSuccessRate();
        if (successRate < 50) {
            recommendations.add("The overall conversion success rate is low (< 50%). Consider reviewing your JSP structure and making it more standardized before conversion.");
        } else if (successRate < 80) {
            recommendations.add("The conversion success rate is moderate. Focus on fixing the documented errors to improve results.");
        } else if (successRate < 100) {
            recommendations.add("Conversion is mostly successful. Review the remaining issues to achieve full conversion.");
        } else {
            recommendations.add("Conversion completed successfully for all files. Conduct thorough testing to ensure functional equivalence.");
        }
        
        // Tag conversion recommendations
        if (!report.getTagConversionSummaries().isEmpty()) {
            // Find tags with lowest success rates
            List<TagConversionSummary> problematicTags = report.getTagConversionSummaries().values().stream()
                    .filter(tag -> tag.getConversionCount() > 0 && tag.getSuccessRate() < 80)
                    .sorted(Comparator.comparingDouble(TagConversionSummary::getSuccessRate))
                    .limit(3)
                    .collect(Collectors.toList());
            
            if (!problematicTags.isEmpty()) {
                StringBuilder tagRecommendation = new StringBuilder("Focus on improving conversion for problematic tags: ");
                for (int i = 0; i < problematicTags.size(); i++) {
                    TagConversionSummary tag = problematicTags.get(i);
                    if (i > 0) {
                        tagRecommendation.append(", ");
                    }
                    tagRecommendation.append(tag.getQualifiedTagName())
                            .append(" (").append(String.format("%.1f%%", tag.getSuccessRate())).append(" success)");
                }
                recommendations.add(tagRecommendation.toString());
            }
        }
        
        // Scriptlet recommendations
        ScriptletSummary scriptletSummary = report.getScriptletSummary();
        if (scriptletSummary.getTotalProcessed() > 0) {
            if (scriptletSummary.getCountForType(ScriptletSummary.ScriptletType.SCRIPTLET) > 0) {
                recommendations.add("Your code contains " + 
                        scriptletSummary.getCountForType(ScriptletSummary.ScriptletType.SCRIPTLET) + 
                        " scriptlet blocks. Consider refactoring these into controller methods or Thymeleaf expressions.");
            }
            
            if (scriptletSummary.getCountForType(ScriptletSummary.ScriptletType.EXPRESSION) > 0) {
                recommendations.add("There are " + 
                        scriptletSummary.getCountForType(ScriptletSummary.ScriptletType.EXPRESSION) + 
                        " JSP expression blocks. These should be converted to Thymeleaf expressions using ${...} syntax.");
            }
            
            if (scriptletSummary.getCountForType(ScriptletSummary.ScriptletType.DECLARATION) > 0) {
                recommendations.add("Found " + 
                        scriptletSummary.getCountForType(ScriptletSummary.ScriptletType.DECLARATION) + 
                        " JSP declarations. These should be moved to controller classes or utility classes.");
            }
        }
        
        // Issue-based recommendations
        long criticalIssues = report.getConversionIssues().stream()
                .filter(issue -> issue.getSeverity() == ConversionIssue.Severity.CRITICAL)
                .count();
        
        if (criticalIssues > 0) {
            recommendations.add("Address the " + criticalIssues + " critical issues first, as they will prevent proper rendering.");
        }
        
        // Check for common issues patterns
        Map<String, Long> issueTypeCount = report.getConversionIssues().stream()
                .filter(issue -> issue.getIssueType() != null)
                .collect(Collectors.groupingBy(ConversionIssue::getIssueType, Collectors.counting()));
        
        issueTypeCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 3) // Threshold for "frequent" issues
                .forEach(entry -> {
                    recommendations.add("There are " + entry.getValue() + " issues of type '" + entry.getKey() + 
                            "'. Consider a systematic approach to fix these.");
                });
        
        return recommendations;
    }
    
    private void appendFooter(StringBuilder sb) {
        sb.append("---\n");
        sb.append("Generated by JSP2Thymeleaf Reporting Module\n");
    }
    
    private String formatDuration(long durationMillis) {
        long seconds = durationMillis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        if (minutes > 0) {
            return minutes + " min " + seconds + " sec";
        } else {
            return seconds + " seconds";
        }
    }

    @Override
    public String getFileExtension() {
        return "md";
    }

    @Override
    public String getReportName() {
        return "Summary Report";
    }
}