package com.cybernostics.jsp2thymeleaf.service.core.engine;

import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionOptions;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionStatus;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionResult;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionStatistics;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Core engine that interfaces with the JSP2Thymeleaf toolkit for performing conversions.
 */
@Component
public class ConversionEngine {
    
    private final ConversionOptions options;
    private final ConversionStatistics statistics;
    private volatile boolean isCancelled;
    
    public ConversionEngine(ConversionOptions options) {
        this.options = options != null ? options : new ConversionOptions();
        this.statistics = new ConversionStatistics();
        this.isCancelled = false;
    }
    
    /**
     * Converts a single JSP file to Thymeleaf.
     */
    public CompletableFuture<ConversionResult> convertFile(Path sourcePath) {
        return CompletableFuture.supplyAsync(() -> {
            Instant start = Instant.now();
            ConversionResult result = new ConversionResult();
            String conversionId = UUID.randomUUID().toString();
              try {
                if (isCancelled) {
                    result.setStatus(ConversionStatus.CANCELLED);
                    return result;
                }

                if (!Files.exists(sourcePath)) {
                    throw new IllegalArgumentException("Source file does not exist: " + sourcePath);
                }

                Path outputPath = generateOutputPath(sourcePath);
                JSP2ThymeleafConfiguration configuration = createConfiguration();
                
                // Create TokenisedFile 
                TokenisedFile tokenisedFile = new TokenisedFile(sourcePath, outputPath);
                
                // Perform conversion using JSP2Thymeleaf
                JSP2Thymeleaf converter = new JSP2Thymeleaf(configuration);
                List<JSP2ThymeLeafException> exceptions = converter.run();
                
                result.setConversionId(conversionId);
                result.setOutputPath(outputPath.toString());
                result.setStatus(exceptions.isEmpty() ? ConversionStatus.COMPLETED : ConversionStatus.FAILED);
                
                exceptions.forEach(e -> {
                    if (e.isFatal()) {
                        result.addError(e.getMessage());
                    }
                });
                
                updateStatistics(start);
                return result;
                
            } catch (Exception e) {
                result.setConversionId(conversionId);
                result.setStatus(ConversionStatus.FAILED);
                result.addError(e.getMessage());
                return result;
            }
        });
    }
    
    private Path generateOutputPath(Path sourcePath) {
        String filename = sourcePath.getFileName().toString();
        String outputFilename = filename.replace(".jsp", ".html");
        if (options.getTargetDirectory() != null) {
            return Path.of(options.getTargetDirectory()).resolve(outputFilename);
        }
        return sourcePath.getParent().resolve("converted").resolve(outputFilename);
    }
    
    private JSP2ThymeleafConfiguration createConfiguration() {
        JSP2ThymeleafConfiguration config = new JSP2ThymeleafConfiguration();
        // Apply conversion options
        // This will be enhanced in future phases
        return config;
    }
    
    private void updateStatistics(Instant start) {
        statistics.setConversionTime(Duration.between(start, Instant.now()));
    }
    
    public void cancel() {
        this.isCancelled = true;
    }
    
    public ConversionStatistics getStatistics() {
        return statistics;
    }
}
