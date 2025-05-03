package com.cybernostics.jsp2thymeleaf.api;

import com.cybernostics.jsp2thymeleaf.api.model.ConversionRequest;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionResponse;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionStatus;

import java.util.Optional;

public interface ConversionService {
    /**
     * Starts a new conversion process for the given request.
     *
     * @param request The conversion request containing source and options
     * @return A ConversionResponse with the initial status and conversion ID
     */
    ConversionResponse startConversion(ConversionRequest request);

    /**
     * Gets the current status of a conversion process.
     *
     * @param conversionId The ID of the conversion process
     * @return The current status of the conversion
     */
    Optional<ConversionStatus> getConversionStatus(String conversionId);

    /**
     * Gets the full conversion response with results and statistics.
     *
     * @param conversionId The ID of the conversion process
     * @return The complete conversion response
     */
    Optional<ConversionResponse> getConversionResult(String conversionId);

    /**
     * Cancels an ongoing conversion process.
     *
     * @param conversionId The ID of the conversion process to cancel
     * @return true if the conversion was successfully cancelled, false otherwise
     */
    boolean cancelConversion(String conversionId);
}
