# Documentation du Module jsp2tl-maven-plugin

## 1. Vue d'Ensemble

Le module `jsp2tl-maven-plugin` est un plugin Maven qui intègre le processus de conversion JSP vers Thymeleaf dans le cycle de vie de build Maven. Son objectif principal est d'automatiser la migration des fichiers JSP vers des templates Thymeleaf lors du processus de build, facilitant ainsi la transition progressive d'une application de JSP vers Thymeleaf.

## 2. Architecture et Composants

### 2.1. Structure du Module

```
jsp2tl-maven-plugin/
├── src/
│   └── main/
│       └── java/
│           └── com/cybernostics/jsp2thymeleaf/maven/
│               ├── JSPConvertMojo.java     # Mojo principal pour la conversion de fichiers JSP
│               └── JSPTaglibMojo.java      # Mojo pour la génération de convertisseurs de taglibs
├── pom.xml                                 # Configuration Maven du plugin
└── README.md                               # Documentation du module
```

### 2.2. Composants Principaux

#### 2.2.1. JSPConvertMojo

Cette classe est le composant principal du plugin, responsable de la conversion des fichiers JSP en templates Thymeleaf. Elle est annotée avec `@Mojo(name = "convert")` et est conçue pour s'exécuter pendant la phase `PROCESS_SOURCES` du cycle de vie Maven.

Principales fonctionnalités:
- Analyse des fichiers JSP source selon des patterns d'inclusion/exclusion
- Conversion des fichiers JSP en templates Thymeleaf équivalents
- Génération des fichiers convertis dans un répertoire de destination
- Configuration des convertisseurs de taglibs à utiliser

#### 2.2.2. JSPTaglibMojo

Ce composant est responsable de la génération de projets de convertisseurs pour des bibliothèques de balises JSP (TLD) personnalisées. Il est annotée avec `@Mojo(name = "taglib")` et s'intègre avec le module `jsp2thymeleaf-tldgen`.

Principales fonctionnalités:
- Génération de projets de convertisseurs pour des TLD spécifiques
- Support pour les TLD contenues dans des fichiers JAR
- Création de la structure de projet Maven pour le convertisseur

## 3. Paramètres de Configuration

### 3.1. Paramètres de JSPConvertMojo

| Paramètre | Description | Valeur par défaut |
|-----------|-------------|-------------------|
| `outputDirectory` | Répertoire de destination pour les templates Thymeleaf générés | `${project.build.directory}/classes/templates` |
| `updateLinks` | Indique si les liens vers d'autres JSP doivent être mis à jour | `true` |
| `srcDirectory` | Répertoire contenant les fichiers JSP source | `${project.basedir}/src/main/webapp/WEB-INF/jsp` |
| `converterScriptDirectory` | Répertoire contenant des scripts de conversion personnalisés | `${project.basedir}/src/main/jsp2thymeleaf` |
| `taglibConverterPackages` | Packages contenant les convertisseurs de taglibs | `com.cybernostics.jsp2thymeleaf.conveters.tld` |
| `includes` | Patterns pour sélectionner les fichiers à inclure | `**/*.jsp`, `**/*.jspx`, `**/*.jspf` |
| `excludes` | Patterns pour exclure certains fichiers | - |

### 3.2. Paramètres de JSPTaglibMojo

| Paramètre | Description | Obligatoire |
|-----------|-------------|-------------|
| `taglib` | Chemin vers le fichier TLD ou nom du fichier TLD dans le JAR | Oui |
| `taglibContainer` | JAR contenant la TLD (format: groupe:artefact:version) | Non |

## 4. Intégration avec Maven

