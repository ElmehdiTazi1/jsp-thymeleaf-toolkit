
# Jsp2Thymeleaf - An Extensible JSP to Thymeleaf Converter

This project is intended to automate over 95% of JSP to Thymeleaf conversion.

## Overview

It features:

1) A great JSP parser thanks to the excellent jsp2jspx project which I uncovered on
an archeological SourceForge dig and resurrected from Subversion.

2) A converter framework allowing you to add converters for your own 
tag libraries in Java, Groovy or Python.

3) A configurable Maven plugin to automate the conversion of pages either
entirely or more sensibly, page by page or fragment by fragment. Used in
conjunction with the com.cybernostics:spring-thymeleaf-jsp library (which allows
JSP pages to work alongside Thymeleaf, even including Thymeleaf fragments), you can
run and test your project at any stage in the conversion.

## Development Roadmap

A detailed project roadmap is available in the [ROADMAP.md](ROADMAP.md) file, which outlines:

- Phase 1: Analysis and Stabilization (2 weeks)
- Phase 2: Refactoring and Enhancement (3 weeks)
- Phase 3: Documentation (2 weeks) 
- Phase 4: Integration and Deployment (1 week)

The roadmap includes specific objectives, deliverables, resources, validation criteria, and risk management for each phase.

## Running It

### From the Command Line

```bash
java -jar jsp2thymeleaf-1.0.0-SNAPSHOT.jar [src_files|*.jsp] [destpath|.] --taglibs=[pathlist] --urimap=[path]
```

Where:
- `destpath` - The path where converted files will be saved
- `taglibs` - A list of taglib converters (*.groovy or *.py)
- `urimap` - A map of custom URIs used to select taglibs from your JSPs

### Using the Maven Plugin

Include the `jsp2tl-maven-plugin` in your project's `pom.xml`:

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <src>${project.basedir}/src/main/webapp</src>
        <dest>${project.build.directory}/converted</dest>
        <includes>
            <include>**/*.jsp</include>
        </includes>
        <excludes>
            <exclude>**/excluded/*.jsp</exclude>
        </excludes>
    </configuration>
</plugin>
```

Run the plugin with:

```bash
mvn jsp2tl:convert
```

#JSP coverage


#JSTL coverage

|Done?| Tag                       |
|-----|---------------------------|
| No  | c:catch                   |
| No  | c:choose                  |
| Yes | c:forEach                 |
| No  | c:forTokens               |
| Yes | c:if                      |
| No  | c:import                  |
| No  | c:otherwise               |
| Yes | c:out                     |
| No  | c:param                   |
| No  | c:redirect                |
| No  | c:remove                  |
| No  | c:set                     |
| No  | c:url                     |
| No  | c:when                    |
| No  | fmt:bundle                |
| No  | fmt:formatDate            |
| No  | fmt:formatNumber          |
| No  | fmt:message               |
| No  | fmt:param                 |
| No  | fmt:parseDate             |
| No  | fmt:parseNumber           |
| No  | fmt:requestEncoding       |
| No  | fmt:setBundle             |
| No  | fmt:setLocale             |
| No  | fmt:setTimeZone           |
| No  | fmt:timeZone              |
| No  | fn:contains()             |
| No  | fn:containsIgnoreCase()   |
| No  | fn:endsWith()             |
| No  | fn:escapeXml()            |
| No  | fn:indexOf()              |
| No  | fn:join()                 |
| No  | fn:length()               |
| No  | fn:replace()              |
| No  | fn:split()                |
| No  | fn:startsWith()           |
| No  | fn:substring()            |
| No  | fn:substringAfter()       |
| No  | fn:substringBefore()      |
| No  | fn:toLowerCase()          |
| No  | fn:toUpperCase()          |
| No  | fn:trim()                 |
| No  | sql:dateParam             |
| No  | sql:param                 |
| No  | sql:query                 |
| No  | sql:setDataSource         |
| No  | sql:transaction           |
| No  | sql:update                |
| No  | x:choose                  |
| No  | x:forEach                 |
| No  | x:if                      |
| No  | x:otherwise               |
| No  | x:out                     |
| No  | x:param                   |
| No  | x:parse                   |
| No  | x:set                     |
| No  | x:transform               |
| No  | x:when                    |