package com.cybernostics.jsp2thymeleaf.reporting.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Summary statistics for scriptlet processing.
 * Adheres to SRP by focusing on scriptlet processing data.
 * 
 * @author elmehdi.tazi
 */
public class ScriptletSummary {
    /**
     * Enum representing different types of scriptlets.
     */
    public enum ScriptletType {
        EXPRESSION("<%="),
        SCRIPTLET("<%"),
        DECLARATION("<%!"),
        COMMENT("<%--");
        
        private final String prefix;
        
        ScriptletType(String prefix) {
            this.prefix = prefix;
        }
        
        public String getPrefix() {
            return prefix;
        }
    }
    
    /**
     * Enum representing different strategies for handling scriptlets.
     */
    public enum ScriptletHandlingStrategy {
        HTML_COMMENT,
        THYMELEAF_COMMENT,
        EXTRACT_TO_FILE,
        CONVERT_TO_THYMELEAF
    }
    
    private int totalProcessed;
    private Map<ScriptletType, Integer> countByType = new HashMap<>();
    private Map<ScriptletHandlingStrategy, Integer> countByStrategy = new HashMap<>();
    private int successfullyHandled;
    private int failedToHandle;
    
    // Initialize with default values
    public ScriptletSummary() {
        // Initialize counts for each type
        for (ScriptletType type : ScriptletType.values()) {
            countByType.put(type, 0);
        }
        
        // Initialize counts for each strategy
        for (ScriptletHandlingStrategy strategy : ScriptletHandlingStrategy.values()) {
            countByStrategy.put(strategy, 0);
        }
    }
    
    // Methods to update counts
    
    public void incrementTotal() {
        totalProcessed++;
    }
    
    public void incrementSuccessful() {
        successfullyHandled++;
    }
    
    public void incrementFailed() {
        failedToHandle++;
    }
    
    public void incrementByType(ScriptletType type) {
        countByType.put(type, countByType.getOrDefault(type, 0) + 1);
    }
    
    public void incrementByStrategy(ScriptletHandlingStrategy strategy) {
        countByStrategy.put(strategy, countByStrategy.getOrDefault(strategy, 0) + 1);
    }
    
    // Getters
    
    public int getTotalProcessed() {
        return totalProcessed;
    }
    
    public Map<ScriptletType, Integer> getCountByType() {
        return countByType;
    }
    
    public Map<ScriptletHandlingStrategy, Integer> getCountByStrategy() {
        return countByStrategy;
    }
    
    public int getSuccessfullyHandled() {
        return successfullyHandled;
    }
    
    public int getFailedToHandle() {
        return failedToHandle;
    }
    
    /**
     * Get the success rate for scriptlet handling
     * 
     * @return Percentage of successfully handled scriptlets (0-100)
     */
    public double getSuccessRate() {
        if (totalProcessed == 0) {
            return 0;
        }
        return (double) successfullyHandled / totalProcessed * 100;
    }
    
    /**
     * Get count for a specific scriptlet type
     * 
     * @param type The scriptlet type
     * @return The count for that type
     */
    public int getCountForType(ScriptletType type) {
        return countByType.getOrDefault(type, 0);
    }
    
    /**
     * Get count for a specific handling strategy
     * 
     * @param strategy The handling strategy
     * @return The count for that strategy
     */
    public int getCountForStrategy(ScriptletHandlingStrategy strategy) {
        return countByStrategy.getOrDefault(strategy, 0);
    }
}