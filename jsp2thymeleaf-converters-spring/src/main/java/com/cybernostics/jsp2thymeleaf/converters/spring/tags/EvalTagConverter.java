/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.converters.spring.tags;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPElementNodeConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverter;
import java.util.Collections;
import java.util.List;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 * Convertisseur pour les balises &lt;spring:eval&gt;.
 * <p>
 * Ce convertisseur transforme les balises &lt;spring:eval&gt; de Spring MVC en
 * expressions Thymeleaf équivalentes. Le tag &lt;spring:eval&gt; est utilisé pour
 * évaluer des expressions Spring Expression Language (SpEL) et les afficher ou
 * les stocker dans une variable.
 * </p>
 * <p>
 * Les attributs pris en charge incluent:
 * <ul>
 *   <li>expression - L'expression SpEL à évaluer</li>
 *   <li>var - Le nom de la variable dans laquelle stocker le résultat</li>
 *   <li>scope - Le scope de la variable (ignoré dans Thymeleaf)</li>
 *   <li>htmlEscape - Indique si le résultat doit être échappé pour HTML (géré par Thymeleaf)</li>
 * </ul>
 * </p>
 * <p>
 * Exemple de conversion:
 * <pre>
 * JSP:
 * &lt;spring:eval expression="user.getFullName().toUpperCase()" /&gt;
 * 
 * Thymeleaf:
 * &lt;span th:text="${user.getFullName().toUpperCase()}"&gt;Nom d'utilisateur&lt;/span&gt;
 * </pre>
 * </p>
 * 
 * @author jason
 * @version 1.0
 */
public class EvalTagConverter implements TagConverter {
    
    private ScopedJSPConverters scopedConverters;
    
    /**
     * Retourne le nom du tag que ce convertisseur peut gérer.
     * 
     * @return "spring:eval"
     */
    @Override
    public String getApplicableTag() {
        return "spring:eval";
    }
    
    /**
     * Détermine si ce convertisseur peut gérer le nœud JSP spécifié.
     * 
     * @param node Le nœud JSP à vérifier
     * @return true si ce nœud est une balise &lt;spring:eval&gt;
     */
    @Override
    public boolean canHandle(JSPParser.JspElementContext node) {
        return node != null && node.name != null && 
               node.name.getText().equals(getApplicableTag());
    }
    
    /**
     * Convertit une balise &lt;spring:eval&gt; en expression Thymeleaf.
     * 
     * @param node Le nœud JSP à convertir
     * @param context Le contexte de conversion
     * @return Une liste contenant l'élément Thymeleaf généré
     */
    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
        // Extraire les attributs
        String expression = extractAttributeValue(node, "expression");
        String var = extractAttributeValue(node, "var");
        String javaScriptEscape = extractAttributeValue(node, "javaScriptEscape");
        
        // Préparer l'expression Thymeleaf
        String thymeleafExpression = "${" + expression + "}";
        
        // Si var est présent, générer une variable Thymeleaf
        if (var != null && !var.isEmpty()) {
            Element varElement = new Element("meta");
            varElement.setAttribute("th:with", var + "=" + thymeleafExpression);
            varElement.setAttribute("hidden", "true");
            return Collections.singletonList(varElement);
        } 
        // Sinon, générer un élément pour afficher l'expression
        else {
            Element evalElement = new Element("span");
            
            // Gérer l'échappement JavaScript si nécessaire
            if (javaScriptEscape != null && Boolean.parseBoolean(javaScriptEscape)) {
                // Utiliser un utilitaire Thymeleaf pour l'échappement JavaScript
                evalElement.setAttribute("th:text", "${#strings.escapeJavaScript(" + expression + ")}");
            } else {
                evalElement.setAttribute("th:text", thymeleafExpression);
            }
            
            // Ajouter un texte par défaut pour un meilleur design
            evalElement.setText("Valeur de l'expression");
            
            return Collections.singletonList(evalElement);
        }
    }
    
    /**
     * Extrait la valeur d'un attribut du nœud JSP.
     * 
     * @param node Le nœud JSP
     * @param attributeName Le nom de l'attribut à extraire
     * @return La valeur de l'attribut ou null si l'attribut n'existe pas
     */
    private String extractAttributeValue(JSPParser.JspElementContext node, String attributeName) {
        return node.atts.stream()
                .filter(attr -> attr.name != null && attr.name.getText().equals(attributeName))
                .map(attr -> attr.value.getText())
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public ScopedJSPConverters getScopedConverters() {
        return scopedConverters;
    }
    
    @Override
    public void setScopedConverters(ScopedJSPConverters scopedConverters) {
        this.scopedConverters = scopedConverters;
    }
}
