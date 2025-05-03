package com.cybernostics.jsp2thymeleaf.service.api;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import jakarta.validation.constraints.NotNull;

/**
 * Main interface for JSP to Thymeleaf conversion operations.
 */
public interface ConversionService {
    /**
     * Convert a single JSP file to Thymeleaf.
     *
     * @param file The JSP file to convert
     * @return Result of the conversion
     */
    ConversionResult convertJspFile(@NotNull MultipartFile file);

    /**
     * Convert an entire WEB-INF directory structure.
     *
     * @param directoryPath Path to the WEB-INF directory
     * @return Result of the batch conversion
     */
    BatchConversionResult convertWebInfDirectory(@NotNull Path directoryPath);

    /**
     * Get the status of an ongoing conversion.
     *
     * @param conversionId The ID of the conversion process
     * @return Current status of the conversion
     */
    ConversionStatus getConversionStatus(@NotNull String conversionId);
}