### 4.1. Configuration dans le pom.xml

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.cybernostics</groupId>
            <artifactId>jsp2tl-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <configuration>
                <srcDirectory>${basedir}/src/main/webapp/WEB-INF/jsp</srcDirectory>
                <outputDirectory>${basedir}/src/main/resources/templates</outputDirectory>
                <includes>
                    <include>**/*.jsp</include>
                </includes>
                <excludes>
                    <exclude>**/exclude/*.jsp</exclude>
                </excludes>
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
</build>
```

### 4.2. Phases du Cycle de Vie

Le plugin JSP2TL s'intègre naturellement dans le cycle de vie Maven:

- La conversion JSP (`convert`) est par défaut liée à la phase `process-sources`
- La génération de convertisseurs de taglibs (`taglib`) peut être exécutée à la demande

### 4.3. Exécution Manuelle

```bash
# Conversion de fichiers JSP
mvn jsp2tl:convert

# Génération d'un projet de convertisseur pour une TLD
mvn jsp2tl:taglib -Dtaglib=path/to/taglib.tld

# Génération d'un projet de convertisseur pour une TLD dans un JAR
mvn jsp2tl:taglib -Dtaglib=taglib.tld -DtaglibContainer=group:artifact:version
```

## 5. Processus de Conversion

### 5.1. Étapes de la Conversion

1. **Identification des fichiers source**: Le plugin recherche les fichiers JSP correspondant aux patterns d'inclusion/exclusion.
2. **Parsing des fichiers JSP**: Chaque fichier JSP est analysé pour créer une représentation structurée.
3. **Application des convertisseurs**: Les convertisseurs appropriés sont appliqués à chaque élément JSP.
4. **Génération des templates Thymeleaf**: Le contenu converti est écrit dans les fichiers de destination.
5. **Mise à jour des références**: Si `updateLinks=true`, les liens vers d'autres JSP sont convertis en liens vers les templates Thymeleaf correspondants.

### 5.2. Gestion des Erreurs

Le plugin gère plusieurs types d'erreurs qui peuvent survenir pendant la conversion:

- **Erreurs de parsing**: Lorsqu'un fichier JSP est mal formé
- **Erreurs de conversion**: Lorsqu'un élément JSP ne peut pas être converti
- **Erreurs d'écriture**: Lorsqu'il y a des problèmes d'accès aux fichiers de destination

Ces erreurs sont rapportées dans les logs Maven avec différents niveaux de détail.

## 6. Intégration avec d'Autres Modules

Le plugin jsp2tl-maven-plugin s'intègre avec plusieurs autres modules du toolkit JSP-to-Thymeleaf:

1. **jsp2thymeleaf**: Module principal pour la conversion JSP vers Thymeleaf
2. **jsp2thymeleaf-tldgen**: Utilisé par JSPTaglibMojo pour générer des projets de convertisseurs de taglibs
3. **Convertisseurs spécifiques**: Utilisation de convertisseurs pour Spring, Spring Form, etc.

## 7. Cas d'Utilisation

### 7.1. Migration Progressive

Le plugin est particulièrement utile pour une migration progressive de JSP vers Thymeleaf:

1. Configurer le plugin pour convertir un sous-ensemble de pages JSP
2. Vérifier et ajuster les templates générés
3. Déployer l'application avec les deux types de vues
4. Progressivement élargir le champ des fichiers convertis

### 7.2. Migration Complète

Pour une migration complète:

1. Configurer le plugin pour convertir tous les fichiers JSP
2. Exécuter la conversion comme partie du processus de build
3. Vérifier et ajuster les templates générés
4. Reconfigurer l'application pour utiliser exclusivement Thymeleaf

### 7.3. Génération de Convertisseurs Personnalisés

Pour les bibliothèques de balises personnalisées:

1. Utiliser le goal `taglib` pour générer un projet de convertisseur
2. Implémenter les détails de conversion dans le projet généré
3. Compiler et déployer le convertisseur personnalisé
4. Configurer le plugin pour utiliser le convertisseur personnalisé

## 8. Bonnes Pratiques et Recommandations

1. **Validation manuelle**: Toujours vérifier les templates générés, car certaines constructions JSP complexes peuvent nécessiter des ajustements.
2. **Approche progressive**: Commencer par convertir les fichiers les plus simples avant de passer aux plus complexes.
3. **Tests de non-régression**: Mettre en place des tests pour vérifier que les pages converties fonctionnent comme attendu.
4. **Gestion de version**: Garder les fichiers JSP originaux sous contrôle de version jusqu'à ce que la migration soit complète et validée.
5. **Configuration personnalisée**: Utiliser les options de configuration pour adapter le processus de conversion à votre projet.

## 9. Limites et Contraintes

1. **Conversion parfaite impossible**: Certaines constructions JSP avancées peuvent nécessiter des ajustements manuels.
2. **Taglibs personnalisées**: Les bibliothèques de balises personnalisées nécessitent des convertisseurs spécifiques.
3. **Intégration JavaScript**: Les interactions JSP-JavaScript complexes peuvent nécessiter une attention particulière.
4. **Performance**: La conversion de grands projets peut être gourmande en ressources.

## 10. Pistes d'Amélioration

1. **Support de configuration avancée**: Ajouter plus d'options pour personnaliser le processus de conversion.
2. **Rapports de conversion**: Générer des rapports détaillés sur les éléments convertis et ceux nécessitant une attention.
3. **Mode interactif**: Proposer un mode interactif pour résoudre les ambiguïtés de conversion.
4. **Intégration IDE**: Développer des plugins pour IDE facilitant l'utilisation du convertisseur.
5. **Performances**: Optimiser le processus de conversion pour les grands projets.
