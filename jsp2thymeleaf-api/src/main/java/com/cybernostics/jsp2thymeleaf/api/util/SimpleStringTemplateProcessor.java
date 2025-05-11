/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.api.util;

import static com.cybernostics.jsp2thymeleaf.api.util.KeyValueMapFormatter.expandMap;
import java.util.Map;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;

/**
 * Processeur de templates pour générer des chaînes de caractères à partir de templates.
 * 
 * Cette classe facilite la génération de chaînes de caractères basées sur des templates
 * contenant des placeholders. Elle est particulièrement utile pour générer du code Thymeleaf
 * à partir de modèles paramétrés, où les valeurs réelles sont injectées lors de la conversion.
 * 
 * Les templates utilisent le format %{cle} pour les placeholders. Des transformations
 * peuvent être appliquées aux valeurs en utilisant le format %{cle!transformation}.
 * Par exemple: %{valeur!ucFirst} pour mettre la première lettre en majuscule.
 * 
 * Exemple d'utilisation:
 * <pre>
 *   Map&lt;String, Object&gt; values = new HashMap&lt;&gt;();
 *   values.put("element", "div");
 *   values.put("attr", "text");
 *   values.put("value", "${message}");
 *   
 *   String template = "&lt;%{element} th:%{attr}=\"%{value!stripEL}\"&gt;&lt;/%{element}&gt;";
 *   String result = SimpleStringTemplateProcessor.generate(template, values);
 *   // Résultat: "<div th:text="message"></div>"
 * </pre>
 *
 * @author jason
 * @version 1.0
 */
public class SimpleStringTemplateProcessor
{

    /**
     * Initialisation statique de la classe.
     * Ajoute le transformateur "kvMap" qui permet de formater un Map en paires clé-valeur.
     */
    static
    {
        StringTransformers.add("kvMap", o -> expandMap(o));
    }

    /** Le format de template à utiliser */
    private final String format;

    /**
     * Constructeur qui initialise le processeur avec un template spécifique.
     * 
     * @param format Le template à utiliser pour la génération
     */
    public SimpleStringTemplateProcessor(String format)
    {
        this.format = format;
    }

    /**
     * Génère une chaîne de caractères en remplaçant les placeholders du template
     * par les valeurs fournies.
     * 
     * @param values Map contenant les valeurs de remplacement (clé -> valeur)
     * @return La chaîne générée avec les placeholders remplacés par leurs valeurs
     */
    public String generate(Map<String, Object> values)
    {
        return getSubstitutor(values).replace(format);
    }

    /**
     * Crée un substitutor configuré pour utiliser les valeurs fournies.
     * 
     * @param values Map contenant les valeurs de remplacement
     * @return Un StrSubstitutor configuré pour le remplacement de variables
     */
    public static StrSubstitutor getSubstitutor(Map<String, Object> values)
    {
        return getSubstitutor(new ValueResolverWithDefaultAndFilter(values));
    }

    /**
     * Crée un substitutor configuré avec le résolveur de variables fourni.
     * 
     * Ce substitutor est configuré pour utiliser %{ comme préfixe de variable
     * et % comme caractère d'échappement.
     * 
     * @param variableResolver Le résolveur de variables à utiliser
     * @return Un StrSubstitutor configuré pour le remplacement de variables
     */
    public static StrSubstitutor getSubstitutor(StrLookup<String> variableResolver)
    {
        StrSubstitutor strSubstitutor = new StrSubstitutor(variableResolver);
        strSubstitutor.setEscapeChar('%');
        strSubstitutor.setVariablePrefix("%{");
        strSubstitutor.setEnableSubstitutionInVariables(false);
        return strSubstitutor;
    }

    public static String generate(String inputFormat, Map<String, Object> values)
    {
        return getSubstitutor(values).replace(inputFormat);
    }

}
