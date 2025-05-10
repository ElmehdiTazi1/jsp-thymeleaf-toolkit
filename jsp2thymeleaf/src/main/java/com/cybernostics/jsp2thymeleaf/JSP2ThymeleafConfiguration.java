/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.common.ScriptletHandlingStrategy;
import com.cybernostics.jsp2thymeleaf.api.util.SetUtils;
import com.cybernostics.jsp2thymeleaf.converters.AllJstlConverters;
import com.cybernostics.jsp2thymeleaf.util.Globber;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.EMPTY_LIST;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

/**
 * Contains parameters to control the conversion process.
 *
 * @author jason
 */
public class JSP2ThymeleafConfiguration
{

    private boolean showBanner;

    private Path srcFolder;
    private Path destFolder;
    private Path rootFolder;
    
    // Configuration pour les scriptlets
    private ScriptletHandlingStrategy scriptletStrategy = ScriptletHandlingStrategy.HTML_COMMENT;
    private Path scriptletExtractionFolder;

    private static final String[] EMPTY = new String[0];
    private String[] includes = EMPTY;
    private String[] excludes = EMPTY;
    private String[] filenames = EMPTY;

    private Set<Path> filesToProcess;

    private FileAttribute GROUP_WRITABLE = PosixFilePermissions.asFileAttribute(SetUtils.setOf(
            PosixFilePermission.GROUP_READ,
            PosixFilePermission.GROUP_WRITE,
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE
    ));
    private List<String> converterPackages = new ArrayList<>();

    private List<Path> converterScripts = EMPTY_LIST;

    // Reporting configuration
    private boolean generateReports = false;
    private String[] reportTypes = {"html", "json", "summary"};
    private Path reportsOutputDir;
    private boolean verboseReports = false;

    public Set<Path> getFilesToProcess()
    {
        processPathParameters();
        reportAnyMissingFiles();
        return filesToProcess;
    }

    private JSP2ThymeleafConfiguration()
    {
    }

    /**
     * Used for API access to the configuration
     */
    public static JSP2ThymeleafConfigurationBuilder getBuilder()
    {
        return new JSP2ThymeleafConfigurationBuilder();
    }

    public static JSP2ThymeleafConfiguration parse(String... args)
    {
        try
        {
            Options options = new Options();
            options.addOption("p", "taglib-converter-pkgs", true, "List of classpath packages containing additional taglib converters.");
            options.addOption("g", "taglib-converter-scripts", true, "The location of script files containing additional taglib converters.");
            options.addOption("s", "source-folder", true, "The location of the jsp files to convert.");
            options.addOption("d", "dest-folder", true, "The location of the jsp files to convert.");
            options.addOption("i", "includes", true, "Comma-separated list of ant patterns to include.\n  Default is *.jsp,*.jspx,*.jspf");
            options.addOption("e", "excludes", true, "Comma-separated list of ant patterns to exclude.\n  Default is empty");
            options.addOption("b", "show-banner", false, "Whether to add the JSP2thymeleaf banner to the JSPs");
            // Nouvelles options pour la gestion des scriptlets
            options.addOption("st", "scriptlet-strategy", true, "Strategy for handling scriptlets: HTML_COMMENT, THYMELEAF_COMMENT, EXTRACT_TO_FILE, or FAIL_ON_SCRIPTLET");
            options.addOption("ef", "extraction-folder", true, "Folder where to extract scriptlets when using EXTRACT_TO_FILE strategy");
            
            // Reporting options
            options.addOption("r", "generate-reports", false, "Generate reports after conversion");
            options.addOption("rt", "report-types", true, "Comma-separated list of report types: html,json,summary");
            options.addOption("ro", "reports-output-dir", true, "Directory where to write the reports");
            options.addOption("v", "verbose-reports", false, "Include verbose details in reports");
            
            final JSP2ThymeleafConfiguration config = new JSP2ThymeleafConfiguration();

            Parser parser = new PosixParser();
            final CommandLine parsedArgs = parser.parse(options, args);
            config.destFolder = Paths.get(parsedArgs.getOptionValue("d", "")).toAbsolutePath();
            config.srcFolder = Paths.get(parsedArgs.getOptionValue("s", "")).toAbsolutePath();
            config.includes = parsedArgs.getOptionValue("i", "**/*.jsp,**/*.jspx,**/*.jspf").split(",");
            config.excludes = parsedArgs.getOptionValue("e", "").split(",");
            config.showBanner = parsedArgs.hasOption("b");
            config.filenames = parsedArgs.getArgs();
            config.converterPackages.addAll(
                    Arrays.stream(parsedArgs.getOptionValue("p", "")
                            .split(","))
                            .filter(it -> it.length() > 0)
                            .collect(toList()));

            if (parsedArgs.hasOption("g")) {
                config.converterScripts = asList(parsedArgs.getOptionValue("g").split(",")).stream().map(path -> Paths.get(path)).collect(toList());
            } else {
                config.converterScripts = EMPTY_LIST;
            }
            
            // Traitement des options de scriptlet
            if (parsedArgs.hasOption("st")) {
                try {
                    config.scriptletStrategy = ScriptletHandlingStrategy.valueOf(parsedArgs.getOptionValue("st"));
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid scriptlet strategy: " + parsedArgs.getOptionValue("st") + 
                            ". Using default: " + config.scriptletStrategy);
                }
            }
            
            if (parsedArgs.hasOption("ef")) {
                config.scriptletExtractionFolder = Paths.get(parsedArgs.getOptionValue("ef")).toAbsolutePath();
            }

            // Parsing reporting options
            config.generateReports = parsedArgs.hasOption("r");
            if (parsedArgs.hasOption("rt")) {
                config.reportTypes = parsedArgs.getOptionValue("rt").split(",");
            }
            if (parsedArgs.hasOption("ro")) {
                config.reportsOutputDir = Paths.get(parsedArgs.getOptionValue("ro")).toAbsolutePath();
            } else {
                // Default output directory is a "reports" folder in the destination directory
                config.reportsOutputDir = config.destFolder.resolve("reports");
            }
            config.verboseReports = parsedArgs.hasOption("v");
            
            // Create reports directory if reports are enabled
            if (config.generateReports && config.reportsOutputDir != null) {
                config.ensureExists(config.reportsOutputDir);
            }

            AllJstlConverters.init();
            return config;
        } catch (ParseException ex)
        {
            Logger.getLogger(JSP2ThymeleafConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException("Error parsing command line:" + ex.getMessage(), ex);
        }
    }

