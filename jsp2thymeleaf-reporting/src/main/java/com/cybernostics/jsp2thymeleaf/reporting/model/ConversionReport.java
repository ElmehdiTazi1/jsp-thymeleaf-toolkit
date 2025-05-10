package com.cybernostics.jsp2thymeleaf.reporting.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Main report model that contains all data about a JSP to Thymeleaf conversion process.
 * Follows the Single Responsibility Principle by focusing only on storing conversion data.
 * 
 */
public class ConversionReport {
    private String projectName;
    private String projectVersion;
    private String reportId;
    private String converterVersion;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration totalDuration;
    private int totalFilesProcessed;
    private int successfullyConvertedFiles;
    private int filesWithErrors;
    private int partiallyConverted;
    private int failedConversions;
    private List<FileConversionSummary> fileSummaries = new ArrayList<>();
    private List<ConversionIssue> conversionIssues = new ArrayList<>();
    private Map<String, TagConversionSummary> tagConversionSummaries = new HashMap<>();
    private Map<String, String> converterConfiguration = new HashMap<>();
    private ScriptletSummary scriptletSummary = new ScriptletSummary();
    
    // Builder pattern for immutability and easier construction
    private ConversionReport(Builder builder) {
        this.projectName = builder.projectName;
        this.projectVersion = builder.projectVersion;
        this.reportId = builder.reportId != null ? builder.reportId : UUID.randomUUID().toString();
        this.converterVersion = builder.converterVersion;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.totalDuration = builder.totalDuration;
        this.totalFilesProcessed = builder.totalFilesProcessed;
        this.successfullyConvertedFiles = builder.successfullyConvertedFiles;
        this.filesWithErrors = builder.filesWithErrors;
        this.partiallyConverted = builder.partiallyConverted;
        this.failedConversions = builder.failedConversions;
        this.fileSummaries.addAll(builder.fileSummaries);
        this.conversionIssues.addAll(builder.conversionIssues);
        this.tagConversionSummaries.putAll(builder.tagConversionSummaries);
        this.converterConfiguration.putAll(builder.converterConfiguration);
        this.scriptletSummary = builder.scriptletSummary;
    }
    
    public static class Builder {
        private String projectName = "JSP to Thymeleaf Conversion";
        private String projectVersion;
        private String reportId;
        private String converterVersion;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Duration totalDuration;
        private int totalFilesProcessed;
        private int successfullyConvertedFiles;
        private int filesWithErrors;
        private int partiallyConverted;
        private int failedConversions;
        private List<FileConversionSummary> fileSummaries = new ArrayList<>();
        private List<ConversionIssue> conversionIssues = new ArrayList<>();
        private Map<String, TagConversionSummary> tagConversionSummaries = new HashMap<>();
        private Map<String, String> converterConfiguration = new HashMap<>();
        private ScriptletSummary scriptletSummary = new ScriptletSummary();
        
        public Builder withProjectName(String projectName) {
            this.projectName = projectName;
            return this;
        }
        
        public Builder withProjectVersion(String projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }
        
        public Builder withReportId(String reportId) {
            this.reportId = reportId;
            return this;
        }
        
        public Builder withConverterVersion(String converterVersion) {
            this.converterVersion = converterVersion;
            return this;
        }
        
        public Builder withStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }
        
