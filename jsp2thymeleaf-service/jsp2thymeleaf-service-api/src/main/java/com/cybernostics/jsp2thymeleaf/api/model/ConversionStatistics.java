package com.cybernostics.jsp2thymeleaf.api.model;

import java.io.Serializable;
import java.time.Duration;

public class ConversionStatistics implements Serializable {
    private int totalFiles;
    private int convertedFiles;
    private int failedFiles;
    private Duration conversionTime;
    private int totalConverters;
    private int appliedConverters;

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getConvertedFiles() {
        return convertedFiles;
    }

    public void setConvertedFiles(int convertedFiles) {
        this.convertedFiles = convertedFiles;
    }

    public int getFailedFiles() {
        return failedFiles;
    }

    public void setFailedFiles(int failedFiles) {
        this.failedFiles = failedFiles;
    }

    public Duration getConversionTime() {
        return conversionTime;
    }

    public void setConversionTime(Duration conversionTime) {
        this.conversionTime = conversionTime;
    }

    public int getTotalConverters() {
        return totalConverters;
    }

    public void setTotalConverters(int totalConverters) {
        this.totalConverters = totalConverters;
    }

    public int getAppliedConverters() {
        return appliedConverters;
    }

    public void setAppliedConverters(int appliedConverters) {
        this.appliedConverters = appliedConverters;
    }
}
