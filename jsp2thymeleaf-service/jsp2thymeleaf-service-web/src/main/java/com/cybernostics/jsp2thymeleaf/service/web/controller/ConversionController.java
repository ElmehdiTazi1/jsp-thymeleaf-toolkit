package com.cybernostics.jsp2thymeleaf.service.web.controller;

import com.cybernostics.jsp2thymeleaf.api.ConversionService;
import com.cybernostics.jsp2thymeleaf.api.exception.ConversionNotFoundException;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionRequest;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionResponse;
import com.cybernostics.jsp2thymeleaf.api.model.ConversionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/conversion")
@Tag(name = "Conversion", description = "JSP to Thymeleaf conversion endpoints")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Convert a single JSP file",
            description = "Upload and convert a JSP file to Thymeleaf template")
    @ApiResponse(responseCode = "200", description = "Successful conversion",
            content = @Content(schema = @Schema(implementation = ConversionResponse.class)))
    public ResponseEntity<ConversionResponse> convertFile(
            @Parameter(description = "JSP file to convert")
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Conversion options")
            @RequestPart(required = false) ConversionRequest options) {
        
        try {
            // Create temporary file
            Path tempFile = Files.createTempFile("jsp_upload_", ".jsp");
            file.transferTo(tempFile);

            // Create or update request
            ConversionRequest request = options != null ? options : new ConversionRequest();
            request.setSource(tempFile.toString());
            request.setDirectory(false);

            ConversionResponse response = conversionService.startConversion(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ConversionResponse errorResponse = new ConversionResponse();
            errorResponse.setStatus(ConversionStatus.FAILED);
            errorResponse.addError("File upload failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }    @PostMapping("/directory")
    @Operation(summary = "Convert a directory of JSP files",
            description = "Convert all JSP files in a specified directory to Thymeleaf templates")
    public ResponseEntity<ConversionResponse> convertDirectory(
            @Valid @RequestBody ConversionRequest request) {
        
        request.setDirectory(true);
        ConversionResponse response = conversionService.startConversion(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{id}")
    @Operation(summary = "Get conversion status",
            description = "Check the status of an ongoing conversion")
    public ResponseEntity<ConversionStatus> getStatus(
            @Parameter(description = "Conversion ID")
            @PathVariable("id") String conversionId) {
        
        return conversionService.getConversionStatus(conversionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/result/{id}")
    @Operation(summary = "Get conversion result",
            description = "Get the complete result of a conversion")
    public ResponseEntity<ConversionResponse> getResult(
            @Parameter(description = "Conversion ID")
            @PathVariable("id") String conversionId) {
        
        return conversionService.getConversionResult(conversionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel conversion",
            description = "Cancel an ongoing conversion process")
    public ResponseEntity<Void> cancelConversion(
            @Parameter(description = "Conversion ID")
            @PathVariable("id") String conversionId) {
        
        try {
            conversionService.cancelConversion(conversionId);
            return ResponseEntity.ok().build();
        } catch (ConversionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
