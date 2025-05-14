/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.api.elements;

import com.cybernostics.jsp2thymeleaf.api.common.DefaultElementConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Gère le contexte de conversion des éléments JSP avec les taglibs associées.
 * 
 * Cette classe joue un rôle central dans le processus de conversion en gardant trace
 * des convertisseurs de taglib qui ont été déclarés via les directives JSP taglib,
 * ainsi que des préfixes avec lesquels ils sont associés.
 * 
 * ScopedJSPConverters implémente un mécanisme de portée (scope) permettant de gérer
 * les contextes imbriqués. Lorsqu'un nouveau contexte est créé (par exemple lors de
 * l'entrée dans un élément imbriqué), il peut hériter des convertisseurs du contexte
 * parent tout en définissant de nouveaux convertisseurs locaux.
 * 
 * Fonctionnalités principales:
 * - Enregistrement des convertisseurs de taglib pour des préfixes spécifiques
 * - Recherche de convertisseurs pour des balises préfixées
 * - Gestion des espaces de noms actifs dans le contexte courant
 * - Support de l'héritage des contextes (pour gérer les portées imbriquées)
 * - Gestion des convertisseurs de nœuds et de fonctions
 * - Suivi des taglibs non supportées pour préserver leur contenu
 *
 * @author wjase
 * @version 1.0
 * @see JSPNodeConverterSource
 * @see JSPElementNodeConverter
 * @see TagConverter
 */
public class ScopedJSPConverters
{
    /** Le contexte parent, si présent, utilisé pour la recherche de convertisseurs en cascade */
    private Optional<ScopedJSPConverters> parentScope = Optional.empty();
    
    /** Source de convertisseurs par défaut utilisée pour les éléments sans préfixe */
    private static JSPNodeConverterSource DEFAULT_CONVERTER_SOURCE = new DefaultElementConverterSource();

    /** 
     * Map des convertisseurs actifs indexés par préfixe de taglib 
     * Par exemple, "c" -> convertisseur pour JSTL Core
     */
    private Map<String, JSPNodeConverterSource> activeTagConverters = new HashMap<>();
    
    /**
     * Map des taglibs non supportées avec leur URI.
     * Clé: préfixe de la taglib (ex: "spring")
     * Valeur: URI de la taglib (ex: "http://www.springframework.org/tags")
     */
    private Map<String, String> unsupportedTaglibs = new HashMap<>();

    /**
     * Constructeur par défaut.
     * 
     * Initialise un nouveau contexte de conversion sans parent et
     * ajoute le convertisseur par défaut pour les tags sans préfixe.
     */
    public ScopedJSPConverters()
    {
        addTaglibConverter("", new DefaultElementConverterSource());
    }

    /**
     * Constructeur avec contexte parent.
     * 
     * Initialise un nouveau contexte de conversion qui hérite
     * du contexte parent spécifié tout en permettant d'ajouter
     * des convertisseurs spécifiques à ce contexte.
     * 
     * @param parentScope Le contexte parent dont ce contexte hérite
     */
    public ScopedJSPConverters(ScopedJSPConverters parentScope)
    {
        this();
        this.parentScope = Optional.ofNullable(parentScope);
    }

    /**
     * Enregistre un convertisseur pour gérer les balises avec le préfixe spécifié.
     * 
     * Cette méthode est utilisée lorsqu'une directive taglib est rencontrée dans
     * le document JSP, pour associer un préfixe à un ensemble de convertisseurs.
     *
     * @param prefix Le préfixe de taglib (comme "c" pour JSTL Core)
     * @param converterSource La source de convertisseurs pour ce préfixe
     */
    public void addTaglibConverter(String prefix, JSPNodeConverterSource converterSource)
    {
        activeTagConverters.putIfAbsent(prefix, converterSource);
    }

    /**
     * Return the converter for a given prefix.
     *
     * @param prefix
     * @return
     */
    public Optional<JSPNodeConverterSource> forPrefix(String prefix)
    {
        JSPNodeConverterSource source = activeTagConverters.getOrDefault(prefix, null);
        if (source == null && parentScope.isPresent())
        {
            return parentScope.get().forPrefix(prefix);
        }
        return Optional.ofNullable(source);
    }

    private Map<String, FunctionConverterSource> activeExpressionConverters = new HashMap<>();

    /**
     * Vérifie si un préfixe correspond à une taglib non supportée.
     * 
     * @param prefix Le préfixe à vérifier
     * @return true si le préfixe correspond à une taglib non supportée, false sinon
     */
    public boolean isUnsupportedTaglib(String prefix) {
        if (unsupportedTaglibs.containsKey(prefix)) {
            return true;
        }
        if (parentScope.isPresent()) {
            return parentScope.get().isUnsupportedTaglib(prefix);
        }
        return false;
    }
    
    /**
     * Récupère l'URI associé à un préfixe de taglib non supportée.
     * 
     * @param prefix Le préfixe de la taglib
     * @return L'URI associé au préfixe, ou null si le préfixe n'est pas une taglib non supportée
     */
    public String getUnsupportedTaglibUri(String prefix) {
        String uri = unsupportedTaglibs.get(prefix);
        if (uri != null) {
            return uri;
        }
        if (parentScope.isPresent()) {
            return parentScope.get().getUnsupportedTaglibUri(prefix);
        }
        return null;
    }
    
    /**
     * Retourne le Map des taglibs non supportées.
     * 
     * @return Le Map des taglibs non supportées
     */
    public Map<String, String> getUnsupportedTaglibs() {
        return unsupportedTaglibs;
    }

    /**
     * Registers a given converter for handling tags with the prefix specified
     *
     * @param prefix
     * @param converterSource
     */
    public void addTaglibFunctionConverter(String prefix, FunctionConverterSource converterSource)
    {
        activeExpressionConverters.putIfAbsent(prefix, converterSource);
    }

    /**
     * Return the converter for a given prefix.
     *
     * @param prefix
     * @return
     */
    public Optional<FunctionConverterSource> functionConverterForPrefix(String prefix)
    {
        FunctionConverterSource source = activeExpressionConverters.getOrDefault(prefix, null);
        if (source == null && parentScope.isPresent())
        {
            return parentScope.get().functionConverterForPrefix(prefix);
        }
        return Optional.ofNullable(source);
    }

    public static JSPNodeConverterSource defaultSource()
    {
        return DEFAULT_CONVERTER_SOURCE;
    }

}
