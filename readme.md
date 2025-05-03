
# JSP2Thymeleaf - An Extensible JSP to Thymeleaf Converter

## Project Overview

JSP2Thymeleaf is an advanced toolkit designed to automate the conversion of JSP (JavaServer Pages) applications to Thymeleaf templates. It aims to achieve a 95% automation rate in the conversion process, making migration from JSP to Thymeleaf significantly easier and more manageable.

### Key Features

- Automated conversion of JSP files to Thymeleaf templates
- Support for incremental migration (page-by-page or fragment-by-fragment)
- Extensible framework for custom taglib converters
- Maven plugin for automated conversion
- Coexistence support for JSP and Thymeleaf during migration

## Architecture

The project consists of several interconnected modules that work together to provide a complete conversion solution:

### Core Modules

1. **jsp-parser**: 
   - Handles lexical analysis and parsing of JSP files
   - Creates Abstract Syntax Tree (AST) for JSP documents
   - Key components: `JSPLexer`, `JSPParser`, `AntlrJSPListener`

2. **jsp2thymeleaf-api**:
   - Core conversion interfaces and utilities 
   - Defines converter APIs and extension points
   - Manages common namespaces and conversions

3. **jsp2thymeleaf**:
   - Main conversion engine
   - Transforms JSP elements to Thymeleaf equivalents
   - Handles expression conversion and template generation

4. **spring-thymeleaf-jsp**:
   - Enables JSP and Thymeleaf coexistence
   - Provides compatibility layer during migration
   - Allows including Thymeleaf fragments in JSP pages

### Extension Modules

5. **jsp2thymeleaf-converters-spring**:
   - Spring-specific tag converters
   - Handles Spring form tags and bindings

6. **jsp2thymeleaf-tldgen**:
   - Generates converter templates for custom taglibs
   - Supports creation of new tag converters

7. **jsp2tl-maven-plugin**:
   - Maven integration for automated conversion
   - Configurable conversion process

## Module Breakdown

### jsp-parser
- **Purpose**: Parse JSP files into manageable AST
- **Key Classes**: 
  - `JSPLexer`: Tokenizes JSP input
  - `JSPParser`: Creates parse tree
  - `JSPParserBaseListener`: Base class for AST traversal
- **Dependencies**: ANTLR4 runtime

### jsp2thymeleaf
- **Purpose**: Core conversion engine
- **Key Classes**:
  - `JSP2Thymeleaf`: Main entry point
  - `JSP2ThymeleafTransformerListener`: Converts JSP nodes to Thymeleaf
  - `JSP2ThymeleafFileConverter`: Handles file-level conversion
- **Dependencies**: jsp-parser, jsp2thymeleaf-api

## Workflows

### JSP Parsing Process
1. Input JSP file is tokenized by `JSPLexer`
2. `JSPParser` creates parse tree
3. `JSP2ThymeleafTransformerListener` walks the tree
4. AST nodes are converted to Thymeleaf elements

### Conversion Process
1. Configuration is loaded (`JSP2ThymeleafConfiguration`)
2. Source files are identified and tokenized
3. Each file is parsed and transformed
4. Converters are applied based on tag type
5. Output is written as Thymeleaf template

### Extension Mechanism
1. Implement `ConverterRegistration` interface
2. Create custom tag converters
3. Register converters via service loader
4. Package as JAR with service descriptor

## Maven Project Structure

```xml
<modules>
    <module>jsp-parser</module>
    <module>jsp2thymeleaf-api</module>
    <module>jsp2thymeleaf</module>
    <module>jsp2thymeleaf-converters-spring</module>
    <module>jsp2thymeleaf-tldgen</module>
    <module>jsp2tl-maven-plugin</module>
    <module>spring-thymeleaf-jsp</module>
</modules>
```

## Extension Mechanisms

### Custom Taglib Converters
1. Use jsp2thymeleaf-tldgen to generate converter template
2. Implement `TagConverterSource` for tag handling
3. Define conversion rules in converter class
4. Register via service loader mechanism

## Design Patterns Used

1. **Visitor Pattern**: Used in AST traversal
2. **Builder Pattern**: Configuration and converter building
3. **Strategy Pattern**: Converter implementations
4. **Factory Pattern**: Creating converter instances
5. **Observer Pattern**: Processing AST events

## Testing Approach

- Unit tests for individual converters
- Integration tests for complete conversions
- Sample project for validation
- Automated testing via Maven

## Implementation Challenges

1. **Complex JSP Features**:
   - Nested tags handling
   - Expression language conversion
   - Custom tag libraries

2. **Edge Cases**:
   - Scriptlet conversion
   - Complex EL expressions
   - Custom tag attributes

## Project Completeness

### Well-Supported Features
- Basic JSP tag conversion
- JSTL core tags
- Spring form tags
- Simple expressions
- Fragment inclusion

### Areas Needing Work
- Complex scriptlets
- Advanced custom tags
- Dynamic includes
- Complex EL expressions

## Usage

### Command Line
 java -jar Jsp2Thymeleaf [src_files|*.jsp] [destpath|.] --taglibs=[pathlist] --urimap=[path]
 
 where:
  destpath - is the path where the converted files will end up
  taglibs - a list of taglib converters, either *.groovy, or *.py.
  urimap - is a map of any custom uri's you use to select taglibs from your jsps   

  ii) From the maven plugin
  Include the com.cybernostics:maven-jsp2thymeleaf plugin:

        <plugin>
            <artifactId>maven-jsp2thymeleaf</artifactId>
            <groupId>com.cybernostics</groupId>
            <configuration>
                <src></src>
                <dest>${build.output.path}</dest>
                <includes>
                   <include>MyPage.jsp</include>
                </includes>
                <excludes>
                   <exclude>MyOtherPage.jsp</exclude>
                </excludes>
            </configuration>
        </plugin>

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