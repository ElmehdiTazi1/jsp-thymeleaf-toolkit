/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.api.elements;

/**
 * Interface spécialisée pour la conversion des balises JSP en éléments Thymeleaf.
 * 
 * Cette interface étend {@link JSPElementNodeConverter} et ajoute des fonctionnalités
 * spécifiques aux convertisseurs de balises. Elle permet d'identifier précisément
 * quelle balise JSP un convertisseur est capable de traiter.
 * 
 * Les implémentations de cette interface sont responsables de la conversion
 * d'une balise JSP spécifique (comme "c:if", "c:forEach", "fmt:message", etc.)
 * vers son équivalent Thymeleaf.
 * 
 * L'approche par convertisseur de balise permet une grande modularité et extensibilité
 * du système de conversion, car de nouveaux convertisseurs peuvent être ajoutés pour
 * prendre en charge des balises personnalisées ou des bibliothèques tierces.
 *
 * @author jason
 * @version 1.0
 * @see JSPElementNodeConverter
 * @see MultipleTagConverter
 */
public interface TagConverter extends JSPElementNodeConverter
{
    /**
     * Retourne le nom de la balise que ce convertisseur peut traiter.
     * 
     * Cette méthode permet d'identifier précisément quelle balise JSP
     * ce convertisseur est capable de transformer. Le nom doit inclure
     * le préfixe de l'espace de noms (par exemple "c:out", "fmt:message").
     * 
     * Cette information est utilisée par le système de conversion pour
     * sélectionner le convertisseur approprié pour chaque balise rencontrée.
     *
     * @return Le nom complet de la balise JSP (avec préfixe) que ce convertisseur traite
     */
    public String getApplicableTag();
    

}
