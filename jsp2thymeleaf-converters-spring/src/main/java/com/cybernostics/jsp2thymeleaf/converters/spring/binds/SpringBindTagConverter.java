/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.converters.spring.binds;

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
 * Convertisseur pour les balises &lt;spring:bind&gt;.
 * <p>
 * Ce convertisseur transforme les balises &lt;spring:bind&gt; de Spring MVC en
 * constructions Thymeleaf équivalentes. Les balises spring:bind sont utilisées pour 
 * créer des liaisons de données entre le modèle et la vue, notamment pour gérer 
 * les erreurs de validation.
 * </p>
 * <p>
 * Les attributs pris en charge incluent:
 * <ul>
 *   <li>path - Le chemin vers la propriété du modèle à lier</li>
 *   <li>ignoreNestedPath - Indique s'il faut ignorer les chemins imbriqués</li>
 * </ul>
 * </p>
 * <p>
 * En JSP avec spring:bind, plusieurs variables sont exposées (status.value, status.error, etc.). 
 * En Thymeleaf, ces informations sont accessibles via #fields.
 * </p>
 * <p>
 * Exemple de conversion:
 * <pre>
 * JSP:
 * &lt;spring:bind path="user.name"&gt;
 *   &lt;input type="text" name="${status.expression}" value="${status.value}" /&gt;
 *   &lt;c:if test="${status.error}"&gt;
 *     &lt;span class="error"&gt;${status.errorMessage}&lt;/span&gt;
 *   &lt;/c:if&gt;
 * &lt;/spring:bind&gt;
 * 
 * Thymeleaf:
 * &lt;div class="form-group" th:classappend="${#fields.hasErrors('user.name')} ? 'has-error'"&gt;
 *   &lt;input type="text" th:field="*{user.name}" /&gt;
 *   &lt;span class="error" th:if="${#fields.hasErrors('user.name')}" 
 *         th:errors="*{user.name}"&gt;Error message&lt;/span&gt;
 * &lt;/div&gt;
 * </pre>
 * </p>
 * 
 * @author jason
 * @version 1.0
 */
public class SpringBindTagConverter implements TagConverter {
    
    private ScopedJSPConverters scopedConverters;
    
    /**
     * Retourne le nom du tag que ce convertisseur peut gérer.
     * 
     * @return "spring:bind"
     */
    @Override
    public String getApplicableTag() {
        return "spring:bind";
    }
    
    /**
     * Détermine si ce convertisseur peut gérer le nœud JSP spécifié.
     * 
     * @param node Le nœud JSP à vérifier
     * @return true si ce nœud est une balise &lt;spring:bind&gt;
     */
    @Override
    public boolean canHandle(JSPParser.JspElementContext node) {
        return node != null && node.name != null && 
               node.name.getText().equals(getApplicableTag());
    }
    
    /**
     * Convertit une balise &lt;spring:bind&gt; en construction Thymeleaf.
     * <p>
     * Cette conversion est complexe car elle doit transformer la logique de bind
     * de Spring MVC en logique de validation de formulaire Thymeleaf.
     * </p>
     * 
     * @param node Le nœud JSP à convertir
     * @param context Le contexte de conversion
     * @return Une liste contenant les éléments Thymeleaf générés
     */
    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
        // Extraire le chemin de liaison
        String path = extractAttributeValue(node, "path");
        
        if (path == null || path.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Créer un conteneur div avec des classes conditionnelles pour les erreurs
        Element containerDiv = new Element("div");
        containerDiv.setAttribute("class", "form-group");
        containerDiv.setAttribute("th:classappend", "${#fields.hasErrors('" + path + "')} ? 'has-error'");
        
        // Ajouter un commentaire pour aider les développeurs à comprendre la conversion
        String comment = "<!-- Converti depuis spring:bind path=\"" + path + "\" -->";
        containerDiv.addContent(new org.jdom2.Comment(comment.substring(4, comment.length() - 3)));
        
        // Traiter les éléments enfants s'il y en a
        if (node.element != null && !node.element.isEmpty()) {
            // Convertir les éléments enfants avec le contexte approprié
            for (JSPParser.JspElementContext childNode : node.element) {
                List<Content> processedChildren = context.process(childNode, context);
                containerDiv.addContent(processedChildren);
            }
        }
        
        // Ajouter un commentaire explicatif sur la façon d'utiliser th:field et th:errors
        Element helperComment = new Element("div");
        helperComment.setAttribute("class", "helper-comment");
        helperComment.setAttribute("style", "display: none;");
        
        String helperText = 
            "Pour utiliser correctement les validations Thymeleaf :\n" +
            "1. Utilisez th:field=\"*{" + path + "}\" sur les champs de formulaire\n" +
            "2. Pour afficher les erreurs: <span th:if=\"${#fields.hasErrors('" + path + "')}\" " +
            "th:errors=\"*{" + path + "}\">Message d'erreur</span>\n" +
            "3. Assurez-vous que votre formulaire a un th:object défini";
            
        helperComment.addContent(new org.jdom2.Comment(helperText));
        containerDiv.addContent(helperComment);
        
        return Collections.singletonList(containerDiv);
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
