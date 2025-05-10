package com.cybernostics.jsp2thymeleaf.reporting.model;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Summary of the conversion process for a single file.
 * Follows SRP by focusing only on file-level conversion data.
 * 
 */
public class FileConversionSummary {
    
    /**
     * Enum representing the status of file conversion
     */
    public enum ConversionStatus {
        SUCCESS,
        PARTIAL,
        FAILED,
        SKIPPED
    }
    
    private Path sourcePath;
    private Path destinationPath;
    private LocalDateTime conversionStartTime;
    private LocalDateTime conversionEndTime;
    private Duration conversionDuration;
    private boolean conversionSuccessful;
    private ConversionStatus conversionStatus;
    private List<ConversionIssue> issues = new ArrayList<>();
    private int scriptletsProcessed;
    private int tagsConverted;
    private int totalJspTags;
    private int convertedTags;
    private String originalSize;
    private String convertedSize;
    private int totalScriptlets;
    private int convertedScriptlets;
    private int commentedScriptlets;
    
    private FileConversionSummary(Builder builder) {
        this.sourcePath = builder.sourcePath;
        this.destinationPath = builder.destinationPath;
        this.conversionStartTime = builder.conversionStartTime;
        this.conversionEndTime = builder.conversionEndTime;
        this.conversionDuration = builder.conversionDuration;
        this.conversionSuccessful = builder.conversionSuccessful;
        this.conversionStatus = builder.conversionStatus;
        this.issues.addAll(builder.issues);
        this.scriptletsProcessed = builder.scriptletsProcessed;
        this.tagsConverted = builder.tagsConverted;
        this.totalJspTags = builder.totalJspTags;
        this.convertedTags = builder.convertedTags;
        this.originalSize = builder.originalSize;
        this.convertedSize = builder.convertedSize;
        this.totalScriptlets = builder.totalScriptlets;
        this.convertedScriptlets = builder.convertedScriptlets;
        this.commentedScriptlets = builder.commentedScriptlets;
    }
    
    public static class Builder {
        private Path sourcePath;
        private Path destinationPath;
        private LocalDateTime conversionStartTime;
        private LocalDateTime conversionEndTime;
        private Duration conversionDuration;
        private boolean conversionSuccessful;
        private ConversionStatus conversionStatus = ConversionStatus.SUCCESS; // Default status
        private List<ConversionIssue> issues = new ArrayList<>();
        private int scriptletsProcessed;
        private int tagsConverted;
        private int totalJspTags;
        private int convertedTags;
        private String originalSize;
        private String convertedSize;
        private int totalScriptlets;
        private int convertedScriptlets;
        private int commentedScriptlets;
        
        public Builder withSourcePath(Path sourcePath) {
            this.sourcePath = sourcePath;
            return this;
        }
        
        public Builder withDestinationPath(Path destinationPath) {
            this.destinationPath = destinationPath;
            return this;
        }
        
        public Builder withConversionStartTime(LocalDateTime startTime) {
            this.conversionStartTime = startTime;
            return this;
        }
        
        public Builder withConversionEndTime(LocalDateTime endTime) {
            this.conversionEndTime = endTime;
            return this;
        }
        
        public Builder withConversionDuration(Duration duration) {
            this.conversionDuration = duration;
            return this;
        }
        
        public Builder withConversionSuccessful(boolean successful) {
            this.conversionSuccessful = successful;
            return this;
        }
        
        public Builder withConversionStatus(ConversionStatus status) {
            this.conversionStatus = status;
            return this;
        }
        
        public Builder withIssues(List<ConversionIssue> issues) {
            this.issues.addAll(issues);
            return this;
        }
        
        public Builder addIssue(ConversionIssue issue) {
            this.issues.add(issue);
            return this;
        }
        
        public Builder withScriptletsProcessed(int count) {
            this.scriptletsProcessed = count;
            return this;
        }
        
        public Builder withTagsConverted(int count) {
            this.tagsConverted = count;
            return this;
        }
        
