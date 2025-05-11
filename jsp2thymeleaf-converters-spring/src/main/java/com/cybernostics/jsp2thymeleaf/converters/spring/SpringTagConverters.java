/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.converters.spring;

import com.cybernostics.jsp2thymeleaf.api.common.taglib.TaglibConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource;
import com.cybernostics.jsp2thymeleaf.converters.spring.tags.MessageTagConverter;
import com.cybernostics.jsp2thymeleaf.converters.spring.tags.ThemeTagConverter;
import com.cybernostics.jsp2thymeleaf.converters.spring.tags.EvalTagConverter;
import com.cybernostics.jsp2thymeleaf.converters.spring.url.SpringUrlTagConverter;
import com.cybernostics.jsp2thymeleaf.converters.spring.binds.SpringBindTagConverter;

/**
 * Convertisseur principal pour les balises Spring MVC en Thymeleaf.
 * <p>
 * Cette classe fournit le point d'entrée pour tous les convertisseurs de balises Spring MVC.
 * Elle implémente l'interface TaglibConverter et est découverte automatiquement par le
 * mécanisme de SPI (Service Provider Interface) de Java.
 * </p>
 * <p>
 * Le convertisseur prend en charge les catégories de tags Spring suivantes :
 * <ul>
 *   <li>URL - Convertit les tags &lt;spring:url&gt; en expressions URL Thymeleaf</li>
 *   <li>Messages - Convertit les tags &lt;spring:message&gt; en expressions message Thymeleaf</li>
 *   <li>Thèmes - Convertit les tags &lt;spring:theme&gt; en expressions Thymeleaf</li>
 *   <li>Évaluation - Convertit les tags &lt;spring:eval&gt; en expressions Thymeleaf</li>
 *   <li>Binding - Convertit les tags &lt;spring:bind&gt; en constructions Thymeleaf</li>
 * </ul>
 * </p>
 * 
 * @author jason
 * @version 1.0
 * @see TaglibConverter
 * @see SpringUrlTagConverter
 * @see MessageTagConverter
 * @see ThemeTagConverter
 * @see EvalTagConverter
 * @see SpringBindTagConverter
 */
public class SpringTagConverters implements TaglibConverter {
    
    /**
     * URI de la taglib Spring MVC.
     */
    private static final String SPRING_URI = "http://www.springframework.org/tags";
    
    /**
     * Retourne l'URI de la taglib Spring MVC.
     * 
     * @return L'URI de la taglib Spring MVC
     */
    @Override
    public String forUri() {
        return SPRING_URI;
    }
    
    /**
     * Fournit les convertisseurs pour les tags Spring MVC.
     * 
     * @return Une source de convertisseurs pour les tags Spring MVC
     */
    @Override
    public TagConverterSource getTagConverter() {
        return (tagName) -> {
            switch (tagName) {
                case "spring:url":
                    return java.util.Optional.of(new SpringUrlTagConverter());
                case "spring:message":
                    return java.util.Optional.of(new MessageTagConverter());
                case "spring:theme":
                    return java.util.Optional.of(new ThemeTagConverter());
                case "spring:eval":
                    return java.util.Optional.of(new EvalTagConverter());
                case "spring:bind":
                    return java.util.Optional.of(new SpringBindTagConverter());
                default:
                    return java.util.Optional.empty();
            }
        };
    }
    
    /**
     * Fournit les convertisseurs pour les fonctions EL Spring MVC.
     * 
     * @return Une source de convertisseurs pour les fonctions Spring MVC
     */
    @Override
    public FunctionConverterSource getFunctionConverter() {
        // Spring n'a pas de fonctions EL standards, donc on retourne une source vide
        return (functionName) -> java.util.Optional.empty();
    }
}