    public boolean isShowBanner()
    {
        return showBanner;
    }
    
    /**
     * Indicates if reports should be generated after conversion
     * @return true if reports should be generated, false otherwise
     */
    public boolean isGenerateReports() {
        return generateReports;
    }
    
    /**
     * Gets the types of reports to generate
     * @return Array of report types (html, json, summary)
     */
    public String[] getReportTypes() {
        return reportTypes;
    }
    
    /**
     * Gets the output directory for generated reports
     * @return Output directory path
     */
    public Path getReportsOutputDir() {
        return reportsOutputDir;
    }
    
    /**
     * Indicates if verbose reporting is enabled
     * @return true if verbose, false otherwise
     */
    public boolean isVerboseReports() {
        return verboseReports;
    }

    public Path getSrcFolder()
    {
        return srcFolder;
    }

    public Path getDestFolder()
    {
        return destFolder;
    }

    public String[] getIncludes()
    {
        return includes;
    }

    public String[] getExcludes()
    {
        return excludes;
    }

    public String[] getFilenames()
    {
        return filenames;
    }

    public Path getOutputPathFor(Path inputFile)
    {
        String relPath = rootFolder.relativize(inputFile).toString();
        String outname = relPath.replaceAll("\\.jspx?$", ".html");
        Path outpath = destFolder.resolve(outname);
        ensureExists(outpath.getParent());
        return outpath;
    }

    private void processPathParameters()
    {
        if (srcFolder != null) {
            rootFolder = srcFolder;
            filesToProcess = Globber.run(srcFolder, includes, excludes).stream().map(name -> srcFolder.resolve(name)).collect(Collectors.toSet());
        } else {
            Set<Path> files = stream(filenames).map(it -> Paths.get(it).toAbsolutePath()).collect(Collectors.toSet());
            if (!files.isEmpty()) {
                Path parent = files.iterator().next().getParent();
                rootFolder = parent != null ? parent : Paths.get("");
                if (!destFolder.toFile().exists()) {
                    ensureExists(destFolder);
                }
                filesToProcess = files;
            }
        }
    }

    private void reportAnyMissingFiles()
    {
        final List<Path> missingFiles = filesToProcess
                .stream()
                .filter(it -> !it.toFile().exists())
                .collect(toList());
        if (!missingFiles.isEmpty())
        {
            throw new IllegalArgumentException("File(s) not found:"
                    + missingFiles
                            .stream()
                            .map(Path::toString)
                            .collect(Collectors.joining(" , "))
            );
        }
    }

