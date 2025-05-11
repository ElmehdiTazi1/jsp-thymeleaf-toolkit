/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeleafFileConverter;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;

/**
 * Point d'entrée principal de l'application JSP2Thymeleaf lorsqu'elle est exécutée
 * depuis la ligne de commande.
 * 
 * Cette classe est responsable de:
 * <ul>
 *   <li>L'initialisation du processus de conversion</li>
 *   <li>La récupération et le filtrage des fichiers JSP à traiter</li>
 *   <li>La gestion du processus de conversion pour chaque fichier</li>
 *   <li>La collecte et le rapport des erreurs rencontrées</li>
 * </ul>
 * 
 * Le flux d'exécution typique est:
 * <ol>
 *   <li>Création d'une configuration ({@link JSP2ThymeleafConfiguration})</li>
 *   <li>Instanciation de cette classe avec la configuration</li>
 *   <li>Appel de la méthode {@link #run()}</li>
 *   <li>Traitement de toutes les exceptions via {@link #getErrorReport()}</li>
 * </ol>
 * 
 * @author jason
 * @version 1.0
 * @see JSP2ThymeleafConfiguration
 * @see JSP2ThymeleafFileConverter
 */
public class JSP2Thymeleaf
{
    /**
     * Point d'entrée du programme lorsqu'il est exécuté depuis la ligne de commande.
     * 
     * @param args Arguments de la ligne de commande qui seront analysés par 
     *             {@link JSP2ThymeleafConfiguration#parse(String[])}
     */
    public static void main(String[] args) {
        final JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.parse(args);
        final JSP2Thymeleaf jsP2Thymeleaf = new JSP2Thymeleaf(config);
        jsP2Thymeleaf.run();
    }

    /** Logger pour cette classe */
    private static final Logger logger = Logger.getLogger(JSP2Thymeleaf.class.getName());
    
    /** Configuration de l'exécution actuelle */
    private JSP2ThymeleafConfiguration configuration;
    
    /** Ensemble des fichiers déjà traités pour éviter les doublons */
    private Set<TokenisedFile> alreadyProcessed = new TreeSet<>();
    
    /** Liste des exceptions rencontrées pendant le traitement */
    private List<JSP2ThymeLeafException> exceptions;
    
    /** Convertisseur responsable de transformer chaque fichier JSP */
    private JSP2ThymeleafFileConverter converter;    /**
     * Map des fichiers tokenisés indexés par leur chemin
     */
    private Map<Path, TokenisedFile> tokenisedFilesMap;

    /**
     * Constructeur initialisant le convertisseur avec la configuration spécifiée.
     * 
     * @param configuration La configuration à utiliser pour cette instance de convertisseur
     */
    public JSP2Thymeleaf(JSP2ThymeleafConfiguration configuration)
    {
        this.configuration = configuration;
        converter = new JSP2ThymeleafFileConverter(configuration);
        converter.setShowBanner(configuration.isShowBanner());
        exceptions = new ArrayList<>();
    }

    /**
     * Exécute le processus de conversion pour tous les fichiers spécifiés
     * dans la configuration.
     * 
     * Cette méthode:
     * <ol>
     *   <li>Récupère tous les fichiers à traiter depuis la configuration</li>
     *   <li>Convertit chaque fichier en TokenisedFile pour traitement</li> 
     *   <li>Lance la conversion pour chaque fichier</li>
     *   <li>Collecte et retourne toutes les erreurs rencontrées</li>
     * </ol>
     * 
     * @return Une liste d'exceptions rencontrées pendant la conversion
     */
    public List<JSP2ThymeLeafException> run()
    {
        tokenisedFilesMap = configuration
                .getFilesToProcess()
                .stream()
                .map(path -> new TokenisedFile(path, configuration.getRootFolder()))
                .sorted()
                .collect(toMap(TokenisedFile::getPath, it -> it));

        return tokenisedFilesMap
                .values()
                .stream()
                .sorted()
                .flatMap(eachInputFile -> convertFile(eachInputFile).stream())
                .collect(Collectors.toList());
    }    /**
     * Version simplifiée de convertFile qui appelle la version complète avec un scope parent null.
     * 
     * @param eachInputFile Le fichier à convertir
     * @return Une liste d'exceptions rencontrées pendant la conversion
     */
    private List<JSP2ThymeLeafException> convertFile(TokenisedFile eachInputFile)
    {
        return convertFile(eachInputFile, null);
    }

    /**
     * Convertit un fichier JSP en fichier Thymeleaf.
     * 
     * Cette méthode gère la vérification des fichiers déjà traités,
     * la création du fichier de sortie, et l'appel du convertisseur.
     * 
     * @param fileToConvert Le fichier à convertir
     * @param parentScope Le contexte de conversion parent (peut être null)
     * @return Une liste d'exceptions rencontrées pendant la conversion
     */
    private List<JSP2ThymeLeafException> convertFile(TokenisedFile fileToConvert, ScopedJSPConverters parentScope)
    {
        ScopedJSPConverters myConverter = new ScopedJSPConverters(parentScope);
        List<JSP2ThymeLeafException> exceptions = new ArrayList<>();
        if (!alreadyProcessed.contains(fileToConvert))
        {
            final File outputFilePath = configuration.getOutputPathFor(fileToConvert.getFilePath()).toFile();
            try
            {
                logger.log(Level.INFO, "JSP2Thymeleaf processing:" + fileToConvert.toString());
                exceptions.addAll(converter.convert(fileToConvert, outputFilePath, myConverter));
                List<TokenisedFile> includedFiles = fileToConvert.getIncludedPaths()
                        .stream()
                        .map(path -> tokenisedFilesMap.get(path))
                        .collect(Collectors.toList());
                exceptions.addAll(
                        includedFiles.stream()
                                .flatMap(includedFile -> convertFile(includedFile, myConverter).stream())
                                .collect(Collectors.toList()));
            } catch (Throwable exception)
            {
                exceptions.add(JSP2ThymeLeafException.jsp2ThymeLeafExceptionBuilder(exception).build());
            } finally
            {
                alreadyProcessed.add(fileToConvert);
            }

        }
        return exceptions;
    }

}
