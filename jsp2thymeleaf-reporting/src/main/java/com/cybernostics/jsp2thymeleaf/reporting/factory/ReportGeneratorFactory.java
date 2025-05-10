package com.cybernostics.jsp2thymeleaf.reporting.factory;

import com.cybernostics.jsp2thymeleaf.reporting.api.ReportGenerator;
import com.cybernostics.jsp2thymeleaf.reporting.generators.HtmlReportGenerator;
import com.cybernostics.jsp2thymeleaf.reporting.generators.JsonReportGenerator;
import com.cybernostics.jsp2thymeleaf.reporting.generators.SummaryReportGenerator;
import com.cybernostics.jsp2thymeleaf.reporting.service.ReportGeneratorType;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Factory class to create appropriate report generators based on type.
 * Uses the Factory Method pattern to create report generator instances.
 */
public class ReportGeneratorFactory {
    
    /**
     * Creates a report generator for the specified type.
     * 
     * @param type The type of report generator to create
     * @return A new report generator instance or null if type is not supported
     */
    public ReportGenerator createGenerator(ReportGeneratorType type) {
        switch (type) {
            case HTML:
                return new HtmlReportGenerator();
            case JSON:
                return new JsonReportGenerator();
            case SUMMARY:
                return new SummaryReportGenerator();
            default:
                return null;
        }
    }
    
    /**
     * Creates instances of all available report generators.
     * 
     * @return A list of all available report generators
     */
    public static List<ReportGenerator> createAllGenerators() {
        ReportGeneratorFactory factory = new ReportGeneratorFactory();
        return Arrays.stream(ReportGeneratorType.values())
                .map(factory::createGenerator)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}