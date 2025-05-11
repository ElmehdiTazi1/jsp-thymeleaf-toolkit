/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp.parser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Classe principale pour exécuter le parseur JSP.
 * Cette classe fournit des fonctionnalités pour analyser un document JSP
 * en utilisant la grammaire ANTLR4 définie. Elle initialise le lexer,
 * le parser et exécute l'analyse sur un document JSP.
 * 
 * Elle peut être utilisée comme point d'entrée pour traiter des fichiers JSP
 * ou comme utilitaire dans le cadre du processus de conversion JSP vers Thymeleaf.
 * 
 * Le flux de traitement comprend les étapes suivantes:
 * 1. Création d'un lexer JSP à partir du texte d'entrée
 * 2. Génération des tokens par le lexer
 * 3. Initialisation du parser avec les tokens générés
 * 4. Exécution de l'analyse du document à partir de la règle racine (jspDocument)
 * 5. Parcours de l'arbre syntaxique généré à l'aide d'un AntlrJSPListener
 * 
 * Cette classe implémente le pattern Facade pour simplifier l'utilisation
 * du parser JSP par les autres modules du toolkit.
 *
 * @author jason
 * @version 1.0
 * @see AntlrJSPListener
 * @see JSPLexer
 * @see JSPParser
 */
public class JSPParserRunner
{    /**
     * Analyse un texte JSP et imprime sa structure.
     * Cette méthode crée un lexer et un parser pour le texte d'entrée,
     * puis utilise un AntlrJSPListener pour parcourir l'arbre syntaxique généré.
     *
     * Le processus d'analyse se déroule en plusieurs étapes:
     * 1. Création d'un lexer JSP à partir du texte d'entrée
     * 2. Génération des tokens par le lexer
     * 3. Initialisation du parser avec les tokens générés
     * 4. Analyse du document à partir de la règle racine (jspDocument)
     * 5. Parcours de l'arbre syntaxique avec un listener pour traitement
     *
     * @param inputText Le contenu JSP à analyser
     */
    private static void printJSP(String inputText)
    {
        // Création du lexer à partir du texte d'entrée
        JSPLexer lexer = new JSPLexer(new ANTLRInputStream(inputText));

        // Génération de la liste des tokens reconnus
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Initialisation du parser avec les tokens
        JSPParser parser = new JSPParser(tokens);

        // Démarrage de l'analyse à partir de la règle jspDocument
        JSPParser.JspDocumentContext documentContext = parser.jspDocument();

        // Parcours de l'arbre syntaxique avec notre listener
        ParseTreeWalker walker = new ParseTreeWalker();
        AntlrJSPListener listener = new AntlrJSPListener(parser);
        walker.walk(listener, documentContext);
    }    /**
     * Point d'entrée principal du parser JSP.
     * Charge un fichier JSP à partir des ressources et l'analyse.
     * Cette méthode démontre l'utilisation du parser sur un fichier d'exemple.
     * 
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args)
    {
        final URL resource = JSPParserRunner.class.getClassLoader().getResource("01_clean_html.html");

        try
        {
            try
            {
                printJSP(new String(Files.readAllBytes(Paths.get(resource.toURI()))));
            } catch (IOException ex)
            {
                Logger.getLogger(JSPParserRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (URISyntaxException ex)
        {
            Logger.getLogger(JSPParserRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
