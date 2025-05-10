package com.cybernostics.jsp2thymeleaf.reporting.model;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents an issue encountered during the conversion process.
 * Clean code principles applied with meaningful names and immutability.
 * 
 * @author elmehdi.tazi
 */
public class ConversionIssue {
    
    /**
     * Enum representing severity levels of conversion issues
     */
    public enum Severity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }
    
    private final String issueId;
    private final Path filePath;
    private final int lineNumber;
    private final int columnNumber;
    private final String message;
    private final Severity severity;
    private final String issueType;
    private final String suggestion;
    private final String category;
    
    private ConversionIssue(Builder builder) {
        this.issueId = builder.issueId;
        this.filePath = builder.filePath;
        this.lineNumber = builder.lineNumber;
        this.columnNumber = builder.columnNumber;
        this.message = builder.message;
        this.severity = builder.severity;
        this.issueType = builder.issueType;
        this.suggestion = builder.suggestion;
        this.category = builder.category;
    }
    
    /**
     * Legacy constructor for backward compatibility
     * 
     * @param filePath Path to the file with the issue
     * @param lineNumber Line number of the issue
     * @param message Error message
     * @param severity Issue severity
     */
    public ConversionIssue(String filePath, int lineNumber, String message, Severity severity) {
        this(builder()
            .withFilePath(Paths.get(filePath))
            .withLineNumber(lineNumber)
            .withMessage(message)
            .withSeverity(severity));
    }
    
    /**
     * Builder for ConversionIssue
     */
    public static class Builder {
        private String issueId;
        private Path filePath;
        private int lineNumber;
        private int columnNumber;
        private String message;
        private Severity severity = Severity.ERROR; // Default severity
        private String issueType;
        private String suggestion;
        private String category;
        
        public Builder withIssueId(String issueId) {
            this.issueId = issueId;
            return this;
        }
        
        public Builder withFilePath(Path filePath) {
            this.filePath = filePath;
            return this;
        }
        
        public Builder withLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }
        
        public Builder withColumnNumber(int columnNumber) {
            this.columnNumber = columnNumber;
            return this;
        }
        
        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }
        
        public Builder withSeverity(Severity severity) {
            this.severity = severity;
            return this;
        }
        
        public Builder withIssueType(String issueType) {
            this.issueType = issueType;
            return this;
        }
        
        public Builder withSuggestion(String suggestion) {
            this.suggestion = suggestion;
            return this;
        }
        
        public Builder withCategory(String category) {
            this.category = category;
            return this;
        }
        
        public ConversionIssue build() {
            return new ConversionIssue(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }

    // Getters - no setters for immutability
    
    public String getIssueId() {
        return issueId;
    }

    public Path getFilePath() {
        return filePath;
    }
    
    public String getFilePath(String defaultValue) {
        return filePath != null ? filePath.toString() : defaultValue;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getSuggestion() {
        return suggestion;
    }
    
    public String getCategory() {
        return category;
    }
    
    /**
     * Gets a formatted location string (file:line:column)
     * 
     * @return Formatted location
     */
    public String getFormattedLocation() {
        if (filePath == null) {
            return "Unknown:" + lineNumber + ":" + columnNumber;
        }
        return filePath.getFileName() + ":" + lineNumber + ":" + columnNumber;
    }
    
    @Override
    public String toString() {
        return severity + " at " + getFormattedLocation() + ": " + message;
    }
}