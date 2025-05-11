# Documentation du module jsp2thymeleaf-sample-jsp

## 1. Introduction

Le module `jsp2thymeleaf-sample-jsp` est un projet de démonstration conçu pour illustrer le processus de migration d'une application Spring MVC utilisant des vues JSP vers des vues Thymeleaf. Ce module sert à la fois de cas de test réel pour le toolkit JSP-to-Thymeleaf et de guide pratique pour les développeurs qui entreprennent une migration similaire.

Le projet montre comment:
- Convertir des pages JSP en templates Thymeleaf
- Exécuter une application hybride avec les deux technologies côte à côte
- Utiliser les outils de conversion automatisés
- Traiter les cas particuliers et les balises JSP complexes

## 2. Structure du Module

```
jsp2thymeleaf-sample-jsp/
├── src/
│   ├── main/
│   │   ├── java/                      # Code source Java
│   │   │   └── com/cybernostics/jsp2thymeleaf/sampleapp/
│   │   │       ├── controllers/       # Contrôleurs Spring MVC
│   │   │       └── models/            # Modèles de données
│   │   ├── resources/
│   │   │   ├── templates/             # Templates Thymeleaf convertis
│   │   │   │   ├── includes/          # Fragments Thymeleaf
│   │   │   │   └── ...                # Pages principales
│   │   │   └── application.properties # Configuration Spring Boot
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           ├── jsp/               # Pages JSP originales
│   │           │   ├── includes/      # Fragments JSP
│   │           │   └── ...            # Pages principales
│   │           └── ...                # Configuration web
│   └── test/                          # Tests unitaires et d'intégration
├── pom.xml                            # Configuration Maven
└── readme.md                          # Documentation du projet
```

## 3. Analyse des Exemples de Conversion

Le module contient des exemples de conversion pour les cas d'usage les plus courants en JSP, organisés comme suit:

Le module démontre systématiquement les conversions suivantes:
1. Balises JSTL Core (`c:out`, `c:if`, `c:forEach`, etc.)
2. Balises Spring Form pour les formulaires web
3. Inclusion de fragments et composition de pages 
4. Expressions et fonctions JSP

### 3.1. Balises JSTL Core

Le projet comprend des exemples pour toutes les balises JSTL Core couramment utilisées:

#### 3.1.1. c:out
**JSP:**
```jsp
<c:out value="${message}"/>
```
**Thymeleaf:**
```html
<span th:text="${message}"></span>
```

#### 3.1.2. c:url
**JSP:**
```jsp
<c:url value="/resources/text.txt" var="url"/>
<a href="${url}">Click here</a>
```
**Thymeleaf:**
```html
<a th:href="@{/resources/text.txt}">Click here</a>
```

#### 3.1.3. c:if
**JSP:**
```jsp
<c:if test="${true}">
    This will be visible.
</c:if>
```
**Thymeleaf:**
```html
<span th:if="${true}">
    This will be visible.
</span>
```

#### 3.1.4. c:forEach
**JSP:**
```jsp
<c:forEach var="item" items="${items}" varStatus="status">
    Item ${item}, Status: ${status.index}
</c:forEach>
```
**Thymeleaf:**
```html
<span th:each="item,status : ${items}">
    Item <span th:text="${item}"></span>, Status: <span th:text="${status.index}"></span>
</span>
```

#### 3.1.5. c:choose/when/otherwise
**JSP:**
```jsp
<c:choose>
    <c:when test="${condition1}">Content if condition1 is true</c:when>
    <c:when test="${condition2}">Content if condition2 is true</c:when>
    <c:otherwise>Content if none of the conditions are true</c:otherwise>
</c:choose>
```
**Thymeleaf:**
```html
<div th:switch="${expression}">
    <span th:case="${condition1}">Content if condition1 is true</span>
    <span th:case="${condition2}">Content if condition2 is true</span>
    <span th:case="*">Content if none of the conditions are true</span>
</div>
```

### 3.2. Spring Form Tags

Le module démontre également la conversion des balises de formulaire Spring vers leurs équivalents Thymeleaf:

#### 3.2.1. form:form
**JSP:**
```jsp
<form:form method="POST" commandName="user" action="${post_url}">
    <!-- Form content -->
</form:form>
```
**Thymeleaf:**
```html
<form th:object="${user}" method="POST" th:action="@{${post_url}}">
    <!-- Form content -->
</form>
```

#### 3.2.2. form:input
**JSP:**
```jsp
<form:input path="name" class="form-control"/>
```
**Thymeleaf:**
```html
<input th:field="*{name}" class="form-control"/>
```

