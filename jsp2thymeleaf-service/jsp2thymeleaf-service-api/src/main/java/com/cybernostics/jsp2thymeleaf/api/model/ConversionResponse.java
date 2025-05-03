package com.cybernostics.jsp2thymeleaf.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConversionResponse implements Serializable {
    private String conversionId;
    private ConversionStatus status;
    private String outputPath;
    private List<String> errors;
    private ConversionStatistics statistics;

    public ConversionResponse() {
        this.errors = new ArrayList<>();
        this.statistics = new ConversionStatistics();
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public ConversionStatus getStatus() {
        return status;
    }

    public void setStatus(ConversionStatus status) {
        this.status = status;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public ConversionStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(ConversionStatistics statistics) {
        this.statistics = statistics;
    }
}
