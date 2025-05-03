package com.cybernostics.jsp2thymeleaf.service.api.validation;

import jakarta.validation.constraints.NotNull;

/**
 * Interface for validating conversion results.
 */
public interface ValidationService {
    /**
     * Validate the converted Thymeleaf template.
     *
     * @param template The converted template content
     * @return ValidationResult containing any validation errors or warnings
     */
    ValidationResult validateTemplate(@NotNull String template);

    /**
     * Validate the structure of a converted WEB-INF directory.
     *
     * @param directoryPath Path to the converted directory
     * @return ValidationResult containing any structural validation issues
     */
    ValidationResult validateStructure(@NotNull String directoryPath);
}
