package com.cybernostics.jsp2thymeleaf.api.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ConversionRequest implements Serializable {
    @NotNull
    private String source;
    private boolean isDirectory;
    private ConversionOptions options;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public ConversionOptions getOptions() {
        return options;
    }

    public void setOptions(ConversionOptions options) {
        this.options = options;
    }
}
