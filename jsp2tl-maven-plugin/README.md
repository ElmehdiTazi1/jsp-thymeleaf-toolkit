# Module JSP2TL-Maven-Plugin

## Description et Objectif

Le module jsp2tl-maven-plugin est un plugin Maven qui intègre le convertisseur JSP vers Thymeleaf dans le cycle de vie de build Maven. Il permet d'automatiser le processus de conversion des fichiers JSP en templates Thymeleaf dans le cadre de projets Maven, facilitant ainsi la migration progressive des applications web JSP vers Thymeleaf.

## Fonctionnalités Principales

1. **Intégration avec Maven**
   * S'exécute comme une étape standard du cycle de vie Maven
   * Configuration via le pom.xml de votre projet

2. **Contrôle Fin de la Conversion**
   * Sélection précise des fichiers à convertir via patterns d'inclusion/exclusion
   * Configuration des dossiers source et destination

3. **Support des Convertisseurs Personnalisés**
   * Utilisation des convertisseurs standards et personnalisés
   * Configuration des URI de taglibs spécifiques

4. **Migration Progressive**
   * Conversion sélective de certains fichiers ou sections
   * Compatible avec l'approche hybride JSP/Thymeleaf

## Guide d'Utilisation

### Configuration de Base

Ajoutez la configuration suivante à votre fichier `pom.xml`:

```xml
<build>
    <plugins>
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
                    <exclude>**/excluded/*.jsp</exclude>
                </excludes>
            </configuration>
            <executions>
                <execution>
                    <id>convert-jsps</id>
                    <phase>generate-resources</phase>
                    <goals>
                        <goal>convert</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Options de Configuration

| Option | Description | Valeur par défaut |
|--------|-------------|-------------------|
| `src` | Dossier source des fichiers JSP | `${project.basedir}/src/main/webapp` |
| `dest` | Dossier destination pour les fichiers Thymeleaf | `${project.build.directory}/thymeleaf` |
| `includes` | Patterns d'inclusion (format Ant) | `**/*.jsp` |
| `excludes` | Patterns d'exclusion (format Ant) | - |
| `converterPackages` | Packages contenant des convertisseurs supplémentaires | - |
| `uriMappings` | Mappings d'URI personnalisés | - |
| `showBanner` | Affiche une bannière lors de l'exécution | `true` |

### Exécution

Pour exécuter le plugin manuellement:

```bash
mvn jsp2tl:convert
```

Le plugin s'exécutera également automatiquement lors de la phase configurée (par exemple `generate-resources`).

## Exemples d'Utilisation

### Exemple 1: Configuration de Base

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <src>${basedir}/src/main/webapp/WEB-INF/views</src>
        <dest>${basedir}/src/main/resources/templates</dest>
    </configuration>
</plugin>
```

### Exemple 2: Conversion Sélective

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <src>${basedir}/src/main/webapp/WEB-INF/views</src>
        <dest>${basedir}/src/main/resources/templates</dest>
        <includes>
            <include>**/user/*.jsp</include>
            <include>**/product/*.jsp</include>
        </includes>
        <excludes>
            <exclude>**/fragments/**/*.jsp</exclude>
        </excludes>
    </configuration>
</plugin>
```

### Exemple 3: Utilisation de Convertisseurs Personnalisés

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <src>${basedir}/src/main/webapp/WEB-INF/views</src>
        <dest>${basedir}/src/main/resources/templates</dest>
        <converterPackages>
            <package>com.mycompany.converters</package>
        </converterPackages>
        <uriMappings>
            <mapping>
                <uri>http://mycompany.com/mytags</uri>
                <prefix>my</prefix>
            </mapping>
        </uriMappings>
    </configuration>
</plugin>
```

## Intégration avec Spring Boot

Pour un projet Spring Boot, il est recommandé de:

1. Configurer le dossier de destination dans `src/main/resources/templates`
2. Ajouter les dépendances Spring Boot Thymeleaf nécessaires
3. Configurer la cohabitation JSP/Thymeleaf pendant la transition

```xml
<!-- Dépendances nécessaires -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>com.cybernostics</groupId>
        <artifactId>spring-thymeleaf-jsp</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Dépendances et Prérequis

* Java 8 ou supérieur
* Maven 3.3 ou supérieur
* Les modules jsp2thymeleaf, jsp-parser et jsp2thymeleaf-api
* Éventuellement, les modules de convertisseurs spécifiques (spring, spring-form, etc.)

## Limitations et Améliorations Futures

1. **Amélioration des Rapports**
   * Génération de rapports détaillés sur la conversion
   * Statistiques sur les éléments convertis/non convertis

2. **Support de l'Intégration Continue**
   * Meilleure intégration avec les outils de CI/CD
   * Options pour la validation des fichiers générés

3. **Mapping Personnalisé Avancé**
   * Interface utilisateur pour configurer les mappings
   * Assistant de conversion interactive
