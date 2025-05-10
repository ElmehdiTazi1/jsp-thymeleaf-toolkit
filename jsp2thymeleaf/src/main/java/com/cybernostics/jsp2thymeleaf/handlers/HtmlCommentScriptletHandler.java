package com.cybernostics.jsp2thymeleaf.handlers;

import com.cybernostics.jsp.parser.JSPParser.ScriptletContext;
import org.jdom2.Comment;
import org.jdom2.Content;

/**
 * Handler qui convertit les scriptlets en commentaires HTML.
 * Cette approche conserve le code original sous forme de commentaires
 * visibles dans la source HTML générée.
 * 
 * @author elmehdi.tazi
 */
public class HtmlCommentScriptletHandler extends BaseScriptletHandler {

    @Override
    public Content handleScriptlet(ScriptletContext ctx, String scriptletCode, String filePath) {
        if (scriptletCode == null || scriptletCode.isEmpty()) {
            scriptletCode = extractScriptletCode(ctx);
        }
        
        String sanitizedCode = sanitizeScriptletCode(scriptletCode);
        String commentText = " Original scriptlet: <% " + sanitizedCode + " %> ";
        
        logger.fine("Converting scriptlet to HTML comment: " + commentText);
        return new Comment(commentText);
    }
}