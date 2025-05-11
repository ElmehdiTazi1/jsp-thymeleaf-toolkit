# Module JSP2Thymeleaf-Converters-Spring-Form

## Description et Objectif

Le module jsp2thymeleaf-converters-spring-form est dédié à la conversion des balises de formulaire Spring MVC JSP (`form:*`) vers leur équivalent Thymeleaf. Ce module est essentiel pour la migration des applications web Spring MVC qui utilisent intensivement les formulaires. Il permet de convertir automatiquement les formulaires JSP en templates Thymeleaf tout en préservant la fonctionnalité de binding de données et de validation.

## Architecture Interne et Composants Clés

### Structure du Module

```
jsp2thymeleaf-converters-spring-form/
├── src/main/java/com/cybernostics/jsp2thymeleaf/converters/springform/
│   ├── converters/       # Implémentations des convertisseurs pour chaque tag form:*
│   ├── model/            # Classes de modèle pour la représentation des formulaires
│   ├── utils/            # Utilitaires pour le traitement des attributs et expressions
│   └── SpringFormTagConverters.java # Point d'entrée pour les convertisseurs de formulaire
```

### Fonctionnalités Principales

1. **Conversion des balises formulaire**
   * `form:form` → `<form th:action="..." th:object="...">`
   * Gestion du modelAttribute et des méthodes HTTP

2. **Conversion des champs de formulaire**
   * `form:input`, `form:textarea`, etc. → `<input th:field="*{...}">`
   * Préservation des attributs HTML standard et personnalisés

3. **Conversion des balises de sélection**
   * `form:select`, `form:options`, `form:option` → Structure Thymeleaf équivalente
   * Support pour les listes statiques et dynamiques

4. **Gestion des erreurs**
   * `form:errors` → `th:errors` et `th:if="${#fields.hasErrors('...')}"` 
   * Support pour les messages d'erreur globaux et par champ

## Guide d'Utilisation

### Intégration avec le Processus de Conversion

Les convertisseurs Spring Form sont automatiquement détectés et utilisés par le module principal jsp2thymeleaf. Aucune configuration supplémentaire n'est nécessaire si le module est présent dans le classpath.

### Configuration Manuelle

```java
// Configuration avec convertisseurs Spring Form explicitement activés
JSP2ThymeleafConfiguration config = new JSP2ThymeleafConfiguration();
config.setSrcFolder(Paths.get("/chemin/vers/jsp"));
config.setDestFolder(Paths.get("/chemin/vers/thymeleaf"));

// Ajouter les convertisseurs Spring Form (fait automatiquement si dans le classpath)
config.registerConverters(new SpringFormTagConverters().getConverters());

// Exécuter la conversion
JSP2Thymeleaf converter = new JSP2Thymeleaf(config);
converter.run();
```

## Exemples de Conversion

### Exemple 1: Formulaire Simple

**JSP:**
```jsp
<form:form action="/users/save" method="post" modelAttribute="user">
    <div class="form-group">
        <form:label path="name">Nom</form:label>
        <form:input path="name" class="form-control" />
        <form:errors path="name" cssClass="text-danger" />
    </div>
    <button type="submit">Enregistrer</button>
</form:form>
```

**Thymeleaf:**
```html
<form th:action="@{/users/save}" th:object="${user}" method="post">
    <div class="form-group">
        <label for="name">Nom</label>
        <input type="text" th:field="*{name}" class="form-control" />
        <span class="text-danger" th:if="${#fields.hasErrors('name')}" th:errors="*{name}">Erreur</span>
    </div>
    <button type="submit">Enregistrer</button>
</form>
```

### Exemple 2: Select avec Options Dynamiques

**JSP:**
```jsp
<form:select path="country" items="${countries}" itemValue="code" itemLabel="name" />
```

**Thymeleaf:**
```html
<select th:field="*{country}">
    <option th:each="country : ${countries}" th:value="${country.code}" th:text="${country.name}">Pays</option>
</select>
```

### Exemple 3: Checkboxes

**JSP:**
```jsp
<form:checkboxes path="roles" items="${availableRoles}" />
```

