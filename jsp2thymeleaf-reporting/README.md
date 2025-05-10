# JSP2Thymeleaf Reporting

## Overview

The JSP2Thymeleaf reporting module provides comprehensive reporting capabilities for the JSP to Thymeleaf conversion process. This module allows you to generate detailed reports about the conversion process, including statistics, issues encountered, and recommendations for manual intervention.

## Features

- **Multiple Report Formats**: Generate reports in HTML, JSON, and plain text summary formats
- **Detailed Conversion Statistics**: Track success rates, conversion time, and file-level metrics
- **Tag Conversion Analysis**: See which JSP tags were converted successfully and their Thymeleaf equivalents
- **Scriptlet Handling**: View statistics and details about scriptlets found in JSP files
- **Issue Tracking**: Identify and categorize issues encountered during conversion
- **Visualization**: HTML reports include charts and visual representations of conversion data
- **Integration**: Seamlessly integrate with both Maven plugin and command-line interface

## Usage

### Maven Plugin

Add the following to your Maven pom.xml to enable reporting:

```xml
<plugin>
  <groupId>com.cybernostics</groupId>
  <artifactId>jsp2tl-maven-plugin</artifactId>
  <version>1.0.0</version>
  <configuration>
    <generateReports>true</generateReports>
    <reportTypes>
      <reportType>html</reportType>
      <reportType>json</reportType>
      <reportType>summary</reportType>
    </reportTypes>
    <reportsOutputDir>${project.build.directory}/jsp2thymeleaf-reports</reportsOutputDir>
    <verboseReports>true</verboseReports>
  </configuration>
</plugin>
```

You can also generate reports separately from the conversion process:

```
mvn jsp2tl:report -DconversionDataDir=target/jsp2thymeleaf-data -DverboseReports=true
```

### Command-Line Interface

Use the following command-line options to generate reports:

```
java -jar jsp2thymeleaf.jar -s src/main/webapp -d target/classes/templates -r -rt html,json,summary -ro reports -v
```

Where:
- `-r` or `--generate-reports`: Enable report generation
- `-rt` or `--report-types`: Comma-separated list of report types (html, json, summary)
- `-ro` or `--reports-output-dir`: Directory where reports will be generated
- `-v` or `--verbose-reports`: Include detailed information in reports

## Report Types

### HTML Report

The HTML report provides a visual representation of the conversion process, including:
- Executive summary with key metrics
- Charts showing conversion statistics
- File-level details
- Tag conversion summaries
- Scriptlet analysis
- Issue list with categories and recommendations

### JSON Report

The JSON report contains the same information as the HTML report but in a machine-readable format, making it suitable for:
- Integration with other tools or dashboards
- Custom analysis and processing
- Automated workflows

### Summary Report

The summary report is a plain text report that provides a concise overview of the conversion process, including:
- Basic statistics (files processed, success rate, etc.)
- List of common issues
- Recommendations for manual intervention

## Scriptlet Handling

The reporting system tracks statistics about scriptlets found in JSP files and how they were handled. For more details on scriptlet handling configuration, see the [Scriptlet Handling Documentation](SCRIPTLET_HANDLING.md).

## API Reference

### ReportingService

The `ReportingService` class is the main entry point for generating reports:

```java
ReportingService reportingService = new ReportingService();

// Generate a report
reportingService.generateReport(conversionReport, 
                               ReportGeneratorType.HTML, 
                               outputPath, 
                               verbose);

// Save conversion data for later report generation
reportingService.saveConversionData(conversionReport, dataPath);

// Load conversion data from a previous run
ConversionReport report = reportingService.loadConversionData(dataPath);
```

### ConversionReport

The `ConversionReport` class is the core model that contains all conversion data:

```java
ConversionReport report = new ConversionReport();
report.setProjectName("My Project");
report.setTotalFiles(100);
report.setSuccessCount(95);
report.addConversionIssue("/path/to/file.jsp", "Error message", "ERROR");
```

## Examples

See the `examples` package for working examples of how to use the reporting system:

```java
import com.cybernostics.jsp2thymeleaf.reporting.examples.ReportingExample;

// Generate a sample report
ReportingExample.main(args);
```

## Contributing

Contributions to the reporting system are welcome. Please follow these guidelines:
1. Write unit tests for new features
2. Ensure backward compatibility
3. Document any new features or changes
4. Follow the existing code style and patterns