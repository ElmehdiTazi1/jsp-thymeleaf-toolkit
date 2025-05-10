package com.cybernostics.jsp2thymeleaf.handlers;

import com.cybernostics.jsp.parser.JSPParser.ScriptletContext;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.api.exception.ScriptletNotAllowedException;
import org.jdom2.Content;

/**
 * Handler qui génère une exception lorsqu'un scriptlet est rencontré.
 * Utile dans les environnements où les scriptlets ne sont pas autorisés et doivent être
 * refactorisés complètement avant la migration.
 * 
 * @author elmehdi.tazi
 */
public class FailOnScriptletHandler extends BaseScriptletHandler {

    @Override
    public Content handleScriptlet(ScriptletContext ctx, String scriptletCode, String filePath) {
        if (scriptletCode == null || scriptletCode.isEmpty()) {
            scriptletCode = extractScriptletCode(ctx);
        }
        
        // Lance une exception spécifique pour les scriptlets
        throw new ScriptletNotAllowedException(
                "Les scriptlets ne sont pas autorisés dans cette configuration de conversion. " +
                "Veuillez refactoriser le code Java suivant: " + scriptletCode,
                filePath,
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine()
        );
    }
}