# Catalogue des Exemples JSP vers Thymeleaf

Ce document référence tous les exemples de conversion JSP vers Thymeleaf présents dans le module jsp2thymeleaf-sample-jsp.

## 1. Exemples JSTL Core

| Nom du Fragment | Fichier JSP | Fichier Thymeleaf | Description |
|----------------|------------|-----------------|-------------|
| c:out | [cout.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/cout.jsp) | [cout.html](src/main/resources/templates/includes/fragments/cout.html) | Affichage de variables avec échappement |
| c:url | [curl.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/curl.jsp) | [curl.html](src/main/resources/templates/includes/fragments/curl.html) | Génération d'URLs avec contexte |
| c:if | [cif.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/cif.jsp) | [cif.html](src/main/resources/templates/includes/fragments/cif.html) | Conditions simples |
| c:forEach (list) | [foreach_1.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/foreach_1.jsp) | [foreach_1.html](src/main/resources/templates/includes/fragments/foreach_1.html) | Boucle sur collection |
| c:forEach (map) | [foreach_2.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/foreach_2.jsp) | [foreach_2.html](src/main/resources/templates/includes/fragments/foreach_2.html) | Boucle sur map |
| c:forTokens | [fortokens.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/fortokens.jsp) | [fortokens.html](src/main/resources/templates/includes/fragments/fortokens.html) | Boucle sur tokens |
| c:choose | [choose.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/choose.jsp) | [choose.html](src/main/resources/templates/includes/fragments/choose.html) | Structure conditionnelle multiple |

## 2. Exemples Spring Form

| Élément | Fichier JSP | Fichier Thymeleaf | Description |
|--------|------------|-----------------|-------------|
| form:form | [springform.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/springform.jsp) | [springform.html](src/main/resources/templates/includes/fragments/springform.html) | Formulaire complet avec validation |
| form:input | [springform.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/springform.jsp) | [springform.html](src/main/resources/templates/includes/fragments/springform.html) | Champ de saisie texte |
| form:password | [springform.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/springform.jsp) | [springform.html](src/main/resources/templates/includes/fragments/springform.html) | Champ de saisie mot de passe |
| form:textarea | [springform.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/springform.jsp) | [springform.html](src/main/resources/templates/includes/fragments/springform.html) | Zone de texte multi-lignes |
| form:checkbox | [springform.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/springform.jsp) | [springform.html](src/main/resources/templates/includes/fragments/springform.html) | Case à cocher |
| form:radiobutton | [springform.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/springform.jsp) | [springform.html](src/main/resources/templates/includes/fragments/springform.html) | Bouton radio |
| form:select | [springform.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/springform.jsp) | [springform.html](src/main/resources/templates/includes/fragments/springform.html) | Liste déroulante |
| form:errors | [springform.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/springform.jsp) | [springform.html](src/main/resources/templates/includes/fragments/springform.html) | Affichage des erreurs de validation |

## 3. Fragments de Page

| Composant | Fichier JSP | Fichier Thymeleaf | Description |
|----------|------------|-----------------|-------------|
| Header | [header.jsp](src/main/webapp/WEB-INF/jsp/includes/header.jsp) | [header.html](src/main/resources/templates/includes/header.html) | En-tête de page avec navigation |
| Footer | [footer.jsp](src/main/webapp/WEB-INF/jsp/includes/footer.jsp) | [footer.html](src/main/resources/templates/includes/footer.html) | Pied de page |

## 4. Pages Complètes

| Page | Fichier JSP | Fichier Thymeleaf | Description |
|-----|------------|-----------------|-------------|
| Welcome | [welcome.jsp](src/main/webapp/WEB-INF/jsp/welcome.jsp) | [welcome.html](src/main/resources/templates/welcome.html) | Page principale avec tous les exemples |
| Welcome2 | N/A | [welcome2.html](src/main/resources/templates/welcome2.html) | Variante avec approche différente |
| Error | N/A | [error.html](src/main/resources/templates/error.html) | Page d'erreur personnalisée |

## 5. Cas d'Utilisation Spécifiques

### 5.1. Expressions Imbriquées

**JSP:**
```jsp
<input value="<c:out value="${message}"/>"/>
```

**Thymeleaf:**
```html
<input th:value="${message}"/>
```

### 5.2. Utilisation avec Spring Functions

**JSP:**
```jsp
<c:forEach var="item" items="${fn:split('red,green,blue',',')}" varStatus="status">
    This is item ${item}. Status is ${status.index}<br/>
</c:forEach>
```

**Thymeleaf:**
```html
<span th:each="item,status : ${#strings.split('red,green,blue',',')}">
    This is item <span th:text="${item}"></span>. Status is <span th:text="${status.index}"></span><br/>
</span>
```

### 5.3. Conditions avec Opérateurs Logiques

**JSP:**
```jsp
<c:if test="${not empty message && message.length() > 5}">
    Message is not empty and longer than 5 characters.
</c:if>
```

**Thymeleaf:**
```html
<span th:if="${not #strings.isEmpty(message) and #strings.length(message) > 5}">
    Message is not empty and longer than 5 characters.
</span>
```

## 6. Exemples de Modèles de Conception

Ce projet démontre également des modèles de conception pour l'organisation des vues:

### 6.1. Pattern d'Organisation des Fragments

Structure organisée des fragments pour faciliter la réutilisation:

