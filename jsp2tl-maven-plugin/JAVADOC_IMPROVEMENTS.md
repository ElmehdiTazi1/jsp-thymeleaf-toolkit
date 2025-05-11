# Proposition d'Amélioration de la JavaDoc pour JSPConvertMojo

Ce document présente des suggestions d'améliorations pour la documentation JavaDoc de la classe `JSPConvertMojo` dans le module `jsp2tl-maven-plugin`.

## Documentation de la Classe

```java
/**
 * Goal Maven qui convertit les fichiers JSP en templates Thymeleaf.
 * 
 * <p>Ce Mojo permet d'automatiser la conversion des pages JSP vers Thymeleaf lors 
 * du cycle de vie de build Maven. Il utilise le framework JSP2Thymeleaf pour effectuer
 * les conversions et prend en charge de nombreuses options de configuration pour
 * personnaliser le processus.</p>
 *
 * <p>Exemple de configuration dans pom.xml:</p>
 * <pre>
 * &lt;plugin&gt;
 *     &lt;groupId&gt;com.cybernostics&lt;/groupId&gt;
 *     &lt;artifactId&gt;jsp2tl-maven-plugin&lt;/artifactId&gt;
 *     &lt;version&gt;1.0-SNAPSHOT&lt;/version&gt;
 *     &lt;configuration&gt;
 *         &lt;srcDirectory&gt;${basedir}/src/main/webapp/WEB-INF/jsp&lt;/srcDirectory&gt;
 *         &lt;outputDirectory&gt;${basedir}/src/main/resources/templates&lt;/outputDirectory&gt;
 *         &lt;includes&gt;
 *             &lt;include&gt;**/*.jsp&lt;/include&gt;
 *         &lt;/includes&gt;
 *         &lt;excludes&gt;
 *             &lt;exclude&gt;**/excluded/*.jsp&lt;/exclude&gt;
 *         &lt;/excludes&gt;
 *     &lt;/configuration&gt;
 *     &lt;executions&gt;
 *         &lt;execution&gt;
 *             &lt;id&gt;convert-jsps&lt;/id&gt;
 *             &lt;goals&gt;
 *                 &lt;goal&gt;convert&lt;/goal&gt;
 *             &lt;/goals&gt;
 *             &lt;phase&gt;generate-resources&lt;/phase&gt;
 *         &lt;/execution&gt;
 *     &lt;/executions&gt;
 * &lt;/plugin&gt;
 * </pre>
 * 
 * @author jason
 * @since 1.0
 */
@Mojo(name = "convert", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresProject = true)
public class JSPConvertMojo extends AbstractMojo {
    // ...
}
```

## Documentation des Paramètres

```java
/**
 * Répertoire de destination où les templates Thymeleaf convertis seront générés.
 * Ce répertoire sera créé s'il n'existe pas.
 */
@Parameter(defaultValue = "${project.build.directory}/classes/templates", property = "outputDir", required = true)
private File outputDirectory;

/**
 * Indique si les liens vers d'autres fichiers JSP doivent être mis à jour vers
 * leurs équivalents Thymeleaf. Si défini à true, les extensions .jsp seront
 * remplacées par .html et les chemins ajustés selon la structure de destination.
 */
@Parameter(defaultValue = "true", property = "updateLinks", required = true)
private Boolean updateLinks;

/**
 * Répertoire source contenant les fichiers JSP à convertir.
 * Ce répertoire doit exister.
 */
@Parameter(defaultValue = "${project.basedir}/src/main/webapp/WEB-INF/jsp", required = true)
private File srcDirectory;

/**
 * Répertoire contenant des scripts de conversion personnalisés.
 * Les scripts dans ce répertoire seront chargés et appliqués pendant la conversion.
 * Ce paramètre est optionnel.
 */
@Parameter(defaultValue = "${project.basedir}/src/main/jsp2thymeleaf", required = false)
private File converterScriptDirectory;

/**
 * Liste de packages contenant des implémentations de convertisseurs de taglibs.
 * Ces packages seront scannés à la recherche d'implémentations de l'interface
 * {@link com.cybernostics.jsp2thymeleaf.api.common.taglib.ConverterRegistration}.
 */
@Parameter(defaultValue = "com.cybernostics.jsp2thymeleaf.conveters.tld", required = false)
private String taglibConverterPackages;

/**
 * Liste des patterns d'inclusion pour identifier les fichiers JSP à convertir.
 * Utilise la syntaxe de pattern Ant.
 */
@Parameter
private String[] includes = DEFAULT_INCLUDES;

/**
 * Liste des patterns d'exclusion pour identifier les fichiers JSP à ignorer.
 * Utilise la syntaxe de pattern Ant.
 */
@Parameter
private String[] excludes;
```

## Documentation des Méthodes

```java
/**
 * Exécute le goal de conversion JSP vers Thymeleaf.
 * Cette méthode est appelée par Maven lors de l'exécution du plugin.
 *
 * @throws MojoExecutionException Si une erreur survient pendant l'exécution du plugin
 */
@Override
public void execute() throws MojoExecutionException {
    // ...
}

/**
 * Génère la configuration pour le convertisseur JSP2Thymeleaf.
 * Cette méthode configure les options de conversion en fonction des paramètres
 * définis dans le pom.xml.
 *
 * @return La configuration pour le convertisseur
 */
private JSP2ThymeleafConfiguration createConfiguration() {
    // ...
}

/**
 * Trouve tous les fichiers JSP à convertir en fonction des patterns d'inclusion et d'exclusion.
 *
 * @param log Le logger Maven pour afficher les informations
 * @return La liste des fichiers JSP à convertir
 * @throws MojoExecutionException Si une erreur survient pendant la recherche des fichiers
 */
private List<File> getFilesToProcess(Log log) throws MojoExecutionException {
    // ...
}

/**
 * Crée le répertoire de destination s'il n'existe pas déjà.
 *
 * @throws MojoExecutionException Si le répertoire ne peut pas être créé
 */
private void ensureOutputDirectoryExists() throws MojoExecutionException {
    // ...
}

/**
 * Convertit un chemin de fichier JSP en chemin de fichier Thymeleaf équivalent.
 *
 * @param jspFile Le fichier JSP source
 * @return Le chemin du fichier Thymeleaf correspondant
 */
private Path getOutputPath(File jspFile) {
    // ...
}
```

## Documentation des Constantes

```java
/**
 * Patterns d'inclusion par défaut pour les fichiers JSP.
 * Ces patterns sont utilisés si aucun pattern d'inclusion spécifique n'est défini.
 */
private static final String[] DEFAULT_INCLUDES = {
    "**/*.jsp", "**/*.jspx", "**/*.jspf"
};
```
