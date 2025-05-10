package com.cybernostics.jsp2thymeleaf.reporting.maven;

import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.reporting.instrumentation.InstrumentedJSP2Thymeleaf;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Goal Maven qui convertit les fichiers JSP en Thymeleaf avec instrumentation pour générer des rapports.
 * Cette classe remplace la classe JSPConvertMojo standard en ajoutant des fonctionnalités de reporting.
 */
@Mojo(name = "convert-with-reports", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = true)
public class JSPConvertInstrumentedMojo extends AbstractMojo {
    
    /**
     * Répertoire contenant les fichiers JSP à convertir.
     */
    @Parameter(property = "jsp2thymeleaf.sourceDirectory", defaultValue = "${project.basedir}/src/main/webapp")
    private File sourceDirectory;
    
    /**
     * Répertoire où les fichiers Thymeleaf seront générés.
     */
    @Parameter(property = "jsp2thymeleaf.outputDirectory", defaultValue = "${project.build.directory}/generated-sources/thymeleaf")
    private File outputDirectory;
    
    /**
     * Liste des packages contenant des convertisseurs de taglibs.
     */
    @Parameter(defaultValue = "com.cybernostics.jsp2thymeleaf.conveters.tld", required = false)
    private String taglibConverterPackages;
    
    /**
     * Répertoire contenant les scripts de convertisseurs personnalisés.
     */
    @Parameter(property = "jsp2thymeleaf.converterScriptDirectory")
    private File converterScriptDirectory;
    
    /**
     * Motifs d'inclusion pour les fichiers JSP. 
     */
    @Parameter
    private String[] includes = {"**/*.jsp", "**/*.jspx", "**/*.jspf"};

    /**
     * Motifs d'exclusion pour les fichiers JSP.
     */
    @Parameter
    private String[] excludes = {};
    
    /**
     * Afficher une bannière dans les fichiers générés.
     */
    @Parameter(property = "jsp2thymeleaf.showBanner", defaultValue = "true")
    private boolean showBanner;
    
    /**
     * Échouer en cas d'erreur de conversion.
     */
    @Parameter(property = "jsp2thymeleaf.failOnError", defaultValue = "true")
    private boolean failOnError;
    
    /**
     * Répertoire où les rapports de conversion seront générés.
     */
    @Parameter(property = "jsp2thymeleaf.reportsDirectory", defaultValue = "${project.build.directory}/jsp2thymeleaf-reports")
    private File reportsDirectory;
    
    /**
     * Types de rapports à générer (html, json, summary)
     */
    @Parameter(property = "jsp2thymeleaf.reportTypes", defaultValue = "html,json,summary")
    private String reportTypes;
    
    /**
     * Activer ou désactiver la génération de rapports
     */
    @Parameter(property = "jsp2thymeleaf.generateReports", defaultValue = "true")
    private boolean generateReports;
    
    @Override
    public void execute() throws MojoExecutionException {
        try {
            getLog().info("Conversion JSP vers Thymeleaf avec génération de rapports");
            
            // Configuration du convertisseur
            JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.getBuilder()
                    .withShowBanner(showBanner)
                    .withSrcFolder(sourceDirectory.toString())
                    .withDestFolder(outputDirectory.toString())
                    .withConverterScripts(scriptsInFolder(converterScriptDirectory))
                    .withConverterPackages(taglibConverterPackages.split(","))
                    .withIncludes(includes)
                    .withExcludes(excludes)
                    .build();
            
            // Utiliser notre version instrumentée
            InstrumentedJSP2Thymeleaf converter = new InstrumentedJSP2Thymeleaf(config);
            
            // Exécuter la conversion
            final List<JSP2ThymeLeafException> exceptions = converter.run();
            
            // Gérer les exceptions
            if (exceptions.isEmpty()) {
                getLog().info("JSP2Thymeleaf a converti tous les fichiers avec succès.");
            } else {
                getLog().error("JSP2Thymeleaf a rencontré des erreurs:");
                for (JSP2ThymeLeafException exception : exceptions) {
                    getLog().error(exception.getMessage());
                }
                
                if (failOnError) {
                    throw new MojoExecutionException("Échec de conversion de certains fichiers");
                }
            }
            
            // Générer les rapports si activé
            if (generateReports) {
                getLog().info("Génération des rapports de conversion...");
                reportsDirectory.mkdirs();
                
                try {
                    List<Path> generatedReports = converter.generateReports(reportsDirectory.getAbsolutePath());
                    
                    getLog().info("Rapports générés:");
                    for (Path reportPath : generatedReports) {
                        getLog().info(" - " + reportPath);
                    }
                } catch (IOException e) {
                    getLog().error("Erreur lors de la génération des rapports", e);
                    if (failOnError) {
                        throw new MojoExecutionException("Échec de la génération des rapports", e);
                    }
                }
            }
            
        } catch (MojoExecutionException exception) {
            throw exception;
        } catch (Throwable t) {
            getLog().error(t);
            throw new MojoExecutionException("Erreur inattendue pendant la conversion", t);
        }
    }
    
    /**
     * Récupère les scripts de convertisseurs dans un dossier.
     * 
     * @param dir Le répertoire contenant les scripts
     * @return Un tableau de chemins de scripts
     */
    private String[] scriptsInFolder(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return new String[0];
        }
        
        File[] files = dir.listFiles((f) -> f.isFile() && 
                                           (f.getName().endsWith(".groovy") || 
                                            f.getName().endsWith(".py") || 
                                            f.getName().endsWith(".js")));
        
        if (files == null) {
            return new String[0];
        }
        
        String[] result = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            result[i] = files[i].getAbsolutePath();
        }
        
        return result;
    }
}