**Thymeleaf:**
```html
<div th:each="role : ${availableRoles}">
    <input type="checkbox" th:field="*{roles}" th:value="${role}" />
    <label th:for="${#ids.prev('roles')}" th:text="${role}">Rôle</label>
</div>
```

## Liste des Tags Spring Form Supportés

| Tag Spring Form | Statut | Équivalent Thymeleaf |
|-----------------|--------|----------------------|
| `form:form` | ✅ | `<form th:action="..." th:object="...">` |
| `form:input` | ✅ | `<input type="text" th:field="*{...}">` |
| `form:password` | ✅ | `<input type="password" th:field="*{...}">` |
| `form:hidden` | ✅ | `<input type="hidden" th:field="*{...}">` |
| `form:textarea` | ✅ | `<textarea th:field="*{...}">` |
| `form:checkbox` | ✅ | `<input type="checkbox" th:field="*{...}">` |
| `form:checkboxes` | ✅ | Structure avec `th:each` et checkbox |
| `form:radiobutton` | ✅ | `<input type="radio" th:field="*{...}">` |
| `form:radiobuttons` | ✅ | Structure avec `th:each` et radio |
| `form:select` | ✅ | `<select th:field="*{...}">` |
| `form:option` | ✅ | `<option th:value="..." th:text="...">` |
| `form:options` | ✅ | `<option>` avec `th:each` |
| `form:errors` | ✅ | `<span th:if="${#fields.hasErrors('...')}" th:errors="*{...}">` |
| `form:label` | ✅ | `<label for="...">` |

### Légende:
- ✅ Supporté complètement
- ⚠️ Supporté partiellement (peut nécessiter des ajustements manuels)
- ❌ Non supporté actuellement

## Dépendances et Configuration

### Dépendances Principales

* **jsp2thymeleaf-api**: Interfaces de base pour les convertisseurs
* **jsp2thymeleaf**: Module principal de conversion
* **Spring Framework**: Pour les classes et utilitaires Spring MVC
* **JDOM2**: Pour la manipulation de documents XML/HTML

## Instructions de Construction et de Test

### Construction du Module

```bash
cd jsp2thymeleaf-converters-spring-form
mvn clean compile
```

### Exécution des Tests

```bash
cd jsp2thymeleaf-converters-spring-form
mvn test
```

### Packaging du Module

```bash
cd jsp2thymeleaf-converters-spring-form
mvn package
```

## Cas d'Utilisation Avancés

### 1. Formulaires Imbriqués

La conversion de formulaires imbriqués nécessite une attention particulière. Thymeleaf utilise une approche différente pour naviguer dans les objets imbriqués:

**JSP:**
```jsp
<form:form modelAttribute="order">
    <form:input path="customer.name" />
    <form:input path="customer.email" />
</form:form>
```

**Thymeleaf:**
```html
<form th:object="${order}">
    <input type="text" th:field="*{customer.name}" />
    <input type="text" th:field="*{customer.email}" />
</form>
```

### 2. Validation Personnalisée

Pour les cas de validation personnalisée, des ajustements manuels peuvent être nécessaires:

**JSP:**
```jsp
<form:errors path="*" cssClass="global-errors" />
```

**Thymeleaf:**
```html
<div class="global-errors" th:if="${#fields.hasAnyErrors()}">
    <div th:each="err : ${#fields.allErrors()}" th:text="${err}">Erreur</div>
</div>
```

## Limitations et Améliorations Futures

1. **Support pour les Attributs Personnalisés**
   * Meilleure gestion des attributs personnalisés dans les balises form
   * Support pour les extensions Spring Form personnalisées

2. **Amélioration des Messages d'Erreur Contextuels**
   * Conversion plus fine des messages d'erreur contextels
   * Support pour les variables dans les messages d'erreur

3. **Optimisation pour Spring Boot**
   * Génération de code optimisé pour les conventions Spring Boot
   * Intégration avec les mécanismes de validation de Spring Boot

4. **Documentation des Cas Complexes**
   * Enrichir la documentation pour les scénarios complexes
   * Fournir plus d'exemples pour les cas particuliers
