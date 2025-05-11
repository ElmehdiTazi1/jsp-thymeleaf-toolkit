
# JSP2Thymeleaf - Un Convertisseur JSP vers Thymeleaf Extensible

Ce module est le cœur du projet jsp-thymeleaf-toolkit, fournissant un framework complet pour automatiser environ 95% de la conversion des fichiers JSP vers Thymeleaf.

## Fonctionnalités Principales

1. **Parser JSP Robuste**
   * Basé sur une grammaire ANTLR4 complète pour une analyse précise
   * Parse efficacement la syntaxe JSP complexe incluant les directives, scriptlets et expressions
   * Gère les balises standards, JSTL, et bibliothèques de tags personnalisées
   * Préserve la structure et les commentaires des documents sources

2. **Framework de Conversion Extensible**
   * Architecture modulaire avec des points d'extension clairement définis
   * Convertisseurs intégrés pour les balises JSP standard et bibliothèques JSTL (Core, fmt, fn)
   * Mécanisme d'extension pour ajouter vos propres convertisseurs de taglibs personnalisées
   * Support multi-langages permettant d'implémenter des convertisseurs en Java, Groovy ou Python
   * Système de plugins SPI (Service Provider Interface) pour charger des convertisseurs externes

3. **Conversion Intelligente**
   * Transformation efficace des expressions EL (Expression Language) en expressions Thymeleaf équivalentes
   * Conversion contextuelle des attributs JSP en attributs Thymeleaf
   * Gestion des scopes et des espaces de noms de taglibs
   * Post-traitement pour optimiser le code généré et améliorer sa lisibilité

4. **Intégration Complète**
   * Utilisable en ligne de commande pour des conversions rapides
   * API Java flexible pour une intégration dans vos propres outils
   * Plugin Maven configurable pour automatiser les conversions dans votre pipeline de build
   * Fonctionne avec la bibliothèque spring-thymeleaf-jsp pour une migration progressive
   * Options avancées de configuration pour personnaliser le processus de conversion

## Architecture Interne

Le module est structuré en plusieurs composants clés:

- **Configuration et Pilotage**: Classes principales (`JSP2Thymeleaf`, `JSP2ThymeleafConfiguration`) pour configurer et exécuter les conversions
- **Convertisseurs**: Implémentations spécifiques pour chaque type de balise JSP, organisées par bibliothèque (standard, JSTL, etc.)
- **Parser et Transformateurs**: Classes pour analyser (`JSPParser`) et transformer (`JSP2ThymeleafTransformerListener`) le code JSP
- **Post-processeurs**: Classes pour optimiser et nettoyer le document Thymeleaf généré (indentation, suppression d'éléments inutiles)
- **Gestionnaires d'Expressions**: Classes spécialisées pour transformer les expressions JSP EL en expressions Thymeleaf

## Guide d'Utilisation

### Exécution en Ligne de Commande

```bash
java -jar jsp2thymeleaf.jar --src [dossier_source] --dest [dossier_destination] [options]
```

Options principales:
- `--src`: Dossier contenant les fichiers JSP source (obligatoire)
- `--dest`: Dossier où seront générés les fichiers Thymeleaf (obligatoire)
- `--includes`: Patterns d'inclusion au format glob (ex: "**/*.jsp")
- `--excludes`: Patterns d'exclusion au format glob
- `--taglibs`: Liste des convertisseurs de taglibs personnalisés (*.groovy, *.py)
- `--urimap`: Fichier de mapping des URI personnalisés pour vos taglibs JSP
- `--no-banner`: Supprime l'affichage de la bannière d'information

Exemple:
```bash
java -jar jsp2thymeleaf.jar --src ./webapp/WEB-INF/views --dest ./webapp/WEB-INF/templates --includes "**/*.jsp" --excludes "**/fragments/**"
```

### Utilisation avec Maven

Incluez le plugin `com.cybernostics:jsp2tl-maven-plugin` dans votre fichier pom.xml:

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <src>${basedir}/src/main/webapp/WEB-INF/views</src>
        <dest>${basedir}/src/main/resources/templates</dest>
        <includes>
            <include>**/*.jsp</include>
        </includes>
        <excludes>
            <exclude>**/fragments/**/*.jsp</exclude>
        </excludes>
    </configuration>
</plugin>
```

Exécution:
```bash
mvn jsp2tl:convert
```

### Utilisation via API Java

```java
// Créer et configurer la conversion
JSP2ThymeleafConfiguration config = new JSP2ThymeleafConfiguration();
config.setSrcFolder(Paths.get("/chemin/vers/jsp"));
config.setDestFolder(Paths.get("/chemin/vers/thymeleaf"));
config.setIncludes(new String[]{"**/*.jsp"});
config.setExcludes(new String[]{"**/fragments/**/*.jsp"});

// Exécuter la conversion
JSP2Thymeleaf converter = new JSP2Thymeleaf(config);
converter.run();

// Récupérer d'éventuelles erreurs
List<JSP2ThymeLeafException> exceptions = converter.getExceptions();
```

### Migration Progressive

Pour une migration en douceur:

1. Utilisez la bibliothèque companion `spring-thymeleaf-jsp` qui permet de faire cohabiter JSP et Thymeleaf
2. Convertissez d'abord les fragments/composants réutilisables
3. Testez après chaque conversion de page
4. Utilisez des commentaires HTML pour marquer les sections nécessitant une attention particulière

## Couverture des Conversions

### Balises JSP Standards

| Balise/Fonctionnalité | Statut | Équivalent Thymeleaf |
|-----------------------|--------|----------------------|
| Expressions `${...}`  | ✅     | `th:text="${...}"` ou insertion directe |
| Directives `<%@ %>`   | ✅     | Commentaires HTML ou namespace xmlns |
| Scriptlets `<% %>`    | ⚠️     | Commentaires HTML (à refactoriser manuellement) |
| Includes              | ✅     | `th:replace` ou `th:insert` |

### JSTL Core

| Tag | Statut | Équivalent Thymeleaf | Exemple |
|-----|--------|----------------------|---------|
| `c:forEach` | ✅ | `th:each` | `<c:forEach items="${users}" var="user">` → `<div th:each="user : ${users}">` |
| `c:if` | ✅ | `th:if` | `<c:if test="${condition}">` → `<div th:if="${condition}">` |
| `c:out` | ✅ | `th:text` | `<c:out value="${value}"/>` → `<span th:text="${value}"></span>` |
| `c:choose/when/otherwise` | ⚠️ | `th:if/th:unless/th:switch/th:case` | Conversion partielle nécessitant ajustements |
| `c:set` | ⚠️ | Variables contextuelles | Conversion manuelle généralement nécessaire |
| `c:url` | ⚠️ | `@{...}` | Conversion basique disponible |
| `c:catch` | ❌ | N/A | Non supporté actuellement |
| `c:forTokens` | ❌ | `th:each` + expression | Non supporté directement |
| `c:import` | ❌ | `th:insert` | Non supporté directement |
| `c:param` | ❌ | Paramètres d'URL | Non supporté actuellement |
| `c:redirect` | ❌ | N/A | Non applicable en Thymeleaf (logique côté contrôleur) |
| `c:remove` | ❌ | N/A | Non supporté actuellement |

### JSTL Formatting (fmt)

| Tag | Statut | Équivalent Thymeleaf |
|-----|--------|----------------------|
| `fmt:formatDate` | ⚠️ | `#temporals` |
| `fmt:formatNumber` | ⚠️ | `#numbers` |
| `fmt:message` | ⚠️ | `#{...}` |
| `fmt:bundle` | ❌ | Messages Thymeleaf |
| `fmt:param` | ❌ | Expression avec paramètres |
| Autres tags fmt | ❌ | Voir documentation Thymeleaf |

### JSTL Functions (fn)

Les fonctions JSTL sont généralement remplacées par les utilitaires de Thymeleaf ou des expressions standards.

| Fonction | Statut | Équivalent Thymeleaf |
|----------|--------|----------------------|
| `fn:length()` | ⚠️ | `#lists.size()` ou `#strings.length()` |
| `fn:contains()` | ⚠️ | `#strings.contains()` |
| `fn:toLowerCase()` | ⚠️ | `#strings.toLowerCase()` |
| `fn:toUpperCase()` | ⚠️ | `#strings.toUpperCase()` |
| Autres fonctions | ❌ | Voir documentation des utilitaires Thymeleaf |

### Spring Form Tags

| Tag | Statut | Équivalent Thymeleaf |
|-----|--------|----------------------|
| `form:form` | ✅ | `th:action, th:object` |
| `form:input` | ✅ | `th:field` |
| `form:select` | ✅ | `th:field` + `option` avec `th:each` |
| `form:checkbox` | ✅ | `th:field` |
| Autres tags form | ⚠️ | Conversion partielle disponible |

### Légende:
- ✅ Supporté complètement
- ⚠️ Supporté partiellement (peut nécessiter des ajustements manuels)
- ❌ Non supporté actuellement

## Exemples de Conversion

### Exemple 1: Boucle ForEach et Condition If

**JSP:**
```jsp
<c:if test="${not empty users}">
    <h2>Liste des utilisateurs</h2>
    <ul>
        <c:forEach items="${users}" var="user">
            <li>${user.name} (${user.email})</li>
        </c:forEach>
    </ul>
</c:if>
```

**Thymeleaf:**
```html
<div th:if="${not empty users}">
    <h2>Liste des utilisateurs</h2>
    <ul>
        <li th:each="user : ${users}">
            <span th:text="${user.name}">Nom</span> (<span th:text="${user.email}">email</span>)
        </li>
    </ul>
</div>
```

### Exemple 2: Formulaire Spring

**JSP:**
```jsp
<form:form method="post" action="/users/save" modelAttribute="user">
    <div class="form-group">
        <form:label path="name">Nom</form:label>
        <form:input path="name" class="form-control" />
        <form:errors path="name" class="text-danger" />
    </div>
    <div class="form-group">
        <form:label path="email">Email</form:label>
        <form:input path="email" class="form-control" />
        <form:errors path="email" class="text-danger" />
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
    <div class="form-group">
        <label for="email">Email</label>
        <input type="text" th:field="*{email}" class="form-control" />
        <span class="text-danger" th:if="${#fields.hasErrors('email')}" th:errors="*{email}">Erreur</span>
    </div>
    <button type="submit">Enregistrer</button>
</form>
```
| No  | sql:transaction           |
| No  | sql:update                |
| No  | x:choose                  |
| No  | x:forEach                 |
| No  | x:if                      |
| No  | x:otherwise               |
| No  | x:out                     |
| No  | x:param                   |
| No  | x:parse                   |
| No  | x:set                     |
| No  | x:transform               |
| No  | x:when                    |
+-----+---------------------------+