        public Builder withTotalJspTags(int count) {
            this.totalJspTags = count;
            return this;
        }
        
        public Builder withConvertedTags(int count) {
            this.convertedTags = count;
            return this;
        }
        
        public Builder withOriginalSize(String size) {
            this.originalSize = size;
            return this;
        }
        
        public Builder withConvertedSize(String size) {
            this.convertedSize = size;
            return this;
        }
        
        public Builder withTotalScriptlets(int count) {
            this.totalScriptlets = count;
            return this;
        }
        
        public Builder withConvertedScriptlets(int count) {
            this.convertedScriptlets = count;
            return this;
        }
        
        public Builder withCommentedScriptlets(int count) {
            this.commentedScriptlets = count;
            return this;
        }
        
        public FileConversionSummary build() {
            // Calculate duration if not explicitly set
            if (conversionDuration == null && conversionStartTime != null && conversionEndTime != null) {
                conversionDuration = Duration.between(conversionStartTime, conversionEndTime);
            }
            
            // Set conversion status based on successful flag if not explicitly set
            if (conversionSuccessful && conversionStatus == ConversionStatus.SUCCESS) {
                // Keep as SUCCESS
            } else if (!conversionSuccessful && conversionStatus == ConversionStatus.SUCCESS) {
                // Override with FAILED if marked unsuccessful but status is still SUCCESS
                conversionStatus = ConversionStatus.FAILED;
            }
            
            return new FileConversionSummary(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters - no setters for immutability
    
    public Path getSourcePath() {
        return sourcePath;
    }

    public Path getDestinationPath() {
        return destinationPath;
    }

    public LocalDateTime getConversionStartTime() {
        return conversionStartTime;
    }

    public LocalDateTime getConversionEndTime() {
        return conversionEndTime;
    }

    public Duration getConversionDuration() {
        return conversionDuration;
    }

    public boolean isConversionSuccessful() {
        return conversionSuccessful;
    }
    
    public ConversionStatus getConversionStatus() {
        return conversionStatus;
    }

    public List<ConversionIssue> getIssues() {
        return issues;
    }

    public int getScriptletsProcessed() {
        return scriptletsProcessed;
    }

    public int getTagsConverted() {
        return tagsConverted;
    }
    
    public int getTotalJspTags() {
        return totalJspTags;
    }
    
    public int getConvertedTags() {
        return convertedTags;
    }
    
    public String getOriginalSize() {
        return originalSize;
    }
    
    public String getConvertedSize() {
        return convertedSize;
    }
    
    public int getTotalScriptlets() {
        return totalScriptlets;
    }
    
    public int getConvertedScriptlets() {
        return convertedScriptlets;
    }
    
    public int getCommentedScriptlets() {
        return commentedScriptlets;
    }
    
    // Methods for backward compatibility
    
    public void setOriginalSize(String size) {
        this.originalSize = size;
    }
    
    public void setConvertedSize(String size) {
        this.convertedSize = size;
    }
    
    public String getFilePath() {
        return sourcePath != null ? sourcePath.toString() : "";
    }
    
    public boolean hasIssues() {
        return !issues.isEmpty();
    }
    
    public int getIssueCount() {
        return issues.size();
    }
    
    public int getCriticalIssueCount() {
        return (int) issues.stream()
                .filter(issue -> issue.getSeverity() == ConversionIssue.Severity.CRITICAL)
                .count();
    }
    
    public int getErrorIssueCount() {
        return (int) issues.stream()
                .filter(issue -> issue.getSeverity() == ConversionIssue.Severity.ERROR)
                .count();
    }
    
    public int getWarningIssueCount() {
        return (int) issues.stream()
                .filter(issue -> issue.getSeverity() == ConversionIssue.Severity.WARNING)
                .count();
    }
    
    public int getInfoIssueCount() {
        return (int) issues.stream()
                .filter(issue -> issue.getSeverity() == ConversionIssue.Severity.INFO)
                .count();
    }
}