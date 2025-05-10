package com.cybernostics.jsp2thymeleaf.handlers;

import com.cybernostics.jsp2thymeleaf.api.common.ScriptletHandlingStrategy;
import com.cybernostics.jsp2thymeleaf.api.handlers.ScriptletHandler;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

/**
 * Factory pour créer des handlers de scriptlets selon la stratégie configurée.
 * Implémente le pattern Factory Method pour créer les différents handlers.
 * 
 * @author elmehdi.tazi
 */
public class ScriptletHandlerFactory {
    
    private static final Map<ScriptletHandlingStrategy, ScriptletHandler> cachedHandlers = new EnumMap<>(ScriptletHandlingStrategy.class);
    
    /**
     * Obtient un handler de scriptlet pour la stratégie spécifiée.
     * 
     * @param strategy Stratégie de traitement des scriptlets
     * @return Handler approprié pour la stratégie
     */
    public static ScriptletHandler getHandler(ScriptletHandlingStrategy strategy) {
        return getHandler(strategy, null);
    }
    
    /**
     * Obtient un handler de scriptlet pour la stratégie spécifiée avec un dossier d'extraction personnalisé.
     * 
     * @param strategy Stratégie de traitement des scriptlets
     * @param extractionFolder Dossier d'extraction pour la stratégie EXTRACT_TO_FILE (peut être null pour les autres stratégies)
     * @return Handler approprié pour la stratégie
     */
    public static ScriptletHandler getHandler(ScriptletHandlingStrategy strategy, Path extractionFolder) {
        // Pour certaines stratégies, nous pouvons réutiliser une instance existante
        if (strategy != ScriptletHandlingStrategy.EXTRACT_TO_FILE && cachedHandlers.containsKey(strategy)) {
            return cachedHandlers.get(strategy);
        }
        
        ScriptletHandler handler;
        switch (strategy) {
            case HTML_COMMENT:
                handler = new HtmlCommentScriptletHandler();
                break;
            case THYMELEAF_COMMENT:
                handler = new ThymeleafCommentScriptletHandler();
                break;
            case EXTRACT_TO_FILE:
                handler = extractionFolder != null 
                        ? new ExtractToFileScriptletHandler(extractionFolder) 
                        : new ExtractToFileScriptletHandler();
                break;
            case FAIL_ON_SCRIPTLET:
                handler = new FailOnScriptletHandler();
                break;
            default:
                // Par défaut, utilise le handler de commentaires HTML comme stratégie la plus sûre
                handler = new HtmlCommentScriptletHandler();
        }
        
        // Cache le handler sauf pour EXTRACT_TO_FILE qui pourrait avoir des dossiers d'extraction différents
        if (strategy != ScriptletHandlingStrategy.EXTRACT_TO_FILE) {
            cachedHandlers.put(strategy, handler);
        }
        
        return handler;
    }
}