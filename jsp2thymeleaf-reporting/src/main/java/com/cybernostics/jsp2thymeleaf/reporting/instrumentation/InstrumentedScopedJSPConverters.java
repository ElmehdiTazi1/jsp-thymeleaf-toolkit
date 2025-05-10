package com.cybernostics.jsp2thymeleaf.reporting.instrumentation;

import com.cybernostics.jsp2thymeleaf.api.elements.JSPNodeConverterSource;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.reporting.api.ReportCollector;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Wrapper instrumenté pour ScopedJSPConverters qui collecte des statistiques sur les convertisseurs utilisés.
 * Respecte le principe de Dependency Inversion en dépendant d'abstractions plutôt que de détails.
 */
public class InstrumentedScopedJSPConverters extends ScopedJSPConverters {

    private final ReportCollector reportCollector;
    private final String filePath;
    
    public InstrumentedScopedJSPConverters(ScopedJSPConverters delegate, 
                                          ReportCollector reportCollector,
                                          Path filePath) {
        super(delegate);
        this.reportCollector = reportCollector;
        this.filePath = filePath.toString();
    }
    
    @Override
    public Optional<JSPNodeConverterSource> forPrefix(String prefixOrEmpty) {
        // Obtenir le convertisseur d'origine
        Optional<JSPNodeConverterSource> converterSource = super.forPrefix(prefixOrEmpty);
        
        // Enregistrer l'utilisation du convertisseur pour les statistiques
        if (converterSource.isPresent()) {
            String prefixName = prefixOrEmpty.isEmpty() ? "default" : prefixOrEmpty;
            reportCollector.getCurrentReport().incrementTagConversion(prefixName);
        }
        
        return converterSource;
    }
    
    /**
     * Create a new scoped converter with the given parent scope
     * 
     * @param parentScope The parent scope to use
     * @return A new instrumented converter with the parent scope
     */
    public ScopedJSPConverters withParentScope(ScopedJSPConverters parentScope) {
        // Create a new instance with the parent scope
        ScopedJSPConverters combined = new ScopedJSPConverters(parentScope);
        
        // Wrap it in an instrumented version to maintain our instrumentation
        return new InstrumentedScopedJSPConverters(combined, reportCollector, Path.of(filePath));
    }
}