```
templates/
├── includes/           # Fragments principaux
│   ├── fragments/      # Fragments spécifiques par fonctionnalité
│   │   ├── form/       # Fragments de formulaires
│   │   └── data/       # Fragments d'affichage de données
│   ├── header.html     # En-tête commun
│   └── footer.html     # Pied de page commun
└── views/              # Pages principales
```

### 6.2. Pattern de Composition de Page

**Thymeleaf:**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/head :: head(~{::title})">
    <title>Page Title</title>
</head>
<body>
    <header th:replace="fragments/header :: header"></header>
    
    <main th:fragment="content">
        <h1>Main Content</h1>
        <!-- Page-specific content -->
    </main>
    
    <footer th:replace="fragments/footer :: footer"></footer>
</body>
</html>
```

## 7. Tableau de Correspondance Complet

| Fonctionnalité JSP | Balise / Syntaxe JSP | Balise / Syntaxe Thymeleaf | Exemple de fichier |
|-------------------|---------------------|--------------------------|------------------|
| Affichage de variable | `${variable}` ou `<c:out value="${variable}"/>` | `th:text="${variable}"` | cout.jsp / cout.html |
| Lien URL | `<c:url value="/path"/>` | `th:href="@{/path}"` | curl.jsp / curl.html |
| Condition | `<c:if test="${condition}">...</c:if>` | `<div th:if="${condition}">...</div>` | cif.jsp / cif.html |
| Condition multiple | `<c:choose><c:when>...</c:when><c:otherwise>...</c:otherwise></c:choose>` | `<div th:switch="${expression}"><div th:case="...">...</div><div th:case="*">...</div></div>` | choose.jsp / choose.html |
| Boucle | `<c:forEach var="item" items="${items}">...</c:forEach>` | `<div th:each="item : ${items}">...</div>` | foreach_1.jsp / foreach_1.html |
| Inclusion | `<%@ include file="path.jsp" %>` | `<div th:replace="path :: fragment"></div>` | welcome.jsp / welcome.html |
| Formulaire | `<form:form modelAttribute="user">...</form:form>` | `<form th:object="${user}">...</form>` | springform.jsp / springform.html |
| Champ texte | `<form:input path="name"/>` | `<input th:field="*{name}"/>` | springform.jsp / springform.html |
| Bouton radio | `<form:radiobutton path="gender" value="M"/>` | `<input type="radio" th:field="*{gender}" value="M"/>` | springform.jsp / springform.html |
| Liste déroulante | `<form:select path="country" items="${countries}"/>` | `<select th:field="*{country}"><option th:each="c : ${countries}" th:value="${c.id}" th:text="${c.name}"></option></select>` | springform.jsp / springform.html |
```

**Thymeleaf:**
```html
<input th:value="${message}"/>
```

*Localisation: [cout.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/cout.jsp) et [cout.html](src/main/resources/templates/includes/fragments/cout.html)*

### 5.2. Manipulation de Collections

**JSP:**
```jsp
<c:forEach var="item" items="${items}" varStatus="status">
    ${status.index}: ${item}
</c:forEach>
```

**Thymeleaf:**
```html
<div th:each="item, status : ${items}">
    <span th:text="${status.index} + ': ' + ${item}">Index: Item</span>
</div>
```

*Localisation: [foreach_1.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/foreach_1.jsp) et [foreach_1.html](src/main/resources/templates/includes/fragments/foreach_1.html)*

### 5.3. URLs et Paramètres

**JSP:**
```jsp
<c:url value="/item" var="itemUrl">
    <c:param name="id" value="${item.id}" />
</c:url>
<a href="${itemUrl}">View Item</a>
```

**Thymeleaf:**
```html
<a th:href="@{/item(id=${item.id})}">View Item</a>
```

*Localisation: [curl.jsp](src/main/webapp/WEB-INF/jsp/includes/fragments/curl.jsp) et [curl.html](src/main/resources/templates/includes/fragments/curl.html)*

## 6. Exemples d'Intégration

### 6.1. Inclusion de Fragments

**JSP:**
```jsp
<%@ include file="includes/header.jsp" %>
```

**Thymeleaf:**
```html
<div th:replace="includes/header::content"></div>
```

*Localisation: [welcome.jsp](src/main/webapp/WEB-INF/jsp/welcome.jsp) et [welcome.html](src/main/resources/templates/welcome.html)*

### 6.2. Expressions Spring

**JSP:**
```jsp
<spring:message code="welcome.message" arguments="${userName}" />
```

**Thymeleaf:**
```html
<span th:text="#{welcome.message(${userName})}">Welcome message</span>
```

*Cette transformation est démontrée dans divers exemples du projet*

## 7. Comment Utiliser Ces Exemples

1. **Examiner les paires JSP-Thymeleaf**:  
   Comparer chaque fichier JSP avec son équivalent Thymeleaf pour comprendre les patterns de conversion.

2. **Analyser les structures complexes**:  
   Les exemples comme `choose.jsp`/`choose.html` et `springform.jsp`/`springform.html` illustrent comment gérer les cas complexes.

3. **Tester les deux versions**:  
   Exécuter l'application et comparer le rendu des pages JSP et Thymeleaf pour s'assurer de leur équivalence fonctionnelle.

4. **Apprendre par la pratique**:  
   Utiliser ces exemples comme référence lors de la conversion de vos propres pages JSP.
