package com.cybernostics.jsp2thymeleaf.reporting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains information about a JSP tag conversion to Thymeleaf.
 * Implements Single Responsibility Principle by focusing only on tag conversion data.
 */
public class TagConversionInfo {
    
    /**
     * Status of the tag conversion.
     */
    public enum ConversionStatus {
        SUCCESS,        // Fully converted
        PARTIAL,        // Partially converted with some issues
        FAILED,         // Conversion failed completely
        UNSUPPORTED     // Tag not supported for conversion
    }
    
    private String filePath;
    private int lineNumber;
    private int columnNumber;
    private String originalTagName;
    private String originalNamespace;
    private String thymeleafEquivalent;
    private ConversionStatus status;
    private String originalMarkup;
    private String convertedMarkup;
    private String converterClassName;
    private Map<String, AttributeConversion> attributeConversions;
    private List<ConversionIssue> issues;
    
    /**
     * Inner class representing attribute conversion details.
     */
    public static class AttributeConversion {
        private String originalName;
        private String originalValue;
        private String convertedName;
        private String convertedValue;
        private boolean successful;
        private String notes;
        
        public AttributeConversion(String originalName, String originalValue, 
                                  String convertedName, String convertedValue, 
                                  boolean successful) {
            this.originalName = originalName;
            this.originalValue = originalValue;
            this.convertedName = convertedName;
            this.convertedValue = convertedValue;
            this.successful = successful;
        }
        
        // Getters and setters
        
        public String getOriginalName() {
            return originalName;
        }
        
        public String getOriginalValue() {
            return originalValue;
        }
        
        public String getConvertedName() {
            return convertedName;
        }
        
        public String getConvertedValue() {
            return convertedValue;
        }
        
        public boolean isSuccessful() {
            return successful;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
    
    /**
     * Builder for TagConversionInfo to simplify creation.
     */
    public static class Builder {
        private String filePath;
        private int lineNumber;
        private int columnNumber;
        private String originalTagName;
        private String originalNamespace;
        private String thymeleafEquivalent;
        private ConversionStatus status = ConversionStatus.SUCCESS;
        private String originalMarkup;
        private String convertedMarkup;
        private String converterClassName;
        private Map<String, AttributeConversion> attributeConversions = new HashMap<>();
        private List<ConversionIssue> issues = new ArrayList<>();
        
        public Builder withFilePath(String filePath) {
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
        
        public Builder withOriginalTagName(String originalTagName) {
            this.originalTagName = originalTagName;
            return this;
        }
        
        public Builder withOriginalNamespace(String originalNamespace) {
            this.originalNamespace = originalNamespace;
            return this;
        }
        
        public Builder withThymeleafEquivalent(String thymeleafEquivalent) {
            this.thymeleafEquivalent = thymeleafEquivalent;
            return this;
        }
        
        public Builder withStatus(ConversionStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder withOriginalMarkup(String originalMarkup) {
            this.originalMarkup = originalMarkup;
            return this;
        }
        
        public Builder withConvertedMarkup(String convertedMarkup) {
            this.convertedMarkup = convertedMarkup;
            return this;
        }
        
        public Builder withConverterClassName(String converterClassName) {
            this.converterClassName = converterClassName;
            return this;
        }
        
        public Builder withAttributeConversion(String originalName, String originalValue, 
                                              String convertedName, String convertedValue, 
                                              boolean successful) {
            AttributeConversion attrConversion = new AttributeConversion(
                originalName, originalValue, convertedName, convertedValue, successful);
            this.attributeConversions.put(originalName, attrConversion);
            return this;
        }
        
        public Builder withIssue(ConversionIssue issue) {
            this.issues.add(issue);
            return this;
        }
        
        public TagConversionInfo build() {
            TagConversionInfo info = new TagConversionInfo();
            info.filePath = this.filePath;
            info.lineNumber = this.lineNumber;
            info.columnNumber = this.columnNumber;
            info.originalTagName = this.originalTagName;
            info.originalNamespace = this.originalNamespace;
            info.thymeleafEquivalent = this.thymeleafEquivalent;
            info.status = this.status;
            info.originalMarkup = this.originalMarkup;
            info.convertedMarkup = this.convertedMarkup;
            info.converterClassName = this.converterClassName;
            info.attributeConversions = new HashMap<>(this.attributeConversions);
            info.issues = new ArrayList<>(this.issues);
            return info;
        }
    }
    
    /**
     * Default constructor.
     */
    public TagConversionInfo() {
        this.attributeConversions = new HashMap<>();
        this.issues = new ArrayList<>();
    }
    
    /**
     * Creates a new tag conversion info with basic details.
     * 
     * @param filePath Path to the file containing the tag
     * @param lineNumber Line number of the tag
     * @param originalTagName Original JSP tag name
     * @param originalNamespace Original JSP namespace
     */
    public TagConversionInfo(String filePath, int lineNumber, String originalTagName, String originalNamespace) {
        this();
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.originalTagName = originalTagName;
        this.originalNamespace = originalNamespace;
    }
    
    /**
     * Creates a new Builder instance for fluent creation of TagConversionInfo objects.
     * 
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Adds information about an attribute conversion.
     * 
     * @param originalName Original attribute name
     * @param originalValue Original attribute value
     * @param convertedName Converted attribute name
     * @param convertedValue Converted attribute value
     * @param successful Whether the conversion was successful
     * @return This instance for method chaining
     */
    public TagConversionInfo addAttributeConversion(String originalName, String originalValue, 
                                                   String convertedName, String convertedValue, 
                                                   boolean successful) {
        AttributeConversion attrConversion = new AttributeConversion(
            originalName, originalValue, convertedName, convertedValue, successful);
        this.attributeConversions.put(originalName, attrConversion);
        return this;
    }
    
    /**
     * Adds an issue related to this tag conversion.
     * 
     * @param issue The issue to add
     * @return This instance for method chaining
     */
    public TagConversionInfo addIssue(ConversionIssue issue) {
        this.issues.add(issue);
        return this;
    }
    
    // Getters and setters
    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getOriginalTagName() {
        return originalTagName;
    }

    public void setOriginalTagName(String originalTagName) {
        this.originalTagName = originalTagName;
    }

    public String getOriginalNamespace() {
        return originalNamespace;
    }

    public void setOriginalNamespace(String originalNamespace) {
        this.originalNamespace = originalNamespace;
    }

    public String getThymeleafEquivalent() {
        return thymeleafEquivalent;
    }

    public void setThymeleafEquivalent(String thymeleafEquivalent) {
        this.thymeleafEquivalent = thymeleafEquivalent;
    }

    public ConversionStatus getStatus() {
        return status;
    }

    public void setStatus(ConversionStatus status) {
        this.status = status;
    }

    public String getOriginalMarkup() {
        return originalMarkup;
    }

    public void setOriginalMarkup(String originalMarkup) {
        this.originalMarkup = originalMarkup;
    }

    public String getConvertedMarkup() {
        return convertedMarkup;
    }

    public void setConvertedMarkup(String convertedMarkup) {
        this.convertedMarkup = convertedMarkup;
    }

    public String getConverterClassName() {
        return converterClassName;
    }

    public void setConverterClassName(String converterClassName) {
        this.converterClassName = converterClassName;
    }

    public Map<String, AttributeConversion> getAttributeConversions() {
        return attributeConversions;
    }

    public List<ConversionIssue> getIssues() {
        return issues;
    }
    
    public boolean hasIssues() {
        return !issues.isEmpty();
    }
}