package com.cybernostics.jsp2thymeleaf.reporting.instrumentation;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.parser.JSP2ThymeleafTransformerListener;
import com.cybernostics.jsp2thymeleaf.reporting.api.ReportCollector;
import com.cybernostics.jsp2thymeleaf.reporting.model.ScriptletInfo;
import com.cybernostics.jsp2thymeleaf.reporting.model.TagConversionInfo;

import java.nio.file.Path;
import java.util.List;

/**
 * Écouteur de transformation instrumenté qui collecte des données sur les balises et les scriptlets.
 * Respecte le principe d'Interface Segregation en implémentant uniquement les méthodes nécessaires.
 */
public class InstrumentedTransformerListener extends JSP2ThymeleafTransformerListener {

    private final ReportCollector reportCollector;
    private final Path filePath;
    
    public InstrumentedTransformerListener(ScopedJSPConverters scopedJspConverters, 
                                          ReportCollector reportCollector,
                                          Path filePath) {
        super(scopedJspConverters);
        this.reportCollector = reportCollector;
        this.filePath = filePath;
    }

    @Override
    public void enterJspElement(JSPParser.JspElementContext ctx) {
        // Capturer les informations sur la balise avant conversion
        String tagName = ctx.name.getText();
        String prefix = "";
        if (tagName.contains(":")) {
            String[] parts = tagName.split(":");
            prefix = parts[0];
            tagName = parts[1];
        }
        
        // Créer une information de conversion de balise en utilisant le builder
        TagConversionInfo.Builder tagInfoBuilder = TagConversionInfo.builder()
                .withFilePath(filePath.toString())
                .withLineNumber(ctx.getStart().getLine())
                .withOriginalTagName(tagName)
                .withOriginalNamespace(prefix);
        
        // Appeler la méthode d'origine pour traiter la balise
        super.enterJspElement(ctx);
        
        // Déterminer le statut de conversion en fonction des problèmes potentiels
        boolean hasProblems = getProblems().stream()
                .anyMatch(p -> getLineNumber(p) == ctx.getStart().getLine());
        
        if (hasProblems) {
            tagInfoBuilder.withStatus(TagConversionInfo.ConversionStatus.UNSUPPORTED);
        } else {
            tagInfoBuilder.withStatus(TagConversionInfo.ConversionStatus.SUCCESS);
        }
        
        // Construire l'objet et enregistrer la conversion de balise
        TagConversionInfo tagInfo = tagInfoBuilder.build();
        reportCollector.recordTagConversion(tagInfo);
    }
    
    @Override
    public void enterScriptlet(JSPParser.ScriptletContext ctx) {
        // Capturer les informations sur le scriptlet avant conversion
        String scriptletContent = ctx.getText();
        
        // Créer une information de scriptlet en utilisant le builder
        ScriptletInfo scriptletInfo = ScriptletInfo.builder()
                .withFilePath(filePath.toString())
                .withLineNumber(ctx.getStart().getLine())
                .withOriginalContent(scriptletContent)
                .withAction(ScriptletInfo.ScriptletAction.COMMENTED)
                .build();
        
        // Appeler la méthode d'origine pour traiter le scriptlet
        super.enterScriptlet(ctx);
        
        // Enregistrer le traitement du scriptlet
        reportCollector.recordScriptletProcessing(scriptletInfo);
    }
    
    /**
     * Helper method to safely get line number from JSP2ThymeLeafException
     * 
     * @param exception The exception to get line number from
     * @return The line number or 0 if not available
     */
    private int getLineNumber(JSP2ThymeLeafException exception) {
        try {
            // Try to access line number through the location API
            return exception.getLocation().getLine();
        } catch (Exception e) {
            return 0;
        }
    }
}