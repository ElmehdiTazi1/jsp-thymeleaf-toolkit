package com.cybernostics.jsp2thymeleaf.reporting.model;

/**
 * Represents information about a scriptlet processed during conversion.
 * Follows Single Responsibility Principle by focusing only on scriptlet data.
 */
public class ScriptletInfo {
    
    /**
     * Type of scriptlet processing action.
     */
    public enum ScriptletAction {
        CONVERTED,      // Successfully converted to Thymeleaf
        COMMENTED,      // Commented out in the output
        EXTRACTED,      // Extracted to a separate file
        IGNORED,        // Left as is / ignored
        FAILED          // Conversion failed
    }
    
    private String filePath;
    private int lineNumber;
    private int columnNumber;
    private String originalContent;
    private String processedContent;
    private ScriptletAction action;
    private String conversionNotes;
    private int complexity;
    private String extractedFilePath;
    
    /**
     * Builder for ScriptletInfo to simplify creation following the Builder pattern.
     */
    public static class Builder {
        private String filePath;
        private int lineNumber;
        private int columnNumber;
        private String originalContent;
        private String processedContent;
        private ScriptletAction action = ScriptletAction.IGNORED; // Default action
        private String conversionNotes;
        private int complexity;
        private String extractedFilePath;
        
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
        
        public Builder withOriginalContent(String originalContent) {
            this.originalContent = originalContent;
            // Calculate complexity based on content
            this.complexity = calculateComplexity(originalContent);
            return this;
        }
        
        public Builder withProcessedContent(String processedContent) {
            this.processedContent = processedContent;
            return this;
        }
        
        public Builder withAction(ScriptletAction action) {
            this.action = action;
            return this;
        }
        
        public Builder withConversionNotes(String conversionNotes) {
            this.conversionNotes = conversionNotes;
            return this;
        }
        
        public Builder withExtractedFilePath(String extractedFilePath) {
            this.extractedFilePath = extractedFilePath;
            return this;
        }
        
        public ScriptletInfo build() {
            ScriptletInfo info = new ScriptletInfo();
            info.filePath = this.filePath;
            info.lineNumber = this.lineNumber;
            info.columnNumber = this.columnNumber;
            info.originalContent = this.originalContent;
            info.processedContent = this.processedContent;
            info.action = this.action;
            info.conversionNotes = this.conversionNotes;
            info.complexity = this.complexity;
            info.extractedFilePath = this.extractedFilePath;
            return info;
        }
        
        /**
         * Calculates the complexity of the scriptlet based on its content.
         * This is a simple heuristic and could be enhanced in the future.
         */
        private int calculateComplexity(String content) {
            if (content == null || content.isEmpty()) {
                return 0;
            }
            
            int baseComplexity = 1;
            
            // Add complexity for control structures
            baseComplexity += countOccurrences(content, "if ") * 2;
            baseComplexity += countOccurrences(content, "for ") * 3;
            baseComplexity += countOccurrences(content, "while ") * 3;
            baseComplexity += countOccurrences(content, "switch ") * 3;
            baseComplexity += countOccurrences(content, "try ") * 2;
            
            // Add complexity for the length
            baseComplexity += content.length() / 100;
            
            return baseComplexity;
        }
        
        /**
         * Counts occurrences of a substring within a string.
         * 
         * @param text The text to search in
         * @param substring The substring to search for
         * @return Number of occurrences
         */
        private int countOccurrences(String text, String substring) {
            int count = 0;
            int index = 0;
            while ((index = text.indexOf(substring, index)) != -1) {
                count++;
                index += substring.length();
            }
            return count;
        }
    }
    
    /**
     * Creates a new Builder instance for fluent creation of ScriptletInfo objects.
     * 
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Default constructor.
     */
    public ScriptletInfo() {
    }
    
    /**
     * Creates a new scriptlet info with specified details.
     * 
     * @param filePath Path to the file containing the scriptlet
     * @param lineNumber Line number where the scriptlet starts
     * @param originalContent Original scriptlet content
     * @param action Action taken on this scriptlet
     */
    public ScriptletInfo(String filePath, int lineNumber, String originalContent, ScriptletAction action) {
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.originalContent = originalContent;
        this.action = action;
        // Calculate complexity based on content
        if (originalContent != null) {
            this.calculateComplexity();
        }
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

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
        // Automatically calculate complexity based on content
        this.calculateComplexity();
    }

    public String getProcessedContent() {
        return processedContent;
    }

    public void setProcessedContent(String processedContent) {
        this.processedContent = processedContent;
    }

    public ScriptletAction getAction() {
        return action;
    }

    public void setAction(ScriptletAction action) {
        this.action = action;
    }

    public String getConversionNotes() {
        return conversionNotes;
    }

    public void setConversionNotes(String conversionNotes) {
        this.conversionNotes = conversionNotes;
    }

    public int getComplexity() {
        return complexity;
    }

    public String getExtractedFilePath() {
        return extractedFilePath;
    }

    public void setExtractedFilePath(String extractedFilePath) {
        this.extractedFilePath = extractedFilePath;
    }
    
    /**
     * Calculates the complexity of the scriptlet based on its content.
     * This is a simple heuristic and could be enhanced in the future.
     */
    private void calculateComplexity() {
        if (originalContent == null || originalContent.isEmpty()) {
            complexity = 0;
            return;
        }
        
        int baseComplexity = 1;
        
        // Add complexity for control structures
        baseComplexity += countOccurrences(originalContent, "if ") * 2;
        baseComplexity += countOccurrences(originalContent, "for ") * 3;
        baseComplexity += countOccurrences(originalContent, "while ") * 3;
        baseComplexity += countOccurrences(originalContent, "switch ") * 3;
        baseComplexity += countOccurrences(originalContent, "try ") * 2;
        
        // Add complexity for the length
        baseComplexity += originalContent.length() / 100;
        
        this.complexity = baseComplexity;
    }
    
    /**
     * Counts occurrences of a substring within a string.
     * 
     * @param text The text to search in
     * @param substring The substring to search for
     * @return Number of occurrences
     */
    private int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}