#### 3.2.3. form:radiobutton
**JSP:**
```jsp
<form:radiobutton path="gender" value="M" class="form-check-input"/> 
<form:label path="gender" class="form-check-label">M</form:label>
```
**Thymeleaf:**
```html
<input type="radio" th:field="*{gender}" value="M" class="form-check-input"/> 
<label th:for="${#ids.prev('gender')}" class="form-check-label">M</label>
```

### 3.3. Inclusion de Fragments

La conversion des directives d'inclusion est démontrée dans les templates principaux:

#### 3.3.1. JSP Include
**JSP:**
```jsp
<%@ include file="includes/header.jsp" %>
```
**Thymeleaf:**
```html
<div th:replace="includes/header::content" />
```

#### 3.3.2. Structure de Fragments

Les fragments Thymeleaf sont structurés différemment des includes JSP:

**JSP (header.jsp):**
```jsp
<nav class="navbar">
    <!-- Navigation content -->
</nav>
```

**Thymeleaf (header.html):**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <body>
    <div th:fragment="content" class="jumbotron marketing_header">
      <nav class="navbar">
        <!-- Navigation content -->
      </nav>
    </div>
  </body>
</html>
```

## 4. Cas d'Usage Complexes

### 4.1. Expressions Imbriquées

Le module démontre comment gérer les expressions JSP imbriquées:

**JSP:**
```jsp
<input value="<c:out value="${message}"/>"/>
```

**Thymeleaf:**
```html
<input th:value="${message}"/>
```

### 4.2. Expressions Conditionnelles Complexes

**JSP:**
```jsp
<c:if test="${not empty user && user.role == 'ADMIN'}">
    Admin Content
</c:if>
```

**Thymeleaf:**
```html
<div th:if="${not #lists.isEmpty(user) and user.role == 'ADMIN'}">Admin Content</div>
```

## 5. Stratégie de Migration

Le module illustre une stratégie de migration progressive:

1. **Coexistence JSP et Thymeleaf**: L'application peut exécuter à la fois des vues JSP et Thymeleaf
2. **Migration par fragments**: Les composants communs (header, footer) sont migrés en premier
3. **Conversion page par page**: Les pages sont converties une à une sans perturber l'application
4. **Validation fonctionnelle**: Chaque page convertie est testée pour assurer l'équivalence fonctionnelle

## 6. Configuration Technique

### 6.1. Configuration Spring Boot

Le projet utilise Spring Boot avec une configuration permettant d'utiliser à la fois JSP et Thymeleaf:

```java
@SpringBootApplication
public class SampleJspApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleJspApplication.class, args);
    }
}
```

### 6.2. ViewResolvers

L'application est configurée pour utiliser à la fois ThymeleafViewResolver et InternalResourceViewResolver:

```properties
# application.properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
```

## 7. Exemples de Conversion Complets

### 7.1. Page JSP Complète

Le module contient des exemples de conversion complète de pages, comme la page welcome.jsp qui est convertie en welcome.html, en maintenant toute la fonctionnalité et la structure.

### 7.2. Démonstration des Outils de Conversion

Le projet montre comment utiliser le toolkit JSP-to-Thymeleaf pour automatiser la conversion:

```bash
java -jar jsp2thymeleaf.jar -f WEB-INF/jsp/welcome.jsp -o src/main/resources/templates -u
```

## 8. Bonnes Pratiques Démontrées

### 8.1. Organisation du Code

- Séparation claire des vues JSP et Thymeleaf
- Structure de répertoires cohérente
- Fragments réutilisables

### 8.2. Patterns de Conversion

- Élimination des scriptlets JSP
- Utilisation des attributs Thymeleaf au lieu des balises JSP
- Expressions Thymeleaf idiomatiques

### 8.3. Tests

- Tests automatisés pour valider la conversion
- Vérification du rendu des vues

## 9. Limitations et Défis

Le module illustre également certains défis courants lors de la migration:

- Gestion des scriptlets JSP (code Java inline)
- Conversion des expressions EL complexes
- Différences dans le traitement du cycle de vie des vues

## 10. Guides d'Utilisation

### 10.1. Exécution de l'Application

```bash
mvn spring-boot:run
```

L'application sera accessible à l'adresse http://localhost:8080/

### 10.2. Conversion d'une Nouvelle Page

1. Identifier la page JSP à convertir
2. Exécuter l'outil de conversion JSP-to-Thymeleaf
3. Vérifier et ajuster le résultat
4. Mettre à jour les contrôleurs si nécessaire
5. Tester la page convertie

## Conclusion

Le module jsp2thymeleaf-sample-jsp constitue un exemple pratique et complet de migration d'une application JSP vers Thymeleaf. Il sert de démonstration des capacités du toolkit JSP-to-Thymeleaf et de guide pour les développeurs entreprenant une migration similaire. L'approche progressive et les nombreux exemples couvrant les cas d'usage courants en font une ressource précieuse pour comprendre les patterns de conversion JSP vers Thymeleaf.
