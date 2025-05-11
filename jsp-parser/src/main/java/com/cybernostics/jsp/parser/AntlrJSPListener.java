/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp.parser;

/**
 * Classe d'écoute (listener) ANTLR pour traiter les événements lors du parcours 
 * de l'arbre syntaxique d'un document JSP. Cette classe est responsable de réagir 
 * aux différents éléments rencontrés dans le document JSP pendant l'analyse.
 * 
 * Elle peut être étendue pour implémenter des comportements spécifiques
 * lors de la reconnaissance des éléments JSP comme les directives, expressions,
 * éléments HTML, etc.
 * 
 * Le listener implémente le pattern Visitor en utilisant le framework ANTLR4 pour
 * parcourir l'arbre syntaxique généré à partir du document JSP. Il fournit des méthodes
 * pour réagir aux différents types d'éléments JSP comme:
 * - Éléments JSP (balises)
 * - Directives JSP (<%@ ... %>)
 * - Expressions JSP (${...})
 * - Scriptlets (<% ... %>)
 * - Contenu HTML
 *
 * @author jason
 * @version 1.0
 */
public class AntlrJSPListener extends JSPParserBaseListener
{
    /** 
     * Instance du parser JSP utilisé pour l'analyse
     */
    private final JSPParser parser;

    public AntlrJSPListener(JSPParser parser)
    {
        this.parser = parser;
    }    /**
     * Méthode appelée lorsque le parser entre dans un document JSP.
     * Cette méthode est invoquée au début de l'analyse du document JSP complet.
     * Point d'entrée idéal pour initialiser des ressources nécessaires au traitement.
     *
     * @param ctx Le contexte du document JSP, contenant toutes les informations sur le document
     */
    @Override
    public void enterJspDocument(JSPParser.JspDocumentContext ctx)
    {;
    }

    /**
     * Méthode appelée lorsque le parser entre dans un élément JSP.
     * Traite les éléments JSP/HTML rencontrés dans le document et affiche leurs informations.
     * Analyse également les attributs associés à l'élément.
     *
     * @param ctx Le contexte de l'élément JSP, contenant le nom de la balise, ses attributs et son contenu
     */
    @Override
    public void enterJspElement(JSPParser.JspElementContext ctx)
    {
        System.out.println("Element:" + ctx.getText());
        System.out.println("has content" + !ctx.htmlContent().isEmpty());
        ctx.atts.stream()
                .filter(att -> att.name != null)
                .forEach(att -> System.out.println(att.name.getText() + getAttValue(att.value)));

    }

    /**
     * Méthode appelée lorsque le parser entre dans un scriptlet JSP.
     * Les scriptlets sont des blocs de code Java intégrés dans un document JSP.
     * Cette méthode peut être étendue pour traiter le contenu des scriptlets.
     *
     * @param ctx Le contexte du scriptlet JSP, contenant le code Java du scriptlet
     */
    @Override
    public void enterScriptlet(JSPParser.ScriptletContext ctx)
    {
    }    /**
     * Méthode appelée lorsque le parser entre dans une directive JSP.
     * Les directives JSP sont des instructions de traitement comme <%@ page %> ou <%@ taglib %>.
     * Cette méthode affiche les informations sur la directive et ses attributs.
     *
     * @param ctx Le contexte de la directive JSP, contenant son nom et ses attributs
     */
    @Override
    public void enterJspDirective(JSPParser.JspDirectiveContext ctx)
    {
        System.out.println("JSP Directive:" + ctx.name.getText() + ctx.getRuleIndex());
        ctx.atts.stream().forEach(att -> System.out.println(att.name.getText() + "=>" + att.value.toStringTree()));
        super.enterJspDirective(ctx);
    }

    /**
     * Méthode utilitaire pour limiter la taille d'une chaîne de caractères.
     * Utilisée pour formater les sorties et éviter l'affichage de textes trop longs.
     *
     * @param input La chaîne d'entrée à formater
     * @return La chaîne tronquée à 50 caractères si nécessaire, avec "..." à la fin
     */
    private String cap(String input)
    {
        if (input.length() > 50)
        {
            return input.substring(0, 50) + "...";
        }
        return input;
    }

    /**
     * Récupère la valeur d'un attribut HTML sous forme de chaîne formatée.
     * Gère les cas où la valeur est null.
     *
     * @param value Le contexte de la valeur d'attribut HTML
     * @return La valeur formatée de l'attribut, ou "nil" si la valeur est null
     */
    private String getAttValue(JSPParser.HtmlAttributeValueContext value)
    {
        return "=>" + (value != null ? value.getText() : "nil");
    }

    /**
     * Méthode appelée lorsque le parser entre dans une expression de valeur d'attribut HTML.
     * Ces expressions sont typiquement des expressions JSP comme ${...} utilisées comme valeurs d'attributs.
     *
     * @param ctx Le contexte de l'expression de valeur d'attribut HTML
     */
    @Override
    public void enterHtmlAttributeValueExpr(JSPParser.HtmlAttributeValueExprContext ctx)
    {
        System.out.println("Expresion:" + ctx.getText());
    }

}
