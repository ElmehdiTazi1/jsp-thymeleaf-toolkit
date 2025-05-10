package com.cybernostics.jsp2thymeleaf.reporting.generators;

import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Generates JSON reports for JSP to Thymeleaf conversion data.
 * Follows the Liskov Substitution Principle by properly implementing
 * the behavior defined in AbstractReportGenerator.
 * 
 * @author elmehdi.tazi
 */
public class JsonReportGenerator extends AbstractReportGenerator {
    
    private final ObjectMapper objectMapper;
    private final boolean prettyPrint;
    
    /**
     * Constructor with configurable pretty printing
     * 
     * @param prettyPrint Whether to format the JSON output for readability
     */
    public JsonReportGenerator(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        this.objectMapper = configureObjectMapper();
    }
    
    /**
     * Default constructor with pretty printing enabled
     */
    public JsonReportGenerator() {
        this(true);
    }
    
    /**
     * Configure the Jackson ObjectMapper with desired settings
     * 
     * @return Configured ObjectMapper
     */
    private ObjectMapper configureObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Only include non-null fields
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        if (prettyPrint) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        
        // Configure date/time serialization
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }

    @Override
    protected byte[] generateReportContent(ConversionReport report) throws IOException {
        String jsonContent = objectMapper.writeValueAsString(report);
        return jsonContent.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getFileExtension() {
        return "json";
    }

    @Override
    public String getReportName() {
        return "JSON Report";
    }
}