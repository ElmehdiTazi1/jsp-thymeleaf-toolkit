package com.cybernostics.jsp2thymeleaf.api.handlers;

import com.cybernostics.jsp.parser.JSPParser.ScriptletContext;
import org.jdom2.Content;

/**
 * Interface définissant le contrat pour les handlers de scriptlets.
 * Conforme au principe de ségrégation d'interface (ISP) en fournissant
 * une interface spécifique pour le traitement des scriptlets.
 * 
 * @author elmehdi.tazi
 */
public interface ScriptletHandler {
    
    /**
     * Traite un scriptlet JSP et génère le contenu Thymeleaf correspondant
     * 
     * @param ctx Contexte du scriptlet JSP
     * @param scriptletCode Code du scriptlet
     * @param filePath Chemin du fichier JSP source
     * @return Contenu Thymeleaf généré
     */
    Content handleScriptlet(ScriptletContext ctx, String scriptletCode, String filePath);
}