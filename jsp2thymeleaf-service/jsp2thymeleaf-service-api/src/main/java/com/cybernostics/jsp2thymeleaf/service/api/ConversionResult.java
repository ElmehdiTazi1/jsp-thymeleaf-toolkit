package com.cybernostics.jsp2thymeleaf.service.api;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents the result of a JSP to Thymeleaf conversion operation.
 */
public class ConversionResult {
    @NotNull
    private final String conversionId;
    private final String outputPath;
    private final ConversionStatus status;
    private final List<ConversionWarning> warnings;
    private final List<ConversionError> errors;

    public ConversionResult(String conversionId, String outputPath, ConversionStatus status,
                          List<ConversionWarning> warnings, List<ConversionError> errors) {
        this.conversionId = conversionId;
        this.outputPath = outputPath;
        this.status = status;
        this.warnings = warnings;
        this.errors = errors;
    }

    public String getConversionId() {
        return conversionId;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public ConversionStatus getStatus() {
        return status;
    }

    public List<ConversionWarning> getWarnings() {
        return warnings;
    }

    public List<ConversionError> getErrors() {
        return errors;
    }

    public boolean isSuccessful() {
        return errors.isEmpty() && status == ConversionStatus.COMPLETED;
    }
}
