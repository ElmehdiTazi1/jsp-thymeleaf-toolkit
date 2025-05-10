package com.cybernostics.jsp2thymeleaf.reporting.service;

/**
 * Defines the supported report generator types.
 */
public enum ReportGeneratorType {
    HTML("html"),
    JSON("json"),
    SUMMARY("txt");
    
    private final String fileExtension;
    
    ReportGeneratorType(String fileExtension) {
        this.fileExtension = fileExtension;
    }
    
    /**
     * Gets the file extension for this report type.
     * 
     * @return The file extension without the leading dot
     */
    public String getFileExtension() {
        return fileExtension;
    }
}