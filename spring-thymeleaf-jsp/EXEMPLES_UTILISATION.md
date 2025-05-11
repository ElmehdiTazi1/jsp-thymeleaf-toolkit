# Guide d'Utilisation du Module spring-thymeleaf-jsp

Ce guide présente des exemples concrets d'utilisation du module `spring-thymeleaf-jsp` pour faciliter la migration progressive d'applications Spring de JSP vers Thymeleaf.

## Table des Matières

1. [Configuration de Base](#1-configuration-de-base)
2. [Insertion de Fragments Thymeleaf dans des Pages JSP](#2-insertion-de-fragments-thymeleaf-dans-des-pages-jsp)
3. [Migration Progressive](#3-migration-progressive)
4. [Partage de Variables entre JSP et Thymeleaf](#4-partage-de-variables-entre-jsp-et-thymeleaf)
5. [Gestion des Formulaires](#5-gestion-des-formulaires)
6. [Résolution des Problèmes Courants](#6-résolution-des-problèmes-courants)
7. [Bonnes Pratiques](#7-bonnes-pratiques)

## 1. Configuration de Base

### 1.1. Dépendances Maven

Ajoutez les dépendances nécessaires à votre fichier `pom.xml` :

```xml
<!-- Spring Boot Starter Web pour MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Support JSP -->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
</dependency>

<!-- Thymeleaf -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Module spring-thymeleaf-jsp -->
<dependency>
    <groupId>com.cybernostics</groupId>
    <artifactId>spring-thymeleaf-jsp</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 1.2. Configuration Application

Configurez votre fichier `application.properties` :

```properties
# Configuration JSP
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp

# Configuration Thymeleaf
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false
```

### 1.3. Structure de Projet Recommandée

```
src/
├── main/
│   ├── java/
│   │   └── com/example/application/
│   │       ├── Application.java
│   │       ├── config/
│   │       │   └── WebConfig.java (optionnel)
│   │       └── controller/
│   │           └── HomeController.java
│   ├── resources/
│   │   ├── templates/
│   │   │   ├── fragments/
│   │   │   │   ├── header.html
│   │   │   │   └── footer.html
│   │   │   └── views/
│   │   │       └── home.html
│   │   └── application.properties
│   └── webapp/
│       └── WEB-INF/
│           └── jsp/
│               ├── fragments/
│               │   └── navigation.jsp
│               └── views/
│                   └── home.jsp
```

## 2. Insertion de Fragments Thymeleaf dans des Pages JSP

### 2.1. Création d'un Fragment Thymeleaf

Créez un fragment Thymeleaf dans `src/main/resources/templates/fragments/header.html` :

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
</head>
<body>
    <header th:fragment="main">
        <div class="header-container">
            <h1>Application Demo</h1>
            <nav>
                <ul>
                    <li><a href="/" th:href="@{/}">Accueil</a></li>
                    <li><a href="/about" th:href="@{/about}">À propos</a></li>
                    <li><a href="/contact" th:href="@{/contact}">Contact</a></li>
                </ul>
            </nav>
        </div>
    </header>
</body>
</html>
```

### 2.2. Utilisation du Fragment dans une Page JSP

Créez une page JSP qui utilise le fragment Thymeleaf dans `src/main/webapp/WEB-INF/jsp/views/about.jsp` :

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="th" uri="/insertthymeleaf" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>À propos</title>
    <link rel="stylesheet" href="<c:url value='/css/styles.css'/>">
</head>
<body>
    <!-- Insérer l'en-tête Thymeleaf -->
    <th:fragment name="fragments/header" fragment="main" />
    
    <div class="container">
        <h2>À propos de nous</h2>
        <p>Cette page utilise un en-tête créé en Thymeleaf mais est elle-même une page JSP.</p>
        <p>C'est un exemple de migration progressive où les composants sont migrés un par un.</p>
    </div>
    
    <%-- Pied de page en JSP traditionnel --%>
    <footer>
        <p>&copy; 2025 Exemple de Migration JSP vers Thymeleaf</p>
    </footer>
</body>
</html>
```

### 2.3. Contrôleur pour la Page

```java
@Controller
public class AboutController {
    
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "À propos");
        return "views/about";  // Fonctionne avec JSP ou Thymeleaf
    }
}
```

## 3. Migration Progressive

### 3.1. Stratégie de Migration par Composants

1. **Étape 1 : Migrer les Fragments Réutilisables**

   Convertissez d'abord les éléments communs comme l'en-tête et le pied de page en fragments Thymeleaf :
   
   ```html
   <!-- src/main/resources/templates/fragments/footer.html -->
   <!DOCTYPE html>
   <html xmlns:th="http://www.thymeleaf.org">
   <body>
       <footer th:fragment="main">
           <div class="footer-container">
               <p>&copy; 2025 Notre Application</p>
               <ul class="footer-links">
                   <li><a href="/terms" th:href="@{/terms}">Conditions d'utilisation</a></li>
                   <li><a href="/privacy" th:href="@{/privacy}">Politique de confidentialité</a></li>
               </ul>
           </div>
       </footer>
   </body>
   </html>
   ```

2. **Étape 2 : Intégrer les Fragments dans les Pages JSP Existantes**

   Utilisez le tag `<th:fragment>` pour intégrer ces fragments dans vos JSP :
   
   ```jsp
   <%@ page contentType="text/html;charset=UTF-8" language="java" %>
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
   <%@ taglib prefix="th" uri="/insertthymeleaf" %>
   <!DOCTYPE html>
   <html>
   <head>
       <title>Page Produit</title>
   </head>
   <body>
       <th:fragment name="fragments/header" fragment="main" />
       
       <!-- Contenu JSP existant -->
       <div class="product-details">
           <h2>${product.name}</h2>
           <p>${product.description}</p>
           <p class="price">Prix: ${product.price} €</p>
       </div>
       
       <th:fragment name="fragments/footer" fragment="main" />
   </body>
   </html>
   ```

3. **Étape 3 : Migrer des Pages Complètes**

   Créez des équivalents Thymeleaf pour vos pages JSP, en commençant par les plus simples :
   
   ```html
   <!-- src/main/resources/templates/views/contact.html -->
   <!DOCTYPE html>
   <html xmlns:th="http://www.thymeleaf.org">
   <head>
       <meta charset="UTF-8">
       <title>Contact</title>
       <link rel="stylesheet" th:href="@{/css/styles.css}">
   </head>
   <body>
       <div th:replace="fragments/header :: main"></div>
       
       <div class="container">
           <h2>Contactez-nous</h2>
           <form th:action="@{/contact/submit}" method="post">
               <div class="form-group">
                   <label for="name">Nom :</label>
                   <input type="text" id="name" name="name" required>
               </div>
               <div class="form-group">
                   <label for="email">Email :</label>
                   <input type="email" id="email" name="email" required>
               </div>
               <div class="form-group">
                   <label for="message">Message :</label>
                   <textarea id="message" name="message" rows="5" required></textarea>
               </div>
               <button type="submit">Envoyer</button>
           </form>
       </div>
       
       <div th:replace="fragments/footer :: main"></div>
   </body>
   </html>
   ```

### 3.2. Résolution Automatique des Vues

Grâce au `ThymeleafJSPViewResolver`, le contrôleur n'a pas besoin de changer lors de la migration :

```java
@Controller
public class ProductController {
    
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getById(id);
        model.addAttribute("product", product);
        
        // Cette vue peut être soit une JSP, soit un template Thymeleaf
        // Le résolveur choisira automatiquement la version Thymeleaf si elle existe
        return "views/product";
    }
}
```

## 4. Partage de Variables entre JSP et Thymeleaf

### 4.1. Accès aux Variables du Modèle

Les variables ajoutées au modèle Spring sont accessibles dans les deux environnements :

```java
@Controller
public class UserController {
    
    @GetMapping("/user/profile")
    public String userProfile(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("lastLogin", user.getLastLoginDate());
        model.addAttribute("activities", userService.getRecentActivities(user.getId()));
        
        return "views/user/profile";  // Fonctionne avec JSP ou Thymeleaf
    }
}
```

### 4.2. Partage de Variables de Session

Les attributs de session sont également accessibles :

```java
// Dans un contrôleur
session.setAttribute("theme", userPreferences.getTheme());
```

Accès dans JSP :
```jsp
<div class="content ${sessionScope.theme}">
    <!-- Contenu -->
</div>
```

Accès dans Thymeleaf :
```html
<div class="content" th:classappend="${session.theme}">
    <!-- Contenu -->
</div>
```

## 5. Gestion des Formulaires

### 5.1. Formulaire en JSP

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Inscription</title>
</head>
<body>
    <h2>Créer un compte</h2>
    <form:form modelAttribute="registrationForm" action="/register" method="post">
        <div>
            <form:label path="username">Nom d'utilisateur</form:label>
            <form:input path="username" />
            <form:errors path="username" cssClass="error" />
        </div>
        <div>
            <form:label path="email">Email</form:label>
            <form:input path="email" type="email" />
            <form:errors path="email" cssClass="error" />
        </div>
        <div>
            <form:label path="password">Mot de passe</form:label>
            <form:password path="password" />
            <form:errors path="password" cssClass="error" />
        </div>
        <button type="submit">S'inscrire</button>
    </form:form>
</body>
</html>
```

### 5.2. Équivalent en Thymeleaf

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Inscription</title>
</head>
<body>
    <h2>Créer un compte</h2>
    <form th:action="@{/register}" th:object="${registrationForm}" method="post">
        <div>
            <label for="username">Nom d'utilisateur</label>
            <input type="text" th:field="*{username}" />
            <span class="error" th:if="${#fields.hasErrors('username')}" th:errors="*{username}"></span>
        </div>
        <div>
            <label for="email">Email</label>
            <input type="email" th:field="*{email}" />
            <span class="error" th:if="${#fields.hasErrors('email')}" th:errors="*{email}"></span>
        </div>
        <div>
            <label for="password">Mot de passe</label>
            <input type="password" th:field="*{password}" />
            <span class="error" th:if="${#fields.hasErrors('password')}" th:errors="*{password}"></span>
        </div>
        <button type="submit">S'inscrire</button>
    </form>
</body>
</html>
```

## 6. Résolution des Problèmes Courants

### 6.1. Problème: Les Fragments Thymeleaf Ne S'Affichent Pas dans JSP

**Symptômes :**  
Le tag `<th:fragment>` n'affiche rien ou génère une erreur.

**Solutions :**
1. Vérifiez que la taglib est correctement déclarée :
   ```jsp
   <%@ taglib prefix="th" uri="/insertthymeleaf" %>
   ```

2. Assurez-vous que le chemin du fragment est correct (relatif au préfixe Thymeleaf) :
   ```jsp
   <!-- Correct -->
   <th:fragment name="fragments/header" fragment="main" />
   
   <!-- Incorrect -->
   <th:fragment name="/fragments/header" fragment="main" />
   ```

3. Vérifiez que le dialecte Thymeleaf personnalisé est correctement configuré :
   ```java
   @Bean
   public SpringTemplateEngine templateEngine() {
       SpringTemplateEngine engine = new SpringTemplateEngine();
       engine.setDialect(new SpringStandardDialectWithJSPBehaviours());
       // Configuration supplémentaire...
       return engine;
   }
   ```

### 6.2. Problème: Conflit de Résolution de Vues

**Symptômes :**  
La mauvaise vue est sélectionnée (JSP au lieu de Thymeleaf ou vice versa).

**Solutions :**
1. Vérifiez l'ordre des résolveurs de vues dans votre application :
   ```java
   @Autowired
   private ThymeleafJSPViewResolver thymeleafJspViewResolver;
   
   @Autowired
   private ThymeleafViewResolver thymeleafViewResolver;
   
   @Autowired
   private InternalResourceViewResolver jspViewResolver;
   
   @PostConstruct
   public void configureViewResolvers() {
       // ThymeleafJSPViewResolver doit avoir la priorité la plus haute
       thymeleafJspViewResolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
       
       // Les autres résolveurs doivent avoir une priorité plus basse
       thymeleafViewResolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
       jspViewResolver.setOrder(Ordered.LOWEST_PRECEDENCE);
   }
   ```

2. Assurez-vous que les préfixes et suffixes sont correctement configurés :
   ```properties
   spring.mvc.view.prefix=/WEB-INF/jsp/
   spring.mvc.view.suffix=.jsp
   spring.thymeleaf.prefix=classpath:/templates/
   spring.thymeleaf.suffix=.html
   ```

### 6.3. Problème: Variables Non Accessibles Entre JSP et Thymeleaf

**Symptômes :**  
Les variables définies dans un contexte ne sont pas disponibles dans l'autre.

**Solutions :**
1. Utilisez exclusivement le Model Spring pour passer des variables :
   ```java
   @GetMapping("/example")
   public String example(Model model) {
       model.addAttribute("message", "Hello World");
       return "example";
   }
   ```

2. Pour les variables de session, utilisez `HttpSession` plutôt que les attributs de page :
   ```java
   @GetMapping("/setPreference")
   public String setPreference(HttpSession session) {
       session.setAttribute("preference", "darkMode");
       return "redirect:/dashboard";
   }
   ```

## 7. Bonnes Pratiques

### 7.1. Organisation des Templates

- **Structure Parallèle** : Maintenez une structure parallèle entre vos JSP et vos templates Thymeleaf pour faciliter la migration
  ```
  webapp/WEB-INF/jsp/views/user/profile.jsp
  resources/templates/views/user/profile.html
  ```

- **Fragments Communs** : Extrayez les éléments réutilisables en fragments Thymeleaf dès le début
  ```
  resources/templates/fragments/
  ├── header.html
  ├── footer.html
  ├── navigation.html
  └── forms/
      ├── address-form.html
      └── payment-form.html
  ```

### 7.2. Stratégie de Migration

1. **Migration Bottom-Up** : Commencez par les composants de base (fragments) avant les pages complètes
2. **Migration Fonctionnelle** : Migrez les fonctionnalités complètes plutôt que des pages isolées
3. **Test Parallèle** : Gardez les deux versions (JSP et Thymeleaf) pendant la transition pour comparer et vérifier

### 7.3. Tests et Validation

- Créez des tests automatisés pour vérifier que le rendu est cohérent entre JSP et Thymeleaf
- Utilisez des outils de comparaison visuelle pour identifier les différences de rendu
- Testez les performances des deux approches pour identifier les optimisations possibles

### 7.4. Documentation

- Documentez les conventions de nommage et la structure des templates
- Créez un guide de migration pour l'équipe
- Maintenez une liste des éléments migrés et restants
- Documentez les différences de comportement entre JSP et Thymeleaf

## Conclusion

Le module `spring-thymeleaf-jsp` offre une solution élégante pour faciliter la migration progressive de JSP vers Thymeleaf dans les applications Spring. En suivant les exemples et bonnes pratiques présentés dans ce guide, vous pouvez réduire considérablement les risques et les efforts associés à cette transition technologique, tout en préservant la fonctionnalité de votre application pendant le processus.
