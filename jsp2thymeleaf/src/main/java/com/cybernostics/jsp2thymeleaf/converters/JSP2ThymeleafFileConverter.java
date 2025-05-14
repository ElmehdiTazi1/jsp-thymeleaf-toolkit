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
import org.jdom2.Element;
import org.jdom2.Comment;
import org.jdom2.Attribute;
import org.jdom2.Namespace;
import java.util.ArrayList;

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
    }    /**
     * Converts a JSP file to a Thymeleaf file.
     * 
     * This method performs the complete conversion process:
     * <ol>
     *   <li>Parsing the JSP file</li>
     *   <li>Converting JSP elements to Thymeleaf elements</li>
     *   <li>Post-processing the generated document</li>
     *   <li>Writing the result to the output file</li>
     * </ol>
     * 
     * @param file The tokenized JSP file to convert
     * @param toWrite The output file for the Thymeleaf result
     * @param converterScope The conversion context containing the converters to use
     * @return A list of exceptions encountered during conversion
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
              // Configure the parser to continue even when encountering errors
            parser.removeErrorListeners(); // Remove default error listeners
            parser.addErrorListener(jsp2ThymeleafErrorCollector); // Add our custom error collector
            
            try {
                // Specify our entry point
                JSPParser.JspDocumentContext documentContext = parser.jspDocument();
                
                // Walk it and attach our parsedElementListener
                ParseTreeWalker walker = new ParseTreeWalker();
                parsedElementListener.setShowBanner(showBanner);
                walker.walk(parsedElementListener, documentContext);
    
                final Document document = parsedElementListener.getDocument();
                if (document.hasRootElement())
                {
                    // Post-process the document to clean up any issues
                    DomWalker docwalker = new DomWalker(
                        DomBlockCleaner.get(),
                        ScriptInlineSpanConverter.get()
                    );
                    docwalker.walk(document.getRootElement());
                    
                    // Write the converted document to the output file
                    write(document, new FileOutputStream(toWrite));
                    logger.log(Level.INFO, "JSP2Thymeleaf wrote: " + toWrite.getAbsolutePath());
                }
                
                // Add any problems encountered during parsing to the error collector
                jsp2ThymeleafErrorCollector.add(parsedElementListener.getProblems());
            } catch (Exception ex) {
                // Log the conversion error
                logConversionError(file, ex);
                // Add the exception to our collector - create a custom exception compatible with our API                jsp2ThymeleafErrorCollector.add(JSP2ThymeLeafException.jsp2ThymeLeafExceptionBuilder(ex).build());
                
                // Rethrow to be handled by the outer catch
                throw ex;
            }
        } catch (Exception ex)
        {            Logger.getLogger(JSP2ThymeleafFileConverter.class.getName()).log(Level.SEVERE, 
                "Error processing file " + file.getFilePath().getFileName() + ": " + ex.getMessage(), ex);
        }
        
        return jsp2ThymeleafErrorCollector.getExceptions();
    }
      /**
     * Handles an unknown or unsupported tag by preserving it in the HTML document
     * and adding a comment to indicate that the tag needs to be manually converted.
     * 
     * @param element DOM Element representing the unsupported tag
     * @param tagName Name of the tag
     * @param uri Namespace URI of the tag
     */
    public static void handleUnknownTag(Element element, String tagName, String uri) {
        // Create a descriptive comment about the unconverted tag
        StringBuilder commentBuilder = new StringBuilder();
        commentBuilder.append(" UNCONVERTED TAG: ").append(tagName)
                     .append(" (namespace: ").append(uri).append(")\n");
        
        // Add hint for common tag libraries
        String thymeleafEquivalent = getThymeleafEquivalentForTag(tagName, uri);
        commentBuilder.append(" THYMELEAF EQUIVALENT: ").append(thymeleafEquivalent).append("\n");
        commentBuilder.append(" Please convert manually ");
        
        Comment comment = new Comment(commentBuilder.toString());
        
        // If the element has a parent, add the comment before the original tag
        Element parent = element.getParentElement();
        if (parent != null) {
            parent.addContent(parent.indexOf(element), comment);
            
            // Preserve original tag information using data-* attributes
            preserveTagAttributes(element);
            
            // Check if tag might contain JSP scriptlets and preserve them safely
            preserveScriptletsInAttributes(element);
            
            // Change the namespace of the tag to make it valid in HTML
            element.setNamespace(Namespace.getNamespace(""));
        }
    }
    
    /**
     * Preserves any scriptlets that might be found in tag attributes
     * by escaping them properly to avoid parsing errors
     * 
     * @param element The element to check for scriptlets in attributes
     */
    private static void preserveScriptletsInAttributes(Element element) {
        List<Attribute> attributes = new ArrayList<>(element.getAttributes());
        for (Attribute attr : attributes) {
            String value = attr.getValue();
            if (value != null && (value.contains("<%") || value.contains("%>"))) {
                // Attribute contains scriptlet code, safely escape it
                String safeValue = value.replace("<%", "&lt;%").replace("%>", "%&gt;");
                
                // Replace the attribute with the escaped version
                element.setAttribute(attr.getName(), safeValue);
                
                // Add a data attribute to indicate this contained scriptlet
                element.setAttribute("data-had-scriptlet-" + attr.getName(), "true");
            }
        }
    }
    
    /**
     * Preserves original tag attributes by converting namespace-specific attributes
     * to data-* attributes to maintain their information in the HTML
     * 
     * @param element The element whose attributes need to be preserved
     */
    private static void preserveTagAttributes(Element element) {
        List<Attribute> originalAttrs = new ArrayList<>(element.getAttributes());
        for (Attribute attr : originalAttrs) {
            if (attr.getNamespaceURI() != null && !attr.getNamespaceURI().isEmpty()) {
                // Use the namespace prefix instead of getPrefix() which doesn't exist
                String namespacePrefix = attr.getNamespace().getPrefix();
                String newName = "data-original-" + namespacePrefix + "-" + attr.getName();
                element.setAttribute(newName, attr.getValue());
                element.removeAttribute(attr.getName(), attr.getNamespace());
            }
        }
    }
    
    /**
     * Provides guidance on Thymeleaf equivalents for common JSP tags
     * 
     * @param tagName The name of the JSP tag
     * @param uri The namespace URI of the tag
     * @return A string suggesting the Thymeleaf equivalent
     */
    private static String getThymeleafEquivalentForTag(String tagName, String uri) {
        // JSTL core tags
        if (uri != null && (uri.contains("java.sun.com/jsp/jstl/core") || 
                            uri.contains("java.sun.com/jstl/core"))) {
            switch (tagName) {
                case "out": return "th:text or th:utext";
                case "if": return "th:if or th:unless";
                case "choose": return "th:switch + th:case";
                case "forEach": return "th:each";
                case "set": return "th:with or th:object";
                case "url": return "th:href with @{...} syntax";
                case "import": return "th:include or th:replace";
                case "redirect": return "Spring redirect: prefix";
                default: return "Appropriate Thymeleaf attribute";
            }
        }
        
        // Spring form tags
        if (uri != null && uri.contains("springframework.org/tags/form")) {
            switch (tagName) {
                case "form": return "th:action + th:object";
                case "input": return "th:field";
                case "select": return "th:field + <option th:each>";
                case "option": return "th:value + th:text + th:selected";
                case "checkbox": return "th:field with type='checkbox'";
                case "radiobutton": return "th:field with type='radio'";
                case "errors": return "th:errors";
                default: return "Appropriate Thymeleaf form binding";
            }
        }
        
        // General case
        return "Use appropriate Thymeleaf syntax - see https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html";
    }
      /**
     * Logs JSP-specific conversion errors gracefully
     * 
     * @param file The source file being processed
     * @param ex The exception that was thrown during conversion
     */
    private void logConversionError(TokenisedFile file, Exception ex) {
        // Log the error
        logger.log(Level.SEVERE, "Error converting JSP file: " + ex.getMessage(), ex);        // Try to determine the context of the error
        String errorContext = "unknown location";
        if (ex instanceof JSP2ThymeLeafException) {
            JSP2ThymeLeafException jspEx = (JSP2ThymeLeafException)ex;
            if (jspEx.getLocation() != null) {
                errorContext = "line " + jspEx.getLocation().getLine();
            }
        }
        
        // Log a more informative error message
        logger.log(Level.WARNING, "JSP conversion error at " + errorContext + ": " + ex.getMessage() + 
                   "\nCheck for scriptlets or unsupported JSP syntax in the source file.");    }
}
