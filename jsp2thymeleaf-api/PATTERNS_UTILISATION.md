# Patterns d'Utilisation de JSP2Thymeleaf API

Ce document présente des patterns d'utilisation des interfaces principales du module jsp2thymeleaf-api.

## 1. Conversion d'un Élément JSP

Le pattern principal pour convertir un élément JSP en élément Thymeleaf utilise `JSPElementNodeConverter`:

```java
// Créer un contexte de conversion avec les convertisseurs nécessaires
ScopedJSPConverters converters = new ScopedJSPConverters();

// Ajouter des convertisseurs spécifiques, par ex. pour JSTL Core (préfixe "c")
converters.addTaglibConverter("c", new JSTLCoreConverterSource());

// Créer un convertisseur pour un nœud spécifique
JSPElementNodeConverter myConverter = new CustomTagConverter();
myConverter.setScopedConverters(converters);

// Convertir le nœud JSP (obtenu via le parser JSP)
JSPParser.JspElementContext node = ...;
List<Content> thymeleafContent = myConverter.process(node, myConverter);

// Utiliser le contenu Thymeleaf généré
// ...
```

## 2. Implémentation d'un Convertisseur de Tag Personnalisé

Pour créer un convertisseur pour un tag spécifique:

```java
public class MyCustomTagConverter implements TagConverter {
    
    private ScopedJSPConverters scopedConverters;
    
    @Override
    public String getApplicableTag() {
        return "custom:tag"; // Le tag que ce convertisseur peut gérer
    }
    
    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
        // Extraire les attributs du tag
        String attributeValue = node.atts.stream()
                .filter(att -> att.name != null && att.name.getText().equals("myAttribute"))
                .map(att -> att.value.getText())
                .findFirst().orElse("");
                
        // Créer un élément Thymeleaf équivalent
        Element resultElement = new Element("div");
        resultElement.setAttribute("th:text", attributeValue);
        
        return Collections.singletonList(resultElement);
    }
    
    @Override
    public boolean canHandle(JSPParser.JspElementContext node) {
        return node.name != null && node.name.getText().equals(getApplicableTag());
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
```

## 3. Renommage d'Attributs

Pour renommer les attributs JSP en attributs Thymeleaf:

```java
// Créer un mapping de renommage d'attributs
AttributeRename rename = AttributeRename.from("readonly").to("th:readonly");

// Utiliser le renommage dans un convertisseur
public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
    Element resultElement = new Element(node.name.getText());
    
    // Appliquer le renommage aux attributs du nœud
    for (JSPParser.HtmlAttributeContext attr : node.atts) {
        if (attr.name != null && attr.name.getText().equals("readonly")) {
            resultElement.setAttribute("th:readonly", attr.value.getText());
        } else {
            resultElement.setAttribute(attr.name.getText(), attr.value.getText());
        }
    }
    
    return Collections.singletonList(resultElement);
}
```

## 4. Création d'un Convertisseur de Taglib

Pour créer un convertisseur pour une bibliothèque de tags complète:

```java
public class MyCustomTaglibConverter implements TaglibConverter {
    
    @Override
    public String forUri() {
        return "http://example.com/mytags"; // L'URI de la taglib
    }
    
    @Override
    public TagConverterSource getTagConverter() {
        // Créer une source qui fournira des convertisseurs pour les tags
        return new TagConverterSource() {
            @Override
            public Optional<TagConverter> converterFor(String tagName) {
                switch (tagName) {
                    case "mytag:custom":
                        return Optional.of(new MyCustomTagConverter());
                    case "mytag:other":
                        return Optional.of(new MyOtherTagConverter());
                    default:
                        return Optional.empty();
                }
            }
        };
    }
    
    @Override
    public FunctionConverterSource getFunctionConverter() {
        // Créer une source qui fournira des convertisseurs pour les fonctions
        // utilisées dans les expressions EL
        return new FunctionConverterSource() {
            @Override
            public Optional<String> converterFor(String functionName) {
                switch (functionName) {
                    case "myfunction":
                        return Optional.of("#myThymeleafFunction");
                    default:
                        return Optional.empty();
                }
            }
        };
    }
}
```

## 5. Traitement des Expressions JSP EL

Pour convertir des expressions JSP EL en expressions Thymeleaf équivalentes:

```java
// Créer un visiteur d'expressions
ExpressionVisitor visitor = new ExpressionWritingVisitor();

// Créer un walker d'expressions pour traverser l'arbre
ExpressionWalker walker = new ExpressionWalker();

try {
    // Convertir une expression JSP EL
    String jspExpression = "${user.name}";
    walker.walkExpressionString(jspExpression, visitor);
    
    // Récupérer l'expression Thymeleaf générée
    String thymeleafExpression = ((ExpressionWritingVisitor) visitor).getOutput();
    
} catch (ParseException e) {
    // Gérer les erreurs de parsing d'expressions
}
```

## 6. Utilisation de Templates pour Générer du Code Thymeleaf

Pour générer du code Thymeleaf à partir de templates paramétrés:

```java
// Créer un template avec des placeholders
String template = "<%{element} th:%{attribute}=\"%{value!stripEL}\">%{content}</%{element}>";

// Préparer les valeurs de remplacement
Map<String, Object> values = new HashMap<>();
values.put("element", "div");
values.put("attribute", "text");
values.put("value", "${message}");
values.put("content", "Hello World");

// Générer le code Thymeleaf
String thymeleafCode = SimpleStringTemplateProcessor.generate(template, values);
// Résultat: "<div th:text="message">Hello World</div>"
```

Ces patterns d'utilisation illustrent les principales façons d'utiliser les interfaces du module jsp2thymeleaf-api pour implémenter des convertisseurs JSP vers Thymeleaf personnalisés et extensibles.
