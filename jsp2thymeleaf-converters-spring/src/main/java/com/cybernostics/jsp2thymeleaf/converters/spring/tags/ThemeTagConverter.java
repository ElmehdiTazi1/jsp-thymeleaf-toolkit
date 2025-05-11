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
 * Convertisseur pour les balises &lt;spring:theme&gt;.
 * <p>
 * Ce convertisseur transforme les balises &lt;spring:theme&gt; de Spring MVC en
 * expressions Thymeleaf utilisant le dialecte standard pour les thèmes.
 * </p>
 * <p>
 * Les attributs pris en charge incluent:
 * <ul>
 *   <li>code - Le code du thème à utiliser</li>
 *   <li>text - Le texte par défaut si le thème n'est pas trouvé</li>
 *   <li>var - Le nom de la variable dans laquelle stocker le résultat</li>
 *   <li>scope - Le scope de la variable (ignoré dans Thymeleaf)</li>
 * </ul>
 * </p>
 * <p>
 * Exemple de conversion:
 * <pre>
 * JSP:
 * &lt;spring:theme code="style.header" text="Header Style" /&gt;
 * 
 * Thymeleaf:
 * &lt;span data-th-text="#{style.header}"&gt;Header Style&lt;/span&gt;
 * </pre>
 * </p>
 * 
 * @author jason
 * @version 1.0
 */
public class ThemeTagConverter implements TagConverter {
    
    private ScopedJSPConverters scopedConverters;
    
    /**
     * Retourne le nom du tag que ce convertisseur peut gérer.
     * 
     * @return "spring:theme"
     */
    @Override
    public String getApplicableTag() {
        return "spring:theme";
    }
    
    /**
     * Détermine si ce convertisseur peut gérer le nœud JSP spécifié.
     * 
     * @param node Le nœud JSP à vérifier
     * @return true si ce nœud est une balise &lt;spring:theme&gt;
     */
    @Override
    public boolean canHandle(JSPParser.JspElementContext node) {
        return node != null && node.name != null && 
               node.name.getText().equals(getApplicableTag());
    }
    
    /**
     * Convertit une balise &lt;spring:theme&gt; en expression Thymeleaf.
     * 
     * @param node Le nœud JSP à convertir
     * @param context Le contexte de conversion
     * @return Une liste contenant l'élément Thymeleaf généré
     */
    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
        // Extraire les attributs
        String code = extractAttributeValue(node, "code");
        String defaultText = extractAttributeValue(node, "text");
        String var = extractAttributeValue(node, "var");
        
        // Construire l'expression de thème Thymeleaf
        String themeExpression = "#{" + code + "}";
        
        // Si var est présent, générer une variable Thymeleaf
        if (var != null && !var.isEmpty()) {
            Element varElement = new Element("meta");
            varElement.setAttribute("th:with", var + "='" + themeExpression + "'");
            varElement.setAttribute("hidden", "true");
            return Collections.singletonList(varElement);
        } 
        // Sinon, générer un élément pour afficher le thème
        else {
            Element themeElement = new Element("span");
            themeElement.setAttribute("th:text", themeExpression);
            
            // Ajouter le texte par défaut si disponible
            if (defaultText != null && !defaultText.isEmpty()) {
                themeElement.setText(defaultText);
            } else {
                themeElement.setText(code);
            }
            
            return Collections.singletonList(themeElement);
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
