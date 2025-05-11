# Points d'Extension de JSP2Thymeleaf API

Ce document décrit les points d'extension disponibles dans le module jsp2thymeleaf-api qui permettent d'étendre les fonctionnalités de conversion JSP vers Thymeleaf.

## 1. Convertisseurs de Balises (TagConverter)

### Description

Les convertisseurs de balises transforment des balises JSP spécifiques en leurs équivalents Thymeleaf.

### Interface à implémenter

```java
public interface TagConverter extends JSPElementNodeConverter {
    String getApplicableTag();
}
```

### Comment étendre

Créez une classe qui implémente l'interface `TagConverter`:

```java
public class MyCustomTagConverter implements TagConverter {
    
    @Override
    public String getApplicableTag() {
        return "custom:tag";
    }
    
    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
        // Logique de conversion personnalisée
        Element resultElement = new Element("div");
        // Configurer l'élément selon vos besoins
        return Collections.singletonList(resultElement);
    }
    
    // Implémentez les autres méthodes requises...
}
```

### Enregistrement

Enregistrez votre convertisseur personnalisé via `ScopedJSPConverters`:

```java
ScopedJSPConverters converters = new ScopedJSPConverters();
converters.addNodeConverter(new MyCustomTagConverter());
```

## 2. Convertisseurs d'Expressions EL (ExpressionVisitor)

### Description

Les visiteurs d'expressions permettent de convertir des expressions JSP EL en expressions Thymeleaf équivalentes.

### Interface à implémenter

```java
public interface ExpressionVisitor {
    void visit(Object o);
    void visitExpression(Expression o);
    // ...autres méthodes...
}
```

### Comment étendre

Étendez la classe `DefaultExpressionVisitor` ou implémentez `ExpressionVisitor` directement:

```java
public class MyCustomExpressionVisitor extends DefaultExpressionVisitor {
    private StringBuilder output = new StringBuilder();
    
    @Override
    public void visitNamedValue(NamedValue namedValue) {
        // Logique personnalisée pour convertir les variables nommées
        output.append("*{").append(namedValue.getName()).append("}");
    }
    
    // Implémentez d'autres méthodes selon vos besoins
    
    public String getOutput() {
        return output.toString();
    }
}
```

### Utilisation

```java
ExpressionWalker walker = new ExpressionWalker();
MyCustomExpressionVisitor visitor = new MyCustomExpressionVisitor();
walker.walkExpressionString("${user.name}", visitor);
String result = visitor.getOutput();
```

## 3. Convertisseurs de Bibliothèques de Tags (TaglibConverter)

### Description

Permettent de fournir des convertisseurs pour des bibliothèques de tags JSP complètes.

### Interface à implémenter

```java
public interface TaglibConverter {
    String forUri();
    TagConverterSource getTagConverter();
    FunctionConverterSource getFunctionConverter();
}
```

### Comment étendre

```java
public class MyTaglibConverter implements TaglibConverter {
    
    @Override
    public String forUri() {
        return "http://example.com/mytags";
    }
    
    @Override
    public TagConverterSource getTagConverter() {
        return tagName -> {
            // Retourner le convertisseur approprié pour chaque tag
            if (tagName.equals("mytag:example")) {
                return Optional.of(new MyExampleTagConverter());
            }
            return Optional.empty();
        };
    }
    
    @Override
    public FunctionConverterSource getFunctionConverter() {
        return functionName -> {
            // Retourner la transformation pour chaque fonction
            if (functionName.equals("myFunction")) {
                return Optional.of("#myThymeleafFunction");
            }
            return Optional.empty();
        };
    }
}
```

### Enregistrement

Pour l'enregistrement au niveau du système, utilisez le mécanisme de Service Loader de Java:

1. Créez un fichier `META-INF/services/com.cybernostics.jsp2thymeleaf.api.common.taglib.TaglibConverter`
2. Ajoutez le nom complet de votre implémentation dans ce fichier

## 4. Transformateurs de Chaînes (StringTransformers)

### Description

Permettent de créer des transformations personnalisées pour les valeurs dans les templates.

### Comment étendre

Ajoutez votre transformateur personnalisé:

```java
// Ajouter un transformateur qui convertit une chaîne en majuscules
StringTransformers.add("upperCase", value -> value.toString().toUpperCase());
```

### Utilisation

```java
// Utilisation dans un template
String template = "Hello %{name!upperCase}";
Map<String, Object> values = new HashMap<>();
values.put("name", "world");
String result = SimpleStringTemplateProcessor.generate(template, values);
// Résultat: "Hello WORLD"
```

## 5. Renommage d'Attributs Personnalisé

### Description

Création de mappings personnalisés pour renommer les attributs JSP en attributs Thymeleaf.

### Comment étendre

```java
// Définir des mappings de renommage personnalisés
List<AttributeRename> customRenames = new ArrayList<>();
customRenames.add(AttributeRename.from("data-binding").to("th:field"));
customRenames.add(AttributeRename.from("readonly").to("th:readonly"));

// Utiliser ces mappings dans un convertisseur personnalisé
public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context) {
    Element resultElement = new Element(node.name.getText());
    
    for (JSPParser.HtmlAttributeContext attr : node.atts) {
        String attrName = attr.name.getText();
        String attrValue = attr.value.getText();
        
        // Appliquer les renommages personnalisés
        Optional<AttributeRename> rename = customRenames.stream()
            .filter(r -> r.getFrom().equals(attrName))
            .findFirst();
            
        if (rename.isPresent()) {
            resultElement.setAttribute(rename.get().getTo(), attrValue);
        } else {
            resultElement.setAttribute(attrName, attrValue);
        }
    }
    
    return Collections.singletonList(resultElement);
}
```

## Résumé des Points d'Extension

Le module jsp2thymeleaf-api offre plusieurs points d'extension clés:

1. **Convertisseurs de Balises** - Pour convertir des balises JSP spécifiques
2. **Convertisseurs d'Expressions** - Pour transformer les expressions JSP EL
3. **Convertisseurs de Bibliothèques** - Pour prendre en charge des taglibs complètes
4. **Transformateurs de Chaînes** - Pour des transformations personnalisées dans les templates
5. **Mappings d'Attributs** - Pour renommer les attributs JSP en attributs Thymeleaf

Ces points d'extension permettent d'adapter et d'étendre le processus de conversion pour répondre à des besoins spécifiques ou prendre en charge des bibliothèques JSP personnalisées.
