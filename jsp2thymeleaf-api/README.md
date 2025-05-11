# JSP2Thymeleaf API Module

## Description et Objectif

Le module jsp2thymeleaf-api définit l'ensemble des interfaces et classes de base pour la conversion de JSP vers Thymeleaf. Il fournit un cadre de travail extensible permettant d'implémenter divers convertisseurs pour les différentes balises et expressions JSP. Ce module sert de contrat d'API pour tous les autres modules de conversion du projet.

## Architecture Interne et Composants Clés

### Structure du Module

```
jsp2thymeleaf-api/
├── src/main/java/
│   └── com/cybernostics/jsp2thymeleaf/api/
│       ├── common/             # Classes communes partagées
│       ├── elements/           # Interfaces pour convertir les éléments JSP
│       ├── exception/          # Exceptions spécifiques à la conversion
│       ├── expressions/        # Interfaces pour convertir les expressions EL
│       └── util/               # Classes utilitaires
```

### Interfaces Principales

1. **JSPElementNodeConverter**
   * Interface centrale pour convertir les nœuds JSP en éléments Thymeleaf
   * Définit la méthode `process` qui transforme un contexte JSP en contenu Thymeleaf

2. **TagConverter**
   * Étend JSPElementNodeConverter
   * Spécifique à la conversion d'une balise JSP particulière
   * Définit `getApplicableTag` pour identifier la balise que le convertisseur peut traiter

3. **AttributeRename**
   * Interface pour la conversion d'attributs JSP en attributs Thymeleaf
   * Permet de mapper des attributs entre les deux technologies

4. **ELExpressionConverter**
   * Interface pour convertir les expressions JSP EL (Expression Language)
   * Transforme la syntaxe JSP EL (${...}) en expressions Thymeleaf équivalentes

## Patterns de Design Utilisés

1. **Strategy Pattern**
   * Les convertisseurs implémentent une interface commune mais fournissent des implémentations différentes
   * Permet de sélectionner l'algorithme de conversion approprié en fonction du contexte

2. **Composite Pattern**
   * Les convertisseurs peuvent être composés pour traiter des structures JSP complexes
   * Les éléments JSP sont traités de manière récursive

3. **Factory Pattern**
   * Utilisation de sources de convertisseurs (TagConverterSource, JSPNodeConverterSource)
   * Création dynamique de convertisseurs en fonction des besoins

## Guide d'Utilisation

### Implémentation d'un Convertisseur de Balise Personnalisé

```java
public class CustomTagConverter implements TagConverter {

    @Override
    public String getApplicableTag() {
        return "custom:tag";
    }

    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
        // Logique de conversion de la balise custom:tag
        Element resultElement = new Element("div");
        resultElement.setAttribute("th:text", "${customValue}");
        return Collections.singletonList(resultElement);
    }

    @Override
    public boolean canHandle(JSPParser.JspElementContext node) {
        return node.name.getText().equals(getApplicableTag());
    }

    @Override
    public ScopedJSPConverters getScopedConverters() {
        // Implémentation requise
    }

    @Override
    public void setScopedConverters(ScopedJSPConverters scopedConverters) {
        // Implémentation requise
    }
}
```

### Enregistrement d'un Convertisseur dans le Système

```java
// Exemple d'enregistrement d'un convertisseur personnalisé
ScopedJSPConverters converters = new ScopedJSPConverters();
CustomTagConverter customConverter = new CustomTagConverter();
converters.addNodeConverter(customConverter);
```

## Dépendances et Configuration

### Dépendances Principales

* **jsp-parser**: Module interne pour l'analyse des fichiers JSP
* **JDOM2**: Bibliothèque pour la manipulation de documents XML/HTML
* **JUnit (4.12)**: Pour les tests unitaires

### Configuration Maven

Le module est configuré comme un artefact Maven standard avec les dépendances appropriées et la configuration de build nécessaire pour générer un JAR utilisable par d'autres modules.

## Instructions de Construction et de Test

### Construction du Module

```bash
cd jsp2thymeleaf-api
mvn clean compile
```

### Exécution des Tests

```bash
cd jsp2thymeleaf-api
mvn test
```

### Packaging du Module

```bash
cd jsp2thymeleaf-api
mvn package
```

## Exemples d'Utilisation

### Exemple 1: Conversion d'une Expression EL

```java
// Créer un convertisseur d'expressions
ELExpressionConverter expressionConverter = new DefaultELExpressionConverter();

// Convertir une expression JSP EL vers Thymeleaf
String jspExpression = "${user.name}";
String thymeleafExpression = expressionConverter.convert(jspExpression);
// Résultat: "*{user.name}" ou autre format Thymeleaf selon l'implémentation
```

### Exemple 2: Traitement d'un Nœud JSP

```java
// Récupérer un nœud JSP depuis le parser
JSPParser.JspElementContext jspNode = ...;

// Créer le contexte de conversion
ScopedJSPConverters converters = new ScopedJSPConverters();
// Ajouter les convertisseurs nécessaires...

// Créer un convertisseur pour le nœud
JSPElementNodeConverter nodeConverter = determineConverter(jspNode, converters);

// Effectuer la conversion
List<Content> thymeleafContent = nodeConverter.process(jspNode, nodeConverter);
```

## Points d'Extension

L'API est conçue pour être extensible, permettant d'ajouter facilement de nouveaux convertisseurs pour:

1. **Nouvelles balises JSP**: en implémentant l'interface TagConverter
2. **Nouveaux formats d'expressions**: en implémentant l'interface ELExpressionConverter
3. **Transformations spéciales**: en implémentant des convertisseurs personnalisés

## Limitations Connues et Améliorations Futures

* Support complet des attributs dynamiques dans les balises JSP
* Amélioration de la conversion des expressions EL complexes
* Prise en charge des contextes imbriqués de manière plus robuste
* Outils de diagnostic pour aider à comprendre les problèmes de conversion
