package com.cybernostics.jsp2thymeleaf.api.common;

/**
 * Énumération des stratégies possibles pour le traitement des scriptlets JSP.
 * Conforme au principe SOLID de responsabilité unique (SRP) en encapsulant
 * uniquement les différentes stratégies possibles.
 * 
 * @author elmehdi.tazi
 */
public enum ScriptletHandlingStrategy {
    /**
     * Les scriptlets sont convertis en commentaires HTML
     * <!-- Original: <% code %> -->
     */
    HTML_COMMENT,
    
    /**
     * Les scriptlets sont convertis en commentaires Thymeleaf
     * Format: "/&#42;[[ Original: <% code %> ]]&#42;/"
     */
    THYMELEAF_COMMENT,
    
    /**
     * Les scriptlets sont extraits dans des fichiers séparés pour traitement manuel
     */
    EXTRACT_TO_FILE,
    
    /**
     * Les scriptlets génèrent une exception lors de la conversion
     * Utile pour les environnements où les scriptlets ne sont pas autorisés
     */
    FAIL_ON_SCRIPTLET
}