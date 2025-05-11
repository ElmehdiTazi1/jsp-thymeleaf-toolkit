/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.api.elements;

/**
 * Classe utilitaire pour la conversion des attributs JSP en attributs Thymeleaf.
 * 
 * Cette classe facilite le renommage des attributs lors de la conversion
 * de JSP vers Thymeleaf. Elle utilise un pattern fluent (builder) pour
 * créer des mappings de renommage d'attributs de manière expressive et lisible.
 * 
 * Exemple d'utilisation:
 * <pre>
 *     AttributeRename rename = AttributeRename.from("readonly").to("th:readonly");
 * </pre>
 * 
 * Cette classe est particulièrement utile pour convertir les attributs JSP standards
 * ou ceux des taglibs vers les équivalents Thymeleaf, qui utilisent souvent des
 * préfixes comme "th:" ou des noms d'attributs différents.
 *
 * @author jason
 * @version 1.0
 */
public class AttributeRename {
    /** Nom de l'attribut source (JSP) */
  private String from;
  
  /** Nom de l'attribut cible (Thymeleaf) */
  private String to;

  /**
   * Constructeur privé utilisé par le builder.
   * 
   * @param from Nom de l'attribut source (JSP)
   * @param to Nom de l'attribut cible (Thymeleaf)
   */
  private AttributeRename(String from, String to) {
    this.from = from;
    this.to = to;
  }
  
  /**
   * Point d'entrée du pattern builder pour créer un AttributeRename.
   * 
   * Cette méthode statique initialise le processus de création d'un mapping
   * de renommage d'attribut en spécifiant l'attribut source.
   * 
   * @param fromStr Nom de l'attribut source (JSP)
   * @return Un builder pour continuer la construction de l'objet AttributeRename
   */
  public static AttributeRenameBuilder from(String fromStr){
    return new AttributeRenameBuilder(fromStr);
  }
  
  /**
   * Classe builder interne pour construire un AttributeRename de façon fluide.
   */
  public static class AttributeRenameBuilder{
    /** Nom de l'attribut source stocké temporairement */
    String from;

    /**
     * Constructeur privé utilisé par la méthode from().
     * 
     * @param from Nom de l'attribut source (JSP)
     */
    private AttributeRenameBuilder(String from) {
      this.from = from;
    }
    
    /**
     * Complète la création de l'AttributeRename en spécifiant l'attribut cible.
     * 
     * @param to Nom de l'attribut cible (Thymeleaf)
     * @return Un objet AttributeRename complet avec le mapping from->to
     */
    public AttributeRename to(String to){
      return new AttributeRename(from,to);
    }
    
  }

  /**
   * Récupère le nom de l'attribut source (JSP).
   * 
   * @return Le nom de l'attribut source
   */
  public String getFrom() {
    return from;
  }

  /**
   * Récupère le nom de l'attribut cible (Thymeleaf).
   * 
   * @return Le nom de l'attribut cible
   */
  public String getTo() {
    return to;
  }
  
  
  
}
