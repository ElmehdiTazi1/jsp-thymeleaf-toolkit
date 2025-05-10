package com.cybernostics.jsp2thymeleaf.handlers;

import com.cybernostics.jsp.parser.JSPParser.ScriptletContext;
import com.cybernostics.jsp2thymeleaf.api.handlers.ScriptletHandler;
import java.util.logging.Logger;

/**
 * Classe abstraite implémentant des fonctionnalités communes pour tous les handlers de scriptlets.
 * Conforme au principe DRY (Don't Repeat Yourself) en factorisant le code commun.
 * 
 * @author elmehdi.tazi
 */
public abstract class BaseScriptletHandler implements ScriptletHandler {
    
    protected static final Logger logger = Logger.getLogger(BaseScriptletHandler.class.getName());
    
    /**
     * Extrait le code du scriptlet du contexte fourni
     * 
     * @param ctx Contexte du scriptlet JSP
     * @return Code du scriptlet
     */
    protected String extractScriptletCode(ScriptletContext ctx) {
        // Supprime les balises <% et %> et renvoie uniquement le code Java
        String fullText = ctx.getText();
        if (fullText.startsWith("<%")) {
            fullText = fullText.substring(2);
        }
        if (fullText.endsWith("%>")) {
            fullText = fullText.substring(0, fullText.length() - 2);
        }
        return fullText.trim();
    }
    
    /**
     * Nettoie le code du scriptlet en supprimant les caractères spéciaux
     * 
     * @param code Code du scriptlet
     * @return Code nettoyé
     */
    protected String sanitizeScriptletCode(String code) {
        // Échapper les caractères qui pourraient poser problème dans les commentaires
        return code.replace("--", "- -")
                  .replace("*/", "* /");
    }
}