# Guide d'Utilisation et Exemples du Plugin JSP2TL Maven

Ce document fournit des exemples détaillés d'utilisation du plugin jsp2tl-maven-plugin pour différents scénarios de migration de JSP vers Thymeleaf.

## 1. Configuration de Base

### Exemple Minimal

La configuration minimale pour convertir des fichiers JSP en templates Thymeleaf:

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <executions>
        <execution>
            <id>convert-jsps</id>
            <goals>
                <goal>convert</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Cette configuration utilisera les valeurs par défaut:
- Source: `${project.basedir}/src/main/webapp/WEB-INF/jsp`
- Destination: `${project.build.directory}/classes/templates`
- Inclusions: `**/*.jsp`, `**/*.jspx`, `**/*.jspf`

### Configuration Personnalisée

Configuration avec des paramètres personnalisés:

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <srcDirectory>${basedir}/src/main/webapp/views</srcDirectory>
        <outputDirectory>${basedir}/src/main/resources/templates</outputDirectory>
        <includes>
            <include>**/*.jsp</include>
        </includes>
        <excludes>
            <exclude>**/admin/*.jsp</exclude>
            <exclude>**/legacy/*.jsp</exclude>
        </excludes>
        <updateLinks>true</updateLinks>
    </configuration>
    <executions>
        <execution>
            <id>convert-jsps</id>
            <goals>
                <goal>convert</goal>
            </goals>
            <phase>generate-resources</phase>
        </execution>
    </executions>
</plugin>
```

## 2. Scénarios d'Utilisation Avancés

### Migration Progressive

Configuration pour une migration progressive avec des patterns d'inclusion spécifiques:

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <srcDirectory>${basedir}/src/main/webapp/WEB-INF/jsp</srcDirectory>
        <outputDirectory>${basedir}/src/main/resources/templates</outputDirectory>
        <includes>
            <!-- Migrer uniquement les pages publiques dans la phase 1 -->
            <include>**/public/*.jsp</include>
            <include>**/shared/*.jsp</include>
        </includes>
    </configuration>
    <executions>
        <execution>
            <id>convert-phase1</id>
            <goals>
                <goal>convert</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Utilisation de Convertisseurs Personnalisés

Configuration avec des packages de convertisseurs personnalisés:

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <srcDirectory>${basedir}/src/main/webapp/WEB-INF/jsp</srcDirectory>
        <outputDirectory>${basedir}/src/main/resources/templates</outputDirectory>
        <taglibConverterPackages>
            com.cybernostics.jsp2thymeleaf.conveters.tld,
            com.example.myapp.converters
        </taglibConverterPackages>
    </configuration>
    <executions>
        <execution>
            <id>convert-jsps</id>
            <goals>
                <goal>convert</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Génération de Convertisseur de Taglib

Utilisation du goal `taglib` pour générer un projet de convertisseur pour une TLD personnalisée:

```xml
<plugin>
    <groupId>com.cybernostics</groupId>
    <artifactId>jsp2tl-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <taglib>com/example/tags/my-taglib.tld</taglib>
        <taglibContainer>com.example:custom-taglib:1.0.0</taglibContainer>
    </configuration>
    <executions>
        <execution>
            <id>generate-taglib-converter</id>
            <goals>
                <goal>taglib</goal>
            </goals>
            <phase>generate-sources</phase>
        </execution>
    </executions>
</plugin>
```

## 3. Intégration avec d'Autres Plugins

### Avec maven-resources-plugin

```xml
<plugins>
    <!-- Convertit les fichiers JSP en templates Thymeleaf -->
    <plugin>
        <groupId>com.cybernostics</groupId>
        <artifactId>jsp2tl-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <srcDirectory>${basedir}/src/main/webapp/WEB-INF/jsp</srcDirectory>
            <outputDirectory>${project.build.directory}/thymeleaf-templates</outputDirectory>
        </configuration>
        <executions>
            <execution>
                <id>convert-jsps</id>
                <goals>
                    <goal>convert</goal>
                </goals>
                <phase>generate-resources</phase>
            </execution>
        </executions>
    </plugin>
    
    <!-- Copie les templates générés vers le répertoire final -->
    <plugin>
        <artifactId>maven-resources-plugin</artifactId>
        <version>3.2.0</version>
        <executions>
            <execution>
                <id>copy-thymeleaf-templates</id>
                <phase>process-resources</phase>
                <goals>
                    <goal>copy-resources</goal>
                </goals>
                <configuration>
                    <outputDirectory>${basedir}/src/main/resources/templates</outputDirectory>
                    <resources>
                        <resource>
                            <directory>${project.build.directory}/thymeleaf-templates</directory>
                            <filtering>false</filtering>
                        </resource>
                    </resources>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

### Avec Spring Boot

```xml
<plugins>
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>2.5.0</version>
    </plugin>
    
    <plugin>
        <groupId>com.cybernostics</groupId>
        <artifactId>jsp2tl-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <srcDirectory>${basedir}/src/main/webapp/WEB-INF/jsp</srcDirectory>
            <outputDirectory>${basedir}/src/main/resources/templates</outputDirectory>
        </configuration>
        <executions>
            <execution>
                <id>convert-jsps</id>
                <goals>
                    <goal>convert</goal>
                </goals>
                <phase>generate-resources</phase>
            </execution>
        </executions>
    </plugin>
</plugins>
```

## 4. Exécution en Ligne de Commande

### Conversion Standard

```bash
mvn jsp2tl:convert
```

### Conversion avec des Propriétés Spécifiques

```bash
mvn jsp2tl:convert -DsrcDirectory=src/main/views -DoutputDirectory=src/main/resources/templates
```

### Génération de Convertisseur de Taglib

```bash
mvn jsp2tl:taglib -Dtaglib=path/to/custom.tld
```

### Génération de Convertisseur depuis un JAR

```bash
mvn jsp2tl:taglib -Dtaglib=custom.tld -DtaglibContainer=com.example:custom-tags:1.0.0
```

## 5. Guide de Dépannage

### Problème: Fichiers Non Trouvés

**Symptôme:** Message d'erreur indiquant qu'aucun fichier JSP n'a été trouvé.

**Solutions:**
- Vérifier que le chemin source est correct (`srcDirectory`)
- Vérifier que les patterns d'inclusion correspondent aux fichiers JSP
- Utiliser des patterns d'inclusion plus larges si nécessaire (`**/*.jsp`)
- Vérifier les patterns d'exclusion qui pourraient filtrer tous les fichiers

### Problème: Erreurs de Conversion

**Symptôme:** Erreurs pendant la conversion indiquant que certains éléments JSP ne peuvent pas être convertis.

**Solutions:**
- Vérifier que tous les convertisseurs de taglibs nécessaires sont disponibles
- Ajouter des convertisseurs personnalisés pour les balises non standard
- Simplifier temporairement les JSP complexes avant la conversion
- Essayer de convertir les fichiers un par un pour identifier les problématiques

### Problème: Dépendances Manquantes

**Symptôme:** Erreur ClassNotFoundException ou NoClassDefFoundError lors de l'exécution.

**Solutions:**
- Vérifier que toutes les dépendances requises sont déclarées dans le pom.xml
- S'assurer que les versions des dépendances sont compatibles
- Ajouter explicitement les dépendances de transitivité manquantes

### Problème: Liens Non Mis à Jour

**Symptôme:** Les liens vers d'autres JSP ne sont pas correctement convertis en liens vers des templates Thymeleaf.

**Solutions:**
- Vérifier que `updateLinks` est défini à `true`
- S'assurer que les liens utilisent des chemins relatifs cohérents
- Vérifier que la structure des répertoires source et destination est compatible

## 6. Meilleures Pratiques

1. **Migration progressive**: Commencer par les pages simples et augmenter progressivement la complexité.
2. **Contrôle de version**: Effectuer la conversion dans une branche séparée et vérifier les changements.
3. **Tests automatisés**: Mettre en place des tests avant la conversion pour valider après.
4. **Configuration modulaire**: Diviser la configuration en plusieurs exécutions pour les différentes parties de l'application.
5. **Validation manuelle**: Toujours vérifier visuellement les templates générés pour s'assurer de leur qualité.
