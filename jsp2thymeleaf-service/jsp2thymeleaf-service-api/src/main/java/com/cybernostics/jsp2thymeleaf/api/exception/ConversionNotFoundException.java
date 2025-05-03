package com.cybernostics.jsp2thymeleaf.api.exception;

public class ConversionNotFoundException extends ConversionException {
    public ConversionNotFoundException(String conversionId) {
        super("Conversion not found with ID: " + conversionId);
    }
}
