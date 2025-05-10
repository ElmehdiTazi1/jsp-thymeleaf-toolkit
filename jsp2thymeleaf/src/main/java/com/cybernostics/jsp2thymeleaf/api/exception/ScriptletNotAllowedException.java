package com.cybernostics.jsp2thymeleaf.api.exception;

import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException.JSP2ThymeLeafExceptionBuilder;
import com.cybernostics.jsp2thymeleaf.api.exception.DefaultFileErrorLocation;
import com.cybernostics.jsp2thymeleaf.api.exception.HasErrorLocation;

/**
 * Exception spécifique levée lorsqu'un scriptlet est rencontré dans un mode de conversion
 * où les scriptlets ne sont pas autorisés.
 * 
 * @author elmehdi.tazi
 */
public class ScriptletNotAllowedException extends JSP2ThymeLeafException {
    
    /**
     * Constructeur pour l'exception de scriptlet non autorisé.
     * 
     * @param message Message d'erreur
     * @param filePath Chemin du fichier contenant le scriptlet
     * @param line Numéro de ligne du scriptlet
     * @param column Position dans la ligne du scriptlet
     */
    public ScriptletNotAllowedException(String message, String filePath, int line, int column) {
        super(message);
        // Use the proper way to set location information based on parent class
        setLocationSource(() -> new DefaultFileErrorLocation(filePath, line, column));
    }
    
    /**
     * Builder pattern pour créer une ScriptletNotAllowedException
     * 
     * @param message Message d'erreur
     * @return Un builder pour l'exception
     */
    public static JSP2ThymeLeafExceptionBuilder scriptletNotAllowedExceptionBuilder(String message) {
        return new JSP2ThymeLeafExceptionBuilder(message);
    }
}