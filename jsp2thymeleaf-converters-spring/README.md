# Module JSP2Thymeleaf-Converters-Spring

## Description et Objectif

Le module jsp2thymeleaf-converters-spring fournit des convertisseurs spécialisés pour transformer les balises Spring MVC JSP en leurs équivalents Thymeleaf. Il se concentre sur les tags standards de Spring et permet une conversion fluide des vues JSP utilisant les bibliothèques Spring vers des templates Thymeleaf compatibles avec Spring MVC et Spring Boot.

## Architecture Interne et Composants Clés

### Structure du Module

```
jsp2thymeleaf-converters-spring/
├── src/main/java/com/cybernostics/jsp2thymeleaf/converters/spring/
│   ├── binds/           # Convertisseurs pour le binding de données Spring
│   ├── form/            # Convertisseurs pour les éléments de formulaire
│   ├── tags/            # Convertisseurs pour les tags standards Spring
│   ├── url/             # Convertisseurs pour les URL et redirections
│   └── SpringTagConverters.java # Point d'entrée pour les convertisseurs Spring
```

### Fonctionnalités Principales

1. **Conversion des Tags de URL Spring**
   * `spring:url` → `@{...}` (Expression URL de Thymeleaf)
   * Gestion des paramètres et variables de chemin

2. **Conversion des Tags de Messages**
   * `spring:message` → `#{...}` (Expression message de Thymeleaf)
   * Support pour les paramètres et valeurs par défaut

3. **Support pour les Tags de Thème et Resource**
   * `spring:theme` → Expressions Thymeleaf appropriées
   * `spring:eval` → Expressions d'évaluation Thymeleaf

4. **Intégration avec JSP2Thymeleaf Core**
   * Enregistrement automatique des convertisseurs
   * Configuration via l'API standard

## Guide d'Utilisation

### Intégration avec le Processus de Conversion

Les convertisseurs Spring sont automatiquement détectés et utilisés par le module principal jsp2thymeleaf. Aucune configuration supplémentaire n'est nécessaire si le module est présent dans le classpath.

### Utilisation Programmatique

```java
// Configuration avec convertisseurs Spring explicitement activés
JSP2ThymeleafConfiguration config = new JSP2ThymeleafConfiguration();
config.setSrcFolder(Paths.get("/chemin/vers/jsp"));
config.setDestFolder(Paths.get("/chemin/vers/thymeleaf"));

// Ajouter les convertisseurs Spring (fait automatiquement si dans le classpath)
config.registerConverters(new SpringTagConverters().getConverters());

// Exécuter la conversion
JSP2Thymeleaf converter = new JSP2Thymeleaf(config);
converter.run();
```

## Exemples de Conversion

### Exemple 1: URLs Spring

**JSP:**
```jsp
<spring:url value="/users/{id}" var="userUrl">
    <spring:param name="id" value="${user.id}" />
    <spring:param name="action" value="view" />
</spring:url>
<a href="${userUrl}">Voir l'utilisateur</a>
```

**Thymeleaf:**
```html
<a th:href="@{/users/{id}(id=${user.id},action='view')}">Voir l'utilisateur</a>
```

### Exemple 2: Messages Spring

**JSP:**
```jsp
<spring:message code="welcome.message" arguments="${user.name}" />
```

**Thymeleaf:**
```html
<span th:text="#{welcome.message(${user.name})}">Welcome message</span>
```

### Exemple 3: Évaluation d'Expressions Spring

**JSP:**
```jsp
<spring:eval expression="user.getFullName().toUpperCase()" />
```

**Thymeleaf:**
```html
<span th:text="${user.getFullName().toUpperCase()}">User name</span>
```

## Dépendances et Configuration

### Dépendances Principales

* **jsp2thymeleaf-api**: Interfaces de base pour les convertisseurs
* **jsp2thymeleaf**: Module principal de conversion
* **Spring Framework**: Pour les classes et utilitaires Spring
* **JDOM2**: Pour la manipulation de documents XML/HTML

## Instructions de Construction et de Test

### Construction du Module

```bash
cd jsp2thymeleaf-converters-spring
mvn clean compile
```

### Exécution des Tests

```bash
cd jsp2thymeleaf-converters-spring
mvn test
```

### Packaging du Module

```bash
cd jsp2thymeleaf-converters-spring
mvn package
```

## Liste des Tags Spring Supportés

| Tag Spring JSP | Statut | Équivalent Thymeleaf |
|----------------|--------|----------------------|
| `spring:url` | ✅ | `@{...}` avec paramètres |
| `spring:param` | ✅ | Paramètres dans URL expression |
| `spring:message` | ✅ | `#{...}` |
| `spring:theme` | ✅ | `#{...}` avec prefix spécifique |
| `spring:eval` | ✅ | `${...}` (expression standard) |
| `spring:escapeBody` | ⚠️ | Utilisation de `th:utext`/`th:text` |
| `spring:htmlEscape` | ⚠️ | Configuration Thymeleaf |
| `spring:hasBindErrors` | ⚠️ | `${#fields.hasErrors(...)}` |
| `spring:nestedPath` | ❌ | Structure différente en Thymeleaf |
| `spring:transform` | ❌ | Non supporté directement |

### Légende:
- ✅ Supporté complètement
- ⚠️ Supporté partiellement (peut nécessiter des ajustements manuels)
- ❌ Non supporté actuellement

## Limitations et Améliorations Futures

1. **Support Complet des Tags Avancés**
   * Intégration complète avec les fonctionnalités avancées de Spring Security
   * Support pour les scénarios de binding complexes

2. **Optimisation des Expressions Générées**
   * Génération d'expressions Thymeleaf plus idiomatiques
   * Optimisation pour les cas spécifiques à Spring Boot

3. **Documentation des Mappings**
   * Enrichir la documentation des mappings entre tags Spring JSP et attributs Thymeleaf
   * Fournir plus d'exemples pour les cas complexes
