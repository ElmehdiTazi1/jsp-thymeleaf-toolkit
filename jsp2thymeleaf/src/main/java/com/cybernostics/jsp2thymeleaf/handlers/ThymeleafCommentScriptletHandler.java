package com.cybernostics.jsp2thymeleaf.handlers;

import com.cybernostics.jsp.parser.JSPParser.ScriptletContext;
import org.jdom2.Content;
import org.jdom2.Text;

/**
 * Handler qui convertit les scriptlets en commentaires Thymeleaf.
 * Cette approche utilise la syntaxe de commentaire spécifique à Thymeleaf
 * qui est invisible dans le HTML final mais visible dans les templates.
 * 
 * @author elmehdi.tazi
 */
public class ThymeleafCommentScriptletHandler extends BaseScriptletHandler {

    @Override
    public Content handleScriptlet(ScriptletContext ctx, String scriptletCode, String filePath) {
        if (scriptletCode == null || scriptletCode.isEmpty()) {
            scriptletCode = extractScriptletCode(ctx);
        }
        
        String sanitizedCode = sanitizeScriptletCode(scriptletCode);
        // Format spécial des commentaires Thymeleaf
        String commentText = "/*[[ Original scriptlet: <% " + sanitizedCode + " %> ]]*/ ''";
        
        logger.fine("Converting scriptlet to Thymeleaf comment: " + commentText);
        return new Text(commentText);
    }
}