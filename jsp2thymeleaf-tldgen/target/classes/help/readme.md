# Module JSP2Thymeleaf-TLDGen

## Description et Objectif

Le module `jsp2thymeleaf-tldgen` est un générateur de convertisseurs qui crée automatiquement des squelettes de convertisseurs pour des bibliothèques de balises JSP (Tag Library Descriptor - TLD) personnalisées. Cet outil permet d'accélérer le processus de conversion en générant le code de base nécessaire pour transformer des bibliothèques de balises JSP spécifiques en équivalents Thymeleaf.

## Fonctionnalités Principales

1. **Génération Automatique de Convertisseurs**
   * Crée un projet Maven complet pour héberger les convertisseurs
   * Génère un convertisseur de base pour chaque balise et fonction de la TLD
   * Produit la structure de code nécessaire pour l'enregistrement des convertisseurs

2. **Support pour Différents Types de Tags**
   * Génération de convertisseurs pour les balises éléments (Element Tags)
   * Génération de convertisseurs pour les fonctions (Function Tags)
   * Support pour les attributs et les paramètres

3. **Cadre Extensible**
   * Code généré facilement personnalisable
   * Structure basée sur les interfaces standard de jsp2thymeleaf-api
   * Templates Velocity permettant une personnalisation profonde

## Documentation Détaillée

Pour une documentation complète du module, veuillez consulter le fichier [DOCUMENTATION.md](DOCUMENTATION.md), qui contient:

- Architecture détaillée du module
- Guide d'utilisation avec exemples
- Description du processus de génération
- Structure et composants du code généré
- Intégration avec d'autres modules du toolkit

## Types de Convertisseurs Générés

### 1. Convertisseurs de Fonctions

Les convertisseurs de fonctions permettent de transformer les appels de fonctions JSP en expressions Thymeleaf équivalentes. Ils permettent notamment de:
* Changer le nom de la méthode
* Réordonner ou combiner les arguments
* Modifier la syntaxe de l'expression

**Exemple:**
```
mytaglib:someFunctionName(arg1,arg2,arg3)  ---> th:newFunctionName(arg3,arg1,arg2)
```

### 2. Convertisseurs d'Éléments

Les convertisseurs d'éléments transforment les balises JSP en éléments Thymeleaf équivalents. Ils permettent de:
* Mapper le nom de la balise vers un élément HTML approprié
* Transformer les attributs JSP en attributs Thymeleaf
* Gérer le contenu et la structure de l'élément

**Exemple:**
```
<c:out value="Hello there"/>   ------->   <span th:text="Hello there"/>
```

## Guide d'Utilisation

### Syntaxe de Base

```bash
java -jar jsp2thymeleaf-tldgen.jar [options] [chemin_vers_tld]
```

## Guide d'Utilisation Rapide

### Prérequis

- Java 8 ou supérieur
- Maven 3.x

### Utilisation en Ligne de Commande

```bash
java -jar jsp2thymeleaf-tldgen.jar --tld=[chemin_vers_fichier_tld] --package=com.example.converters --output=[dossier_sortie]
```

### Options Disponibles

| Option | Description | Obligatoire |
|--------|-------------|-------------|
| `--tld` | Chemin vers le fichier TLD à analyser | Oui |
| `--package` | Package Java pour les classes générées | Oui |
| `--output` | Dossier où sera généré le projet | Oui |
| `--gav` | Coordonnées Maven pour un JAR contenant la TLD (format: groupe:artefact:version) | Non |

### Exemple d'Utilisation

```bash
# Génération à partir d'un fichier TLD local
java -jar jsp2thymeleaf-tldgen.jar --tld=c:/tlds/mytags.tld --package=com.example.mytagsconverters --output=c:/projects/mytags-converters

# Génération à partir d'une TLD dans une dépendance Maven
java -jar jsp2thymeleaf-tldgen.jar --gav=org.example:custom-tags:1.0.0 --tldPath=META-INF/mytags.tld --package=com.example.converters --output=./output
```

## Projet Généré

Le module génère un projet Maven avec la structure suivante:

```
jsp2thymeleaf-converters-[nom_taglib]/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── [package]/
│   │           └── [nom_taglib]TldConverterRegistration.java
│   └── test/
│       └── java/
│           └── [package]/
│               └── [nom_taglib]TldConverterHappyCaseTest.java
└── pom.xml
```

## Intégration avec JSP2Thymeleaf

Pour utiliser le convertisseur généré:

1. Complétez l'implémentation des convertisseurs dans la classe d'enregistrement
2. Compilez et empaquetez le projet (`mvn package`)
3. Ajoutez le JAR généré aux dépendances de votre projet JSP2Thymeleaf
4. Le convertisseur sera automatiquement détecté et utilisé lors de la conversion
