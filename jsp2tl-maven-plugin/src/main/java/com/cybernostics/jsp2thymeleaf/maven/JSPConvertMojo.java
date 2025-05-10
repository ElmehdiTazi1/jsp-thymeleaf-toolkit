package com.cybernostics.jsp2thymeleaf.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which converts JSP files into Thymeleaf. Files get put into the
 * Generated Sources folder
 *
 */
@Mojo(name = "convert", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = true)
public class JSPConvertMojo
        extends AbstractMojo {

    /**
     * Location of the generated sources.
     */
    @Parameter(defaultValue = "${project.build.directory}/classes/templates", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "true", property = "updateLinks", required = true)
    private Boolean updateLinks;

    @Parameter(defaultValue = "${project.basedir}/src/main/webapp/WEB-INF/jsp", required = true)
    private File srcDirectory;

    @Parameter(defaultValue = "${project.basedir}/src/main/jsp2thymeleaf", required = false)
    private File converterScriptDirectory;

    @Parameter(defaultValue = "com.cybernostics.jsp2thymeleaf.conveters.tld", required = false)
    private String taglibConverterPackages;

    /**
     * Enable generation of reports after conversion
     */
    @Parameter(defaultValue = "false", property = "generateReports", required = false)
    private Boolean generateReports;

    /**
     * Types of reports to generate: html, json, summary
     */
    @Parameter(property = "reportTypes", required = false)
    private String[] reportTypes = {"html", "json", "summary"};

    /**
     * Directory where reports will be generated
     */
    @Parameter(defaultValue = "${project.build.directory}/jsp2thymeleaf-reports", property = "reportsOutputDir", required = false)
    private File reportsOutputDir;

    /**
     * Option to handle scriptlets: COMMENT, THYMELEAF_COMMENT, or EXTRACT
     */
    @Parameter(defaultValue = "COMMENT", property = "scriptletHandlingStrategy", required = false)
    private String scriptletHandlingStrategy;

    public String getTaglibConverterPackages() {
        return taglibConverterPackages;
    }

    private static final String[] DEFAULT_INCLUDES
            = {
                "**/*.jsp", "**/*.jspx", "**/*.jspf"
            };

    @Parameter
    private String[] includes = DEFAULT_INCLUDES;

    @Parameter
    private String[] excludes = new String[0];

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public Boolean getUpdateLinks() {
        return updateLinks;
    }

    public File getSrcDirectory() {
        return srcDirectory;
    }

    public String[] getIncludes() {
        return includes;
    }

    public Boolean getGenerateReports() {
        return generateReports;
    }

    public String[] getReportTypes() {
        return reportTypes;
    }

    public File getReportsOutputDir() {
        return reportsOutputDir;
    }

    public String getScriptletHandlingStrategy() {
        return scriptletHandlingStrategy;
    }

    public void execute()
            throws MojoExecutionException {
        final Log log = getLog();
        log.info("includes:" + Arrays.toString(getIncludes()));
        log.info("outputDirectory = " + outputDirectory.getAbsolutePath());
        log.info("srcDirectory = " + srcDirectory.getAbsolutePath());
        log.info("updateLinks = " + updateLinks);
        log.info("taglib scripts folder " + converterScriptDirectory.toString());
        
        if (generateReports) {
            log.info("Report generation enabled");
            log.info("Report types: " + Arrays.toString(reportTypes));
            log.info("Reports output directory: " + reportsOutputDir.getAbsolutePath());
            log.info("Scriptlet handling strategy: " + scriptletHandlingStrategy);
            
            // Create reports directory if it doesn't exist
            if (!reportsOutputDir.exists()) {
                reportsOutputDir.mkdirs();
            }
        }
        
        // Get the scripts folder path as a String
        String converterScriptPath = converterScriptDirectory.exists() ? 
                                    converterScriptDirectory.getAbsolutePath() : "";
        
        JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration
                .getBuilder()
                .withIncludes(includes)
                .withExcludes(excludes)
                .withSrcFolder(srcDirectory.toString())
                .withDestFolder(outputDirectory.toString())
                .withConverterScripts(converterScriptPath) // Passing String path instead of String[]
                .withConverterPackages(taglibConverterPackages)
                .withGenerateReports(generateReports)
                .withReportTypes(reportTypes)
                .withReportsOutputDir(reportsOutputDir.toString())
                .withScriptletHandlingStrategy(scriptletHandlingStrategy)
                .build();
                
        JSP2Thymeleaf jSP2Thymeleaf = new JSP2Thymeleaf(config);
        try {
            final List<JSP2ThymeLeafException> exceptions = jSP2Thymeleaf.run();
            
            // Generate reports if configured
            if (generateReports) {
                log.info("Generating conversion reports...");
                jSP2Thymeleaf.generateReports();
                log.info("Reports generated successfully in " + reportsOutputDir.getAbsolutePath());
            }
            
            // Print summary of conversion results
            if (exceptions.isEmpty()) {
                log.info("JSP2Thymeleaf converted all files successfully.");
            } else {
                log.error("JSP2Thymeleaf had errors:");
                for (JSP2ThymeLeafException exception : exceptions) {
                    log.error(exception.getMessage());
                }
                throw new MojoExecutionException("Failed to convert all files");
            }
        } catch (MojoExecutionException exception) {
            throw exception;
        } catch (Throwable t) {
            log.error(t);
            throw t;
        }
    }

    private String[] scriptsInFolder(File converterScriptDirectory) {
        final Path scriptFolder = Paths.get(converterScriptDirectory.toURI());
        String[] templateArray = new String[0];
        if (scriptFolder != null && scriptFolder.toFile().exists()) {
            return Arrays.asList(converterScriptDirectory.list((dir, name)
                    -> {
                return name.endsWith(".groovy");
            })).stream()
                    .map(it -> scriptFolder.resolve(it).toAbsolutePath())
                    .collect(toList())
                    .toArray(templateArray);

        }
        return templateArray;
    }

}
