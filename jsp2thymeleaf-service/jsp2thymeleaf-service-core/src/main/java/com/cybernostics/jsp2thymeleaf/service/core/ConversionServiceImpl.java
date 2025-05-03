package com.cybernostics.jsp2thymeleaf.service.core;

import com.cybernostics.jsp2thymeleaf.api.ConversionService;
import com.cybernostics.jsp2thymeleaf.api.exception.ConversionNotFoundException;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionRequest;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionResponse;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionStatus;
import com.cybernostics.jsp2thymeleaf.api.util.ValidationUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the conversion service that manages conversion operations
 * and their status.
 */
@Service
public class ConversionServiceImpl implements ConversionService {
    
    private final Map<String, ConversionContext> activeConversions;
    
    public ConversionServiceImpl() {
        this.activeConversions = new ConcurrentHashMap<>();
    }
    
    @Override
    public ConversionResponse startConversion(ConversionRequest request) {
        ValidationUtils.validateConversionRequest(request);

        String conversionId = UUID.randomUUID().toString();
        ConversionEngine engine = new ConversionEngine(request.getOptions());
        ConversionContext context = new ConversionContext(request, engine);
        
        activeConversions.put(conversionId, context);
        CompletableFuture.runAsync(() -> processConversion(conversionId, context));

        ConversionResponse response = new ConversionResponse();
        response.setConversionId(conversionId);
        response.setStatus(ConversionStatus.PENDING);
        return response;
    }

    @Override
    public Optional<ConversionStatus> getConversionStatus(String conversionId) {
        return Optional.ofNullable(activeConversions.get(conversionId))
                .map(ConversionContext::getStatus);
    }

    @Override
    public Optional<ConversionResponse> getConversionResult(String conversionId) {
        ConversionContext context = activeConversions.get(conversionId);
        if (context == null) {
            return Optional.empty();
        }

        ConversionResponse response = new ConversionResponse();
        response.setConversionId(conversionId);
        response.setStatus(context.getStatus());
        response.setErrors(context.getErrors());
        response.setStatistics(context.getEngine().getStatistics());
        response.setOutputPath(context.getOutputPath());

        if (context.isComplete()) {
            activeConversions.remove(conversionId);
        }

        return Optional.of(response);
    }

    @Override
    public boolean cancelConversion(String conversionId) {
        ConversionContext context = activeConversions.get(conversionId);
        if (context == null) {
            throw new ConversionNotFoundException(conversionId);
        }

        context.getEngine().cancel();
        context.setStatus(ConversionStatus.CANCELLED);
        activeConversions.remove(conversionId);
        return true;
    }

    private void processConversion(String conversionId, ConversionContext context) {
        try {
            context.setStatus(ConversionStatus.IN_PROGRESS);
            
            if (context.getRequest().isDirectory()) {
                processDirectoryConversion(context);
            } else {
                processFileConversion(context);
            }
            
            if (context.getErrors().isEmpty()) {
                context.setStatus(ConversionStatus.COMPLETED);
            } else {
                context.setStatus(ConversionStatus.FAILED);
            }
            
        } catch (Exception e) {
            context.addError("Conversion failed: " + e.getMessage());
            context.setStatus(ConversionStatus.FAILED);
        }
    }
    
    private void processFileConversion(ConversionContext context) {
        Path sourcePath = Path.of(context.getRequest().getSource());
        try {
            context.getEngine().convertFile(sourcePath)
                .thenAccept(result -> {
                    if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                        context.getErrors().addAll(result.getErrors());
                    }
                    context.setOutputPath(result.getOutputPath());
                })
                .join();
        } catch (Exception e) {
            context.addError("File conversion failed: " + e.getMessage());
        }
    }
    
    private void processDirectoryConversion(ConversionContext context) {
        // Will be implemented in the next phase
        throw new UnsupportedOperationException("Directory conversion not yet implemented");
    }
}