    private void ensureExists(Path destFolder)
    {
        if (!destFolder.toFile().exists())
        {
            try
            {
                Files.createDirectories(destFolder);
            } catch (IOException ex)
            {
                Logger.getLogger(JSP2ThymeleafConfiguration.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Can't create directory:" + destFolder, ex);
            }
        }
    }

    public Path getRootFolder()
    {
        return rootFolder;
    }

    public List<String> getConverterPackages()
    {
        return converterPackages;
    }

    public List<Path> getConverterScripts()
    {
        return converterScripts;
    }
    
    /**
     * Obtient la stratégie de traitement des scriptlets configurée
     * 
     * @return Stratégie de traitement des scriptlets
     */
    public ScriptletHandlingStrategy getScriptletStrategy() {
        return scriptletStrategy;
    }
    
    /**
     * Obtient le dossier d'extraction des scriptlets configuré
     * 
     * @return Dossier d'extraction des scriptlets ou null si non configuré
     */
    public Path getScriptletExtractionFolder() {
        return scriptletExtractionFolder;
    }

    public static class JSP2ThymeleafConfigurationBuilder
    {

        private JSP2ThymeleafConfiguration configuration = new JSP2ThymeleafConfiguration();

        public JSP2ThymeleafConfigurationBuilder()
        {
        }

        public JSP2ThymeleafConfigurationBuilder withIncludes(String... includes)
        {
            configuration.includes = includes;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withExcludes(String... excludes)
        {
            configuration.excludes = excludes;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withDestFolder(String destFolder)
        {
            configuration.destFolder = Paths.get(destFolder).toAbsolutePath();;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withSrcFolder(String srcFolder)
        {
            configuration.srcFolder = Paths.get(srcFolder).toAbsolutePath();;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withShowBanner(Boolean showBanner)
        {
            configuration.showBanner = showBanner;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withFileNames(String... filenames)
        {
            configuration.filenames = filenames;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withConverterPackages(String... packages)
        {
            configuration.converterPackages.addAll(asList(packages));
            return this;
        }
        
        /**
         * Configure the converter scripts to use for processing
         * 
         * @param scriptPath Path to converter script directory or file
         * @return The builder for chaining
         */
        public JSP2ThymeleafConfigurationBuilder withConverterScripts(String scriptPath) {
            configuration.converterScripts = Arrays.asList(Paths.get(scriptPath));
            return this;
        }
        
        /**
         * Configure la stratégie de traitement des scriptlets
         * 
         * @param strategy Stratégie à utiliser
         * @return Le builder pour chaînage
         */
        public JSP2ThymeleafConfigurationBuilder withScriptletStrategy(ScriptletHandlingStrategy strategy) {
            configuration.scriptletStrategy = strategy;
            return this;
        }
        
        /**
         * Configure le dossier d'extraction des scriptlets pour la stratégie EXTRACT_TO_FILE
         * 
         * @param extractionFolder Dossier d'extraction
         * @return Le builder pour chaînage
         */
        public JSP2ThymeleafConfigurationBuilder withScriptletExtractionFolder(String extractionFolder) {
            configuration.scriptletExtractionFolder = Paths.get(extractionFolder).toAbsolutePath();
            return this;
        }

        /**
         * Configure whether to generate reports
         * 
         * @param generateReports true to generate reports, false otherwise
         * @return The builder for chaining
         */
        public JSP2ThymeleafConfigurationBuilder withGenerateReports(Boolean generateReports) {
            configuration.generateReports = generateReports;
            return this;
        }
        
        /**
         * Configure the types of reports to generate
         * 
         * @param reportTypes Array of report types (html, json, summary)
         * @return The builder for chaining
         */
        public JSP2ThymeleafConfigurationBuilder withReportTypes(String[] reportTypes) {
            configuration.reportTypes = reportTypes;
            return this;
        }
        
        /**
         * Configure the output directory for reports
         * 
         * @param reportsOutputDir Directory path for report output
         * @return The builder for chaining
         */
        public JSP2ThymeleafConfigurationBuilder withReportsOutputDir(String reportsOutputDir) {
            configuration.reportsOutputDir = Paths.get(reportsOutputDir).toAbsolutePath();
            return this;
        }
        
        /**
         * Configure whether to include verbose details in reports
         * 
         * @param verboseReports true for verbose reports, false otherwise
         * @return The builder for chaining
         */
        public JSP2ThymeleafConfigurationBuilder withVerboseReports(Boolean verboseReports) {
            configuration.verboseReports = verboseReports;
            return this;
        }
        
        /**
         * Configure the scriptlet handling strategy using a string name
         * 
         * @param strategyName String name of the strategy
         * @return The builder for chaining
         */
        public JSP2ThymeleafConfigurationBuilder withScriptletHandlingStrategy(String strategyName) {
            try {
                configuration.scriptletStrategy = ScriptletHandlingStrategy.valueOf(strategyName);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid scriptlet strategy: " + strategyName + 
                        ". Using default: " + configuration.scriptletStrategy);
            }
            return this;
        }

        public JSP2ThymeleafConfiguration build()
        {
            AllJstlConverters.init();
            return configuration;
        }
    }

}
