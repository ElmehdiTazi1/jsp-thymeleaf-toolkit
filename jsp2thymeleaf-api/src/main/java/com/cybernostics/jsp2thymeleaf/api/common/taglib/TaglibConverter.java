/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.api.common.taglib;

import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource;

/**
 * Interface pour la conversion d'une bibliothèque de tags JSP (taglib) vers Thymeleaf.
 * 
 * Cette interface définit le contrat pour convertir des bibliothèques de tags JSP
 * complètes vers leurs équivalents Thymeleaf. Chaque implémentation de cette
 * interface gère la conversion d'une taglib spécifique (comme JSTL, Spring MVC, etc.).
 * 
 * Un convertisseur de taglib fournit:
 * 1. L'URI de la taglib qu'il peut gérer
 * 2. Un ensemble de convertisseurs pour les balises de cette taglib
 * 3. Un ensemble de convertisseurs pour les fonctions de cette taglib
 * 
 * Cette approche modulaire permet d'ajouter facilement le support de nouvelles
 * bibliothèques de tags en implémentant cette interface.
 * 
 * @author jason
 * @version 1.0
 * @see TagConverterSource
 * @see FunctionConverterSource
 */
public interface TaglibConverter
{
    /**
     * Retourne l'URI de la taglib que ce convertisseur peut gérer.
     * 
     * L'URI est l'identifiant unique de la bibliothèque de tags JSP,
     * par exemple "http://java.sun.com/jsp/jstl/core" pour JSTL Core.
     * 
     * @return L'URI de la taglib prise en charge par ce convertisseur
     */
    String forUri();

    /**
     * Retourne la source des convertisseurs de tags pour cette taglib.
     * 
     * Cette méthode fournit une source de convertisseurs capable de transformer
     * les balises spécifiques à cette bibliothèque de tags en leurs équivalents Thymeleaf.
     * 
     * @return Une source de convertisseurs pour les tags de cette taglib
     */
    TagConverterSource getTagConverter();

    /**
     * Retourne la source des convertisseurs de fonctions pour cette taglib.
     * 
     * Cette méthode fournit une source de convertisseurs capable de transformer
     * les fonctions spécifiques à cette bibliothèque de tags (utilisées dans les
     * expressions EL) en leurs équivalents Thymeleaf.
     * 
     * @return Une source de convertisseurs pour les fonctions de cette taglib
     */
    FunctionConverterSource getFunctionConverter();

}
