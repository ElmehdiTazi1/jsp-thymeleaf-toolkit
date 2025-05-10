package com.cybernostics.jsp2thymeleaf.reporting.api;

import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionIssue;
import com.cybernostics.jsp2thymeleaf.reporting.model.ConversionReport;
import com.cybernostics.jsp2thymeleaf.reporting.model.FileConversionSummary;
import com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletInfo;
import com.cybernostics.jsp2thymeleaf.reporting.model.TagConversionInfo;

/**
 * Interface définissant les méthodes pour collecter les données pendant le processus
 * de conversion JSP vers Thymeleaf.
 * Respecte le principe Interface Segregation en fournissant une interface ciblée.
 */
public interface ReportCollector {
    
    /**
     * Initialise un nouveau rapport avec les métadonnées du projet.
     * 
     * @param projectName Nom du projet
     * @param projectVersion Version du projet
     * @return Le rapport initialisé
     */
    ConversionReport initializeReport(String projectName, String projectVersion);
    
    /**
     * Démarre la conversion d'un fichier et crée un résumé.
     * 
     * @param filePath Le chemin du fichier en cours de conversion
     * @return Le résumé de conversion initialisé
     */
    FileConversionSummary startFileConversion(String filePath);
    
    /**
     * Complète le résumé de conversion d'un fichier avec toutes les données collectées.
     * 
     * @param summary Le résumé à compléter et enregistrer dans le rapport
     */
    void completeFileConversion(FileConversionSummary summary);
    
    /**
     * Enregistre une information sur la conversion d'une balise JSP.
     * 
     * @param tagInfo Les informations sur la conversion de la balise
     */
    void recordTagConversion(TagConversionInfo tagInfo);
    
    /**
     * Enregistre une information sur le traitement d'un scriptlet JSP.
     * 
     * @param scriptletInfo Les informations sur le traitement du scriptlet
     */
    void recordScriptletProcessing(ScriptletInfo scriptletInfo);
    
    /**
     * Enregistre un problème rencontré pendant la conversion.
     * 
     * @param issue Le problème à enregistrer
     */
    void recordIssue(ConversionIssue issue);
    
    /**
     * Complète le rapport de conversion en calculant les statistiques
     * finales et en définissant l'heure de fin.
     * 
     * @return Le rapport de conversion complété
     */
    ConversionReport completeReport();
    
    /**
     * Renvoie le rapport de conversion en cours.
     * 
     * @return Le rapport de conversion actuel
     */
    ConversionReport getCurrentReport();
}