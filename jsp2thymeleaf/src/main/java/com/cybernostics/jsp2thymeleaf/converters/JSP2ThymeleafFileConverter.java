/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.converters;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.postprocessors.ScriptInlineSpanConverter;
import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.common.dom.DomWalker;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import static com.cybernostics.jsp2thymeleaf.converters.ConverterScanner.scanForConverters;
import com.cybernostics.jsp2thymeleaf.postprocessors.DomBlockCleaner;
import com.cybernostics.jsp2thymeleaf.parser.JSP2ThymeleafTransformerListener;
import static com.cybernostics.jsp2thymeleaf.parser.XMLDocumentWriter.write;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jdom2.Document;

/**
 * Convertisseur de fichiers JSP vers Thymeleaf.
 * 
 * Cette classe est responsable de la conversion d'un fichier JSP individuel
 * en fichier Thymeleaf. Elle gère:
 * <ul>
 *   <li>Le parsing du fichier JSP</li>
 *   <li>La transformation vers la structure Thymeleaf</li>
 *   <li>Le post-traitement du document généré</li>
 *   <li>L'écriture du résultat dans un fichier de sortie</li>
 * </ul>
 * 
 * Elle s'appuie sur le JSP parser généré par ANTLR et sur des transformateurs
 * qui convertissent les éléments JSP en équivalents Thymeleaf.
 *
 * @author jason
 * @version 1.0
 * @see JSP2ThymeleafTransformerListener
 * @see ScopedJSPConverters
 */
public class JSP2ThymeleafFileConverter
{
    /** Logger pour cette classe */
    public static final Logger logger = Logger.getLogger(JSP2ThymeleafFileConverter.class.getName());
    
    /** Détermine si une bannière de conversion est ajoutée au fichier généré */
    private boolean showBanner = true;

    /**
     * Constructeur initialisant le convertisseur avec la configuration spécifiée.
     * 
     * Cette méthode va également scanner et charger tous les convertisseurs
     * configurés dans le système (standard et personnalisés).
     * 
     * @param configuration La configuration à utiliser pour ce convertisseur
     */
    public JSP2ThymeleafFileConverter(JSP2ThymeleafConfiguration configuration)
    {
        scanForConverters(configuration);
    }

    /**
     * Définit si une bannière de conversion doit être ajoutée aux fichiers générés.
     * 
     * @param showBanner true pour afficher la bannière, false sinon
     */
    public void setShowBanner(boolean showBanner)
    {
        this.showBanner = showBanner;
    }

    /**
     * Convertit un fichier JSP en fichier Thymeleaf.
     * 
     * Cette méthode effectue le processus complet de conversion:
     * <ol>
     *   <li>Parsing du fichier JSP</li>
     *   <li>Conversion des éléments JSP en éléments Thymeleaf</li>
     *   <li>Post-traitement du document généré</li>
     *   <li>Écriture du résultat dans le fichier de sortie</li>
     * </ol>
     * 
     * @param file Le fichier JSP tokenisé à convertir
     * @param toWrite Le fichier de sortie pour le résultat Thymeleaf
     * @param converterScope Le contexte de conversion contenant les convertisseurs à utiliser
     * @return Une liste d'exceptions rencontrées pendant la conversion
     */
    public List<JSP2ThymeLeafException> convert(TokenisedFile file, File toWrite, ScopedJSPConverters converterScope)
    {

        JSP2ThymeleafTransformerListener parsedElementListener = new JSP2ThymeleafTransformerListener(converterScope);
        final JSP2ThymeleafErrorCollector jsp2ThymeleafErrorCollector = new JSP2ThymeleafErrorCollector(file);
        try
        {
            CommonTokenStream tokens = new CommonTokenStream(file.getLexer());
            // Pass the tokens to the parser
            JSPParser parser = new JSPParser(tokens);
            parser.addErrorListener(jsp2ThymeleafErrorCollector);
            // Specify our entry point
            JSPParser.JspDocumentContext documentContext = parser.jspDocument();
            // Walk it and attach our parsedElementListener
            ParseTreeWalker walker = new ParseTreeWalker();
            parsedElementListener.setShowBanner(showBanner);
            walker.walk(parsedElementListener, documentContext);

            final Document document = parsedElementListener.getDocument();
            if (document.hasRootElement())
            {
                DomWalker docwalker = new DomWalker(DomBlockCleaner.get(),
                        ScriptInlineSpanConverter.get());
                docwalker.walk(document.getRootElement());
                write(document, new FileOutputStream(toWrite));
                logger.log(Level.INFO, "JSP2Thymeleaf wrote:" + toWrite.getAbsolutePath());

            }

            jsp2ThymeleafErrorCollector.add(parsedElementListener.getProblems());
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(JSP2ThymeleafFileConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsp2ThymeleafErrorCollector.getExceptions();
    }

}
