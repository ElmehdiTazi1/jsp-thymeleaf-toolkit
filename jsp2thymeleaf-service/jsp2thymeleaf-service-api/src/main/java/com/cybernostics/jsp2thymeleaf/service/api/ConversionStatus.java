package com.cybernostics.jsp2thymeleaf.service.api;

/**
 * Represents the status of a conversion operation.
 */
public enum ConversionStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
}
