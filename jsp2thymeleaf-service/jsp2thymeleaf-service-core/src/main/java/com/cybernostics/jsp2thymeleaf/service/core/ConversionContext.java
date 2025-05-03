package com.cybernostics.jsp2thymeleaf.service.core;

import com.cybernostics.jsp2thymeleaf.api.model.ConversionRequest;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionStatus;
import com.cybernostics.jsp2thymeleaf.service.core.engine.ConversionEngine;

import java.util.ArrayList;
import java.util.List;

public class ConversionContext {
    private final ConversionRequest request;
    private final ConversionEngine engine;
    private final List<String> errors;
    private String outputPath;
    private ConversionStatus status;

    public ConversionContext(ConversionRequest request, ConversionEngine engine) {
        this.request = request;
        this.engine = engine;
        this.errors = new ArrayList<>();
        this.status = ConversionStatus.PENDING;
    }

    public ConversionRequest getRequest() {
        return request;
    }

    public ConversionEngine getEngine() {
        return engine;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public ConversionStatus getStatus() {
        return status;
    }

    public void setStatus(ConversionStatus status) {
        this.status = status;
    }

    public boolean isComplete() {
        return status == ConversionStatus.COMPLETED || 
               status == ConversionStatus.FAILED || 
               status == ConversionStatus.CANCELLED;
    }
}
