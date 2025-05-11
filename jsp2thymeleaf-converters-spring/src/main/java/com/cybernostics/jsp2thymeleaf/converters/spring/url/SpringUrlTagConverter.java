/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.converters.spring.url;

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
 * Convertisseur pour les balises &lt;spring:url&gt;.
 * <p>
 * Ce convertisseur transforme les balises &lt;spring:url&gt; de Spring MVC en
 * expressions URL Thymeleaf (@{...}). Il gère également les paramètres
 * d'URL définis via les balises &lt;spring:param&gt; imbriquées.
 * </p>
 * <p>
 * Exemple de conversion:
 * <pre>
 * JSP:
 * &lt;spring:url value="/users/{id}" var="userUrl"&gt;
 *     &lt;spring:param name="id" value="${user.id}" /&gt;
 *     &lt;spring:param name="action" value="view" /&gt;
 * &lt;/spring:url&gt;
 * &lt;a href="${userUrl}"&gt;Voir l'utilisateur&lt;/a&gt;
 * 
 * Thymeleaf:
 * &lt;a th:href="@{/users/{id}(id=${user.id},action='view')}"&gt;Voir l'utilisateur&lt;/a&gt;
 * </pre>
 * </p>
 * 
 * @author jason
 * @version 1.0
 */
public class SpringUrlTagConverter implements TagConverter {
    
    private ScopedJSPConverters scopedConverters;
    
    /**
     * Retourne le nom du tag que ce convertisseur peut gérer.
     * 
     * @return "spring:url"
     */
    @Override
    public String getApplicableTag() {
        return "spring:url";
    }
    
    /**
     * Détermine si ce convertisseur peut gérer le nœud JSP spécifié.
     * 
     * @param node Le nœud JSP à vérifier
     * @return true si ce nœud est une balise &lt;spring:url&gt;
     */
    @Override
    public boolean canHandle(JSPParser.JspElementContext node) {
        return node != null && node.name != null && 
               node.name.getText().equals(getApplicableTag());
    }
    
    /**
     * Convertit une balise &lt;spring:url&gt; en expression URL Thymeleaf.
     * <p>
     * Ce processus implique:
     * <ol>
     *   <li>Extraire la valeur de l'URL de l'attribut "value"</li>
     *   <li>Extraire le nom de variable de l'attribut "var" s'il est présent</li>
     *   <li>Collecter les paramètres d'URL des balises &lt;spring:param&gt; imbriquées</li>
     *   <li>Générer une expression URL Thymeleaf avec les paramètres</li>
     * </ol>
     * </p>
     * 
     * @param node Le nœud JSP à convertir
     * @param context Le contexte de conversion
     * @return Une liste contenant l'élément Thymeleaf généré
     */
    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
        // Extrait les attributs value et var
        String urlValue = extractAttributeValue(node, "value");
        String varName = extractAttributeValue(node, "var");
        
        // Transforme l'URL JSP en expression URL Thymeleaf
        String thymeleafUrl = transformToThymeleafUrl(urlValue, collectParams(node));
        
        // Si var est défini, on génère une balise de définition de variable
        if (varName != null && !varName.isEmpty()) {
            Element varDefElement = new Element("meta");
            varDefElement.setAttribute("th:with", varName + "='" + thymeleafUrl + "'");
            varDefElement.setAttribute("hidden", "true");
            return Collections.singletonList(varDefElement);
        } else {
            // Sinon on renvoie un commentaire avec l'URL convertie
            return Collections.singletonList(new org.jdom2.Comment("URL Thymeleaf: " + thymeleafUrl));
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
    
    /**
     * Collecte tous les paramètres d'URL des balises &lt;spring:param&gt; imbriquées.
     * 
     * @param node Le nœud JSP parent
     * @return Une liste de paires nom/valeur de paramètres
     */
    private List<ParamPair> collectParams(JSPParser.JspElementContext node) {
        List<ParamPair> params = new ArrayList<>();
        
        // Analyser les balises spring:param imbriquées
        for (JSPParser.JspElementContext childNode : node.element) {
            if (childNode.name != null && childNode.name.getText().equals("spring:param")) {
                String paramName = extractAttributeValue(childNode, "name");
                String paramValue = extractAttributeValue(childNode, "value");
                
                if (paramName != null && paramValue != null) {
                    params.add(new ParamPair(paramName, paramValue));
                }
            }
        }
        
        return params;
    }
    
    /**
     * Transforme une URL JSP et ses paramètres en expression URL Thymeleaf.
     * 
     * @param url L'URL de base
     * @param params Les paramètres d'URL
     * @return L'expression URL Thymeleaf
     */
    private String transformToThymeleafUrl(String url, List<ParamPair> params) {
        StringBuilder sb = new StringBuilder("@{");
        sb.append(url);
        
        if (!params.isEmpty()) {
            sb.append("(");
            
            for (int i = 0; i < params.size(); i++) {
                ParamPair param = params.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(param.name).append("=").append(param.value);
            }
            
            sb.append(")");
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public ScopedJSPConverters getScopedConverters() {
        return scopedConverters;
    }
    
    @Override
    public void setScopedConverters(ScopedJSPConverters scopedConverters) {
        this.scopedConverters = scopedConverters;
    }
    
    /**
     * Classe interne représentant une paire nom/valeur de paramètre d'URL.
     */
    private static class ParamPair {
        final String name;
        final String value;
        
        ParamPair(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
