package com.cybernostics.jsp2thymeleaf.reporting.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary statistics for conversion of a specific tag type.
 * Adheres to Single Responsibility Principle by focusing on tag conversion data only.
 * 
 * @author elmehdi.tazi
 */
public class TagConversionSummary {
    private String tagName;
    private String tagNamespace;
    private String targetTagName;
    private String targetTagNamespace;
    private int conversionCount;
    private int successfulConversions;
    private int failedConversions;
    private List<ConversionIssue> tagSpecificIssues = new ArrayList<>();
    
    private TagConversionSummary(Builder builder) {
        this.tagName = builder.tagName;
        this.tagNamespace = builder.tagNamespace;
        this.targetTagName = builder.targetTagName;
        this.targetTagNamespace = builder.targetTagNamespace;
        this.conversionCount = builder.conversionCount;
        this.successfulConversions = builder.successfulConversions;
        this.failedConversions = builder.failedConversions;
        this.tagSpecificIssues.addAll(builder.tagSpecificIssues);
    }
    
    /**
     * Constructor for backward compatibility
     * 
     * @param tagName Tag name
     * @param tagNamespace Tag namespace
     * @param conversionCount Number of conversions
     * @param successfulConversions Number of successful conversions
     */
    public TagConversionSummary(String tagName, String tagNamespace, int conversionCount, int successfulConversions) {
        this.tagName = tagName;
        this.tagNamespace = tagNamespace;
        this.conversionCount = conversionCount;
        this.successfulConversions = successfulConversions;
        this.failedConversions = conversionCount - successfulConversions;
        this.tagSpecificIssues = new ArrayList<>();
    }
    
    /**
     * Increments the conversion count
     */
    public void incrementConversionCount() {
        this.conversionCount++;
    }
    
    /**
     * Increments the successful conversion count
     */
    public void incrementSuccessfulConversions() {
        this.successfulConversions++;
    }
    
    /**
     * Increments the failed conversion count
     */
    public void incrementFailedConversions() {
        this.failedConversions++;
    }
    
    public static class Builder {
        private String tagName;
        private String tagNamespace;
        private String targetTagName;
        private String targetTagNamespace;
        private int conversionCount;
        private int successfulConversions;
        private int failedConversions;
        private List<ConversionIssue> tagSpecificIssues = new ArrayList<>();
        
        public Builder withTagName(String tagName) {
            this.tagName = tagName;
            return this;
        }
        
        public Builder withTagNamespace(String tagNamespace) {
            this.tagNamespace = tagNamespace;
            return this;
        }
        
        public Builder withTargetTagName(String targetTagName) {
            this.targetTagName = targetTagName;
            return this;
        }
        
        public Builder withTargetTagNamespace(String targetTagNamespace) {
            this.targetTagNamespace = targetTagNamespace;
            return this;
        }
        
        public Builder withConversionCount(int conversionCount) {
            this.conversionCount = conversionCount;
            return this;
        }
        
        public Builder withSuccessfulConversions(int successfulConversions) {
            this.successfulConversions = successfulConversions;
            return this;
        }
        
        public Builder withFailedConversions(int failedConversions) {
            this.failedConversions = failedConversions;
            return this;
        }
        
        public Builder incrementConversionCount() {
            this.conversionCount++;
            return this;
        }
        
        public Builder incrementSuccessfulConversions() {
            this.successfulConversions++;
            return this;
        }
        
        public Builder incrementFailedConversions() {
            this.failedConversions++;
            return this;
        }
        
        public Builder withTagSpecificIssues(List<ConversionIssue> issues) {
            this.tagSpecificIssues.addAll(issues);
            return this;
        }
        
        public Builder addTagSpecificIssue(ConversionIssue issue) {
            this.tagSpecificIssues.add(issue);
            return this;
        }
        
        public TagConversionSummary build() {
            return new TagConversionSummary(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters

    public String getTagName() {
        return tagName;
    }

    public String getTagNamespace() {
        return tagNamespace;
    }

    public String getTargetTagName() {
        return targetTagName;
    }

    public String getTargetTagNamespace() {
        return targetTagNamespace;
    }

    public int getConversionCount() {
        return conversionCount;
    }

    public int getSuccessfulConversions() {
        return successfulConversions;
    }

    public int getFailedConversions() {
        return failedConversions;
    }

    public List<ConversionIssue> getTagSpecificIssues() {
        return tagSpecificIssues;
    }
    
    /**
     * Get the success rate for this tag type conversion
     * 
     * @return Percentage of successful conversions (0-100)
     */
    public double getSuccessRate() {
        if (conversionCount == 0) {
            return 0;
        }
        return (double) successfulConversions / conversionCount * 100;
    }
    
    /**
     * Get the qualified name of this tag (namespace:name)
     * 
     * @return Qualified tag name
     */
    public String getQualifiedTagName() {
        if (tagNamespace == null || tagNamespace.isEmpty()) {
            return tagName;
        }
        return tagNamespace + ":" + tagName;
    }
    
    /**
     * Get the qualified name of the target tag (namespace:name)
     * 
     * @return Qualified target tag name
     */
    public String getQualifiedTargetTagName() {
        if (targetTagNamespace == null || targetTagNamespace.isEmpty()) {
            return targetTagName;
        }
        return targetTagNamespace + ":" + targetTagName;
    }
}