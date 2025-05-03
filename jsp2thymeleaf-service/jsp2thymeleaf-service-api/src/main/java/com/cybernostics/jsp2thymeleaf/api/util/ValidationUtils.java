package com.cybernostics.jsp2thymeleaf.api.util;

import com.cybernostics.jsp2thymeleaf.api.exception.InvalidRequestException;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionRequest;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class ValidationUtils {
    private ValidationUtils() {
        // Utility class
    }

    public static void validateConversionRequest(ConversionRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Conversion request cannot be null");
        }

        if (request.getSource() == null || request.getSource().trim().isEmpty()) {
            throw new InvalidRequestException("Source path cannot be null or empty");
        }

        try {
            Path sourcePath = Paths.get(request.getSource());
            if (request.isDirectory() && !sourcePath.toFile().isDirectory()) {
                throw new InvalidRequestException("Source path is not a directory: " + request.getSource());
            }
        } catch (SecurityException | IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid source path: " + request.getSource(), e);
        }

        if (request.getOptions() != null && request.getOptions().getOutputEncoding() != null) {
            try {
                java.nio.charset.Charset.forName(request.getOptions().getOutputEncoding());
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Invalid output encoding: " + request.getOptions().getOutputEncoding());
            }
        }
    }
}
