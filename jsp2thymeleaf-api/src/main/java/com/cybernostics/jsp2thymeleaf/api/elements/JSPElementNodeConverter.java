/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.api.elements;

import com.cybernostics.jsp.parser.JSPParser;
import java.util.List;
import org.jdom2.Content;

/**
 * Interface principale pour la conversion des nœuds JSP en éléments Thymeleaf.
 * 
 * Cette interface définit le contrat fondamental pour tous les convertisseurs d'éléments JSP.
 * Elle est responsable de la transformation des nœuds JSP en contenu Thymeleaf équivalent
 * tout en gérant les informations de contexte comme les espaces de noms (taglibs) actifs.
 * 
 * Les implémentations de cette interface peuvent traiter différents types d'éléments JSP,
 * comme des balises standards, des directives, des expressions, etc.
 * 
 * Ce convertisseur joue un rôle central dans le pipeline de conversion JSP vers Thymeleaf,
 * car il définit comment chaque type d'élément JSP doit être transformé.
 * 
 * @author jason
 * @version 1.0
 * @see TagConverter
 * @see JSPDirectiveConverter
 * @see ScopedJSPConverters
 */
public interface JSPElementNodeConverter
{    /**
     * Transforme un nœud JSP en une liste d'éléments Thymeleaf qui seront insérés
     * dans le document cible. 
     * 
     * Cette méthode est au cœur du processus de conversion. Elle est responsable 
     * de la transformation effective des éléments JSP en leurs équivalents Thymeleaf.
     * 
     * Note importante: Il n'y a pas nécessairement une correspondance 1:1 entre les
     * éléments d'entrée et de sortie. Un élément JSP peut être transformé en plusieurs
     * éléments Thymeleaf, ou peut être complètement ignoré dans certains cas. Lorsqu'un
     * élément est ignoré, il est préférable de le remplacer par un commentaire HTML
     * pour garder une trace de l'élément original.
     *
     * @param node Le nœud JSP à convertir
     * @param context Le contexte de conversion fournissant des informations sur les taglibs et convertisseurs de fonctions actifs
     * @return Une liste d'éléments Thymeleaf qui remplaceront le nœud JSP dans le document final
     */
    List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context);

    /**
     * Détermine si ce convertisseur peut traiter le nœud JSP spécifié.
     * 
     * Cette méthode permet aux implémentations de déterminer si elles sont
     * capables de convertir un nœud JSP particulier. Elle est utilisée dans le
     * processus de sélection du convertisseur approprié pour chaque nœud.
     *
     * @param node Le nœud JSP à évaluer
     * @return true si ce convertisseur peut traiter le nœud, false sinon
     */
    boolean canHandle(JSPParser.JspElementContext node);

    /**
     * Récupère les convertisseurs contextuels actuellement associés à ce convertisseur.
     * 
     * Les convertisseurs contextuels sont utilisés pour la conversion des éléments
     * enfants et des expressions dans le contexte du nœud actuel.
     *
     * @return L'objet ScopedJSPConverters contenant tous les convertisseurs disponibles dans ce contexte
     */
    ScopedJSPConverters getScopedConverters();

    /**
     * Définit les convertisseurs contextuels associés à ce convertisseur.
     * 
     * Cette méthode permet d'injecter les convertisseurs qui seront utilisés
     * lors de la conversion des éléments enfants et des expressions.
     *
     * @param scopedConverters L'objet ScopedJSPConverters contenant tous les convertisseurs à utiliser
     */
    void setScopedConverters(ScopedJSPConverters scopedConverters);

}
