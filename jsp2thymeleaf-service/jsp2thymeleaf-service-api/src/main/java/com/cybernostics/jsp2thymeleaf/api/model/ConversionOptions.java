package com.cybernostics.jsp2thymeleaf.api.model;

import java.io.Serializable;

public class ConversionOptions implements Serializable {
    private boolean preserveComments = true;
    private boolean prettyPrint = true;
    private boolean generateSpringBootProject = false;
    private String outputEncoding = "UTF-8";
    private String targetDirectory;

    public boolean isPreserveComments() {
        return preserveComments;
    }

    public void setPreserveComments(boolean preserveComments) {
        this.preserveComments = preserveComments;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public boolean isGenerateSpringBootProject() {
        return generateSpringBootProject;
    }

    public void setGenerateSpringBootProject(boolean generateSpringBootProject) {
        this.generateSpringBootProject = generateSpringBootProject;
    }

    public String getOutputEncoding() {
        return outputEncoding;
    }

    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }
}
