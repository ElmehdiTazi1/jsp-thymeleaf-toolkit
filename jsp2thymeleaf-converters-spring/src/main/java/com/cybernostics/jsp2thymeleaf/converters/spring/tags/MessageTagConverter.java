/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.converters.spring.tags;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPElementNodeConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 * Convertisseur pour les balises &lt;spring:message&gt;.
 * <p>
 * Ce convertisseur transforme les balises &lt;spring:message&gt; de Spring MVC en
 * expressions message Thymeleaf (#{...}). Il prend en charge les attributs suivants:
 * <ul>
 *   <li>code - Le code du message à afficher</li>
 *   <li>arguments - Les arguments à passer au message</li>
 *   <li>text - Le texte par défaut si le message n'est pas trouvé</li>
 *   <li>var - La variable à laquelle assigner le résultat</li>
 *   <li>scope - Le scope de la variable (ignoré dans Thymeleaf)</li>
 * </ul>
 * </p>
 * <p>
 * Exemple de conversion:
 * <pre>
 * JSP:
 * &lt;spring:message code="welcome.message" arguments="${user.name}" text="Welcome" /&gt;
 * 
 * Thymeleaf:
 * &lt;span th:text="#{welcome.message(${user.name})}" &gt;Welcome&lt;/span&gt;
 * </pre>
 * </p>
 * 
 * @author jason
 * @version 1.0
 */
public class MessageTagConverter implements TagConverter {
    
    private ScopedJSPConverters scopedConverters;
    
    /**
     * Retourne le nom du tag que ce convertisseur peut gérer.
     * 
     * @return "spring:message"
     */
    @Override
    public String getApplicableTag() {
        return "spring:message";
    }
    
    /**
     * Détermine si ce convertisseur peut gérer le nœud JSP spécifié.
     * 
     * @param node Le nœud JSP à vérifier
     * @return true si ce nœud est une balise &lt;spring:message&gt;
     */
    @Override
    public boolean canHandle(JSPParser.JspElementContext node) {
        return node != null && node.name != null && 
               node.name.getText().equals(getApplicableTag());
    }
    
    /**
     * Convertit une balise &lt;spring:message&gt; en expression message Thymeleaf.
     * 
     * @param node Le nœud JSP à convertir
     * @param context Le contexte de conversion
     * @return Une liste contenant l'élément Thymeleaf généré
     */
    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
        // Extraire les attributs
        String code = extractAttributeValue(node, "code");
        String arguments = extractAttributeValue(node, "arguments");
        String defaultText = extractAttributeValue(node, "text");
        String var = extractAttributeValue(node, "var");
        
        // Construire l'expression de message Thymeleaf
        String messageExpression = buildMessageExpression(code, arguments);
        
        // Si var est présent, générer une variable Thymeleaf
        if (var != null && !var.isEmpty()) {
            Element varElement = new Element("meta");
            varElement.setAttribute("th:with", var + "='" + messageExpression + "'");
            varElement.setAttribute("hidden", "true");
            return Collections.singletonList(varElement);
        } 
        // Sinon, générer un élément pour afficher le message
        else {
            Element messageElement = new Element("span");
            messageElement.setAttribute("th:text", messageExpression);
            
            // Ajouter le texte par défaut si disponible
            if (defaultText != null && !defaultText.isEmpty()) {
                messageElement.setText(defaultText);
            } else {
                messageElement.setText(code);
            }
            
            return Collections.singletonList(messageElement);
        }
    }
    
    /**
     * Construit l'expression de message Thymeleaf à partir du code et des arguments.
     * 
     * @param code Le code du message
     * @param arguments Les arguments à passer au message
     * @return L'expression de message Thymeleaf complète
     */
    private String buildMessageExpression(String code, String arguments) {
        StringBuilder expression = new StringBuilder("#{").append(code);
        
        // Ajouter les arguments s'ils sont présents
        if (arguments != null && !arguments.isEmpty()) {
            expression.append("(").append(arguments).append(")");
        }
        
        expression.append("}");
        return expression.toString();
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
