package com.cybernostics.jsp2thymeleaf.api.exception;

public class InvalidRequestException extends ConversionException {
    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