        public Builder withEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }
        
        public Builder withTotalDuration(Duration totalDuration) {
            this.totalDuration = totalDuration;
            return this;
        }
        
        public Builder withTotalFilesProcessed(int totalFilesProcessed) {
            this.totalFilesProcessed = totalFilesProcessed;
            return this;
        }
        
        public Builder withSuccessfullyConvertedFiles(int successfullyConvertedFiles) {
            this.successfullyConvertedFiles = successfullyConvertedFiles;
            return this;
        }
        
        public Builder withFilesWithErrors(int filesWithErrors) {
            this.filesWithErrors = filesWithErrors;
            return this;
        }
        
        public Builder withPartiallyConverted(int partiallyConverted) {
            this.partiallyConverted = partiallyConverted;
            return this;
        }
        
        public Builder withFailedConversions(int failedConversions) {
            this.failedConversions = failedConversions;
            return this;
        }
        
        public Builder withFileSummaries(List<FileConversionSummary> fileSummaries) {
            this.fileSummaries.addAll(fileSummaries);
            return this;
        }
        
        public Builder addFileSummary(FileConversionSummary fileSummary) {
            this.fileSummaries.add(fileSummary);
            return this;
        }
        
        public Builder withConversionIssues(List<ConversionIssue> conversionIssues) {
            this.conversionIssues.addAll(conversionIssues);
            return this;
        }
        
        public Builder addConversionIssue(ConversionIssue conversionIssue) {
            this.conversionIssues.add(conversionIssue);
            return this;
        }
        
        public Builder withTagConversionSummaries(Map<String, TagConversionSummary> tagConversionSummaries) {
            this.tagConversionSummaries.putAll(tagConversionSummaries);
            return this;
        }
        
        public Builder addTagConversionSummary(String tagKey, TagConversionSummary summary) {
            this.tagConversionSummaries.put(tagKey, summary);
            return this;
        }
        
        public Builder withConverterConfiguration(Map<String, String> converterConfiguration) {
            this.converterConfiguration.putAll(converterConfiguration);
            return this;
        }
        
        public Builder addConverterConfigurationItem(String key, String value) {
            this.converterConfiguration.put(key, value);
            return this;
        }
        
        public Builder withScriptletSummary(ScriptletSummary scriptletSummary) {
            this.scriptletSummary = scriptletSummary;
            return this;
        }
        
        public ConversionReport build() {
            // Calculate duration if not explicitly set
            if (totalDuration == null && startTime != null && endTime != null) {
                totalDuration = Duration.between(startTime, endTime);
            }
            
            // Calculate success/error counts if not explicitly set
            if (totalFilesProcessed == 0 && !fileSummaries.isEmpty()) {
                totalFilesProcessed = fileSummaries.size();
                successfullyConvertedFiles = (int) fileSummaries.stream()
                        .filter(FileConversionSummary::isConversionSuccessful)
                        .count();
                filesWithErrors = totalFilesProcessed - successfullyConvertedFiles;
            }
            
            return new ConversionReport(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }

    // Getters - no setters for immutability
    
    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }
    
    public String getReportId() {
        return reportId;
    }
    
    public String getConverterVersion() {
        return converterVersion;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Duration getTotalDuration() {
        return totalDuration;
    }

    public int getTotalFilesProcessed() {
        return totalFilesProcessed;
    }

    public int getSuccessfullyConvertedFiles() {
        return successfullyConvertedFiles;
    }
    
    public int getSuccessfullyConverted() {
        return successfullyConvertedFiles;
    }

    public int getFilesWithErrors() {
        return filesWithErrors;
    }
    
    public int getPartiallyConverted() {
        return partiallyConverted;
    }
    
    public int getFailedConversions() {
        return failedConversions;
    }

    public List<FileConversionSummary> getFileSummaries() {
        return fileSummaries;
    }

    public List<ConversionIssue> getConversionIssues() {
        return conversionIssues;
    }
    
    // Alias for getConversionIssues for backward compatibility
    public List<ConversionIssue> getIssues() {
        return conversionIssues;
    }

    public Map<String, TagConversionSummary> getTagConversionSummaries() {
        return tagConversionSummaries;
    }
    
    public Map<String, String> getConverterConfiguration() {
        return converterConfiguration;
    }
    
    public Map<String, FileConversionSummary> getFileConversionSummaries() {
        Map<String, FileConversionSummary> result = new HashMap<>();
        for (FileConversionSummary summary : fileSummaries) {
            result.put(summary.getFilePath(), summary);
        }
        return result;
    }

    public ScriptletSummary getScriptletSummary() {
        return scriptletSummary;
    }
    
    // Mutator methods (for compatibility with existing code)
    
    public void setConverterVersion(String converterVersion) {
        this.converterVersion = converterVersion;
    }
    
    public void addConverterConfigurationItem(String key, String value) {
        this.converterConfiguration.put(key, value);
    }
    
    public void incrementTagConversion(String tagName) {
        TagConversionSummary summary = tagConversionSummaries.getOrDefault(tagName, 
            new TagConversionSummary(tagName, tagName, 0, 0));
        summary.incrementConversionCount();
        tagConversionSummaries.put(tagName, summary);
    }
    
    // Setter methods - these were missing and causing the compile errors
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public void setTotalDuration(Duration totalDuration) {
        this.totalDuration = totalDuration;
    }
    
    public void setTotalFilesProcessed(int totalFilesProcessed) {
        this.totalFilesProcessed = totalFilesProcessed;
    }
    
    public void setSuccessfullyConvertedFiles(int successfullyConvertedFiles) {
        this.successfullyConvertedFiles = successfullyConvertedFiles;
    }
    
    public void setFailedConversions(int failedConversions) {
        this.failedConversions = failedConversions;
    }
    
    public void setFilesWithErrors(int filesWithErrors) {
        this.filesWithErrors = filesWithErrors;
    }
    
    public void setConverterConfiguration(Map<String, String> converterConfiguration) {
        this.converterConfiguration = converterConfiguration;
    }
    
    public void setIssues(List<ConversionIssue> issues) {
        this.conversionIssues = issues;
    }
    
    // Helper methods
    
    /**
     * Calculate the overall success rate of the conversion process
     * 
     * @return The success rate as a percentage (0-100)
     */
    public double getSuccessRate() {
        if (totalFilesProcessed == 0) {
            return 0;
        }
        return (double) successfullyConvertedFiles / totalFilesProcessed * 100;
    }
    
    /**
     * Get the total number of issues found during conversion
     * 
     * @return Total number of issues
     */
    public int getTotalIssuesFound() {
        return conversionIssues.size();
    }
    
    /**
     * Get the total number of tags converted during the process
     * 
     * @return Total tags converted
     */
    public int getTotalTagsConverted() {
        return tagConversionSummaries.values().stream()
                .mapToInt(TagConversionSummary::getConversionCount)
                .sum();
    }
    
    /**
     * Get the total number of scriptlets processed during conversion
     * 
     * @return Total scriptlets processed
     */
    public int getTotalScriptletsProcessed() {
        return scriptletSummary.getTotalProcessed();
    }
}