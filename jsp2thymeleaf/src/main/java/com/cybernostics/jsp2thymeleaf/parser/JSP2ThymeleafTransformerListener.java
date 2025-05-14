/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.parser;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp.parser.JSPParser.DtdContext;
import com.cybernostics.jsp.parser.JSPParser.HtmlChardataContext;
import com.cybernostics.jsp.parser.JSPParser.JspDirectiveContext;
import com.cybernostics.jsp.parser.JSPParser.JspElementContext;
import com.cybernostics.jsp.parser.JSPParser.ScriptletContext;
import com.cybernostics.jsp.parser.JSPParser.XhtmlCDATAContext;
import com.cybernostics.jsp.parser.JSPParserBaseListener;
import com.cybernostics.jsp2thymeleaf.api.common.Namespaces;
import static com.cybernostics.jsp2thymeleaf.api.common.Namespaces.TH;
import static com.cybernostics.jsp2thymeleaf.api.common.Namespaces.XMLNS;
import com.cybernostics.jsp2thymeleaf.api.elements.ELExpressionConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSP2ThymeleafExpressionParseException;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPDirectiveConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPElementNodeConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPNodeConverterSource;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import static com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters.defaultSource;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.api.exception.JSPNodeException;
import com.cybernostics.jsp2thymeleaf.api.util.MapUtils;
import com.cybernostics.jsp2thymeleaf.api.util.PrefixedName;
import static com.cybernostics.jsp2thymeleaf.api.util.PrefixedName.prefixedNameFor;
import com.cybernostics.jsp2thymeleaf.converters.jsp.JSPDirectiveConverterSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.el.parser.ParseException;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;

/**
 * This class is a visitor for the parsed JSP element stream which creates
 * new Thymeleaf JDom element(s) for each JSP node element visited.
 * 
 * @author jason
 */
public class JSP2ThymeleafTransformerListener extends JSPParserBaseListener implements JSPElementNodeConverter
{

    private static final String THYMELEAF_DTD = "http://thymeleaf.org/dtd/xhtml-strict-thymeleaf.dtd";
    private final Logger logger = Logger.getLogger(JSP2ThymeleafTransformerListener.class.getName());
    
    private Document thymeleafDoc = new Document();
    private final List<JSP2ThymeLeafException> problems = new ArrayList<>();
    private ScopedJSPConverters scopedJspConverters;

    private Element currentElement;
    
    private JSPDirectiveConverterSource jspDirectiveConverterSource = new JSPDirectiveConverterSource();
    protected ELExpressionConverter expressionConverter = new ELExpressionConverter();

    private final Pattern whitespace = Pattern.compile("^\\s+$");
    private boolean showBanner;

    public static final String NEWLINE = System.getProperty("line.separator");

    public JSP2ThymeleafTransformerListener(ScopedJSPConverters scopedJspConverters)
    {
        showBanner = false;
        this.scopedJspConverters = scopedJspConverters;
    }

    public Document getDocument() {
        return thymeleafDoc;
    }

    public List<JSP2ThymeLeafException> getProblems()
    {
        return problems;
    }

    @Override
    public void enterJspElement(JspElementContext ctx)
    {
        logger.log(Level.FINE, "enterJspElement");
        try
        {
            final JSPElementNodeConverter converterForNode = converterForNode(ctx);
            final List<Content> content = converterForNode.process(ctx, this);
            addContent(content);
            pushElement(content);

        } catch (JSP2ThymeLeafException exception)
        {
            problems.add(exception);
        }
        catch(Throwable t){
            problems.add(new JSPNodeException(t.getMessage(), t, ctx));
        }
    }

    @Override
    public void exitJspElement(JspElementContext ctx)
    {
        logger.log(Level.FINE, "exitJspElement");

        try
        {
            popElement();

        } catch (Exception e)
        {
            problems.add(new JSPNodeException(e.getMessage(), e, ctx));
        }
    }

    @Override
    public void exitJspDocument(JSPParser.JspDocumentContext ctx)
    {
        if (thymeleafDoc.hasRootElement())
        {
            Element rootElement = thymeleafDoc.getRootElement();
            namespacesFor(rootElement).stream().filter(it -> !it.getPrefix().equals(rootElement.getNamespace().getPrefix())).forEach(ns -> rootElement.addNamespaceDeclaration(ns));
        }
    }    @Override
    public void enterJspExpression(JSPParser.JspExpressionContext ctx)
    {
        try
        {
            super.enterJspExpression(ctx);
            Element expression = new Element("span", XMLNS);
            expression.setAttribute("text", expressionConverter.convert(ctx.getText(), scopedJspConverters), TH);
            expression.removeNamespaceDeclaration(XMLNS);
            addContent(expression);
        } catch (ParseException | JSP2ThymeleafExpressionParseException ex)
        {
            // Log the error but don't treat it as a blocking exception
            logger.log(Level.WARNING, "Problem converting JSP expression: " + ctx.getText(), ex);
            
            // Create a more descriptive comment with the original expression
            String expressionText = ctx.getText();
            
            // Escape problematic characters for JDOM
            expressionText = expressionText.replace("<%=", "&lt;%=").replace("%>", "%&gt;");
            
            // Use Text instead of Comment to avoid JDOM exceptions
            String commentText = "<!-- ORIGINAL JSP EXPRESSION: " + expressionText + " -->\n"
                              + "<!-- TIP: This expression could not be automatically converted. "
                              + "Use th:text or th:utext with ${...} for the Thymeleaf equivalent -->\n"
                              + "<!-- EXAMPLE: <span th:text=\"${...}\">Default text</span> -->";
            
            // Add the comment without generating an exception
            addContent(new Text(commentText));
        }
    }

    @Override
    public void enterXhtmlCDATA(XhtmlCDATAContext ctx)
    {
        logger.log(Level.FINE, "enterXhtmlCDATA" + ctx.getText());
        addContent(new CDATA(unEscapeText(ctx.getText())));
    }    @Override
    public void enterHtmlChardata(HtmlChardataContext ctx)
    {
        String content = ctx.getText();
        logger.log(Level.FINE, "enterHtmlCharData: " + content);
        
        // Convert any whitespace-only text directly without additional processing
        if (whitespace.matcher(content).matches()) {
            addContent(new Text(content));
            return;
        }
        
        // Check if the content contains JSP scriptlets that need to be preserved
        if (!preserveScriptletsInHtmlContent(content)) {
            // If no scriptlet was detected, process normally
            addContent(new Text(unEscapeText(content)));
        }
    }

    private String unEscapeText(String s)
    {
        return s.replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&#xD;", "\r");
    }    @Override
    public void enterHtmlCommentText(JSPParser.HtmlCommentTextContext ctx)
    {
        logger.log(Level.FINE, "enterHtmlCommentText" + ctx.getText());
        
        String commentText = ctx.getText();
        boolean containsScriptlet = commentText.contains("<%") && commentText.contains("%>");
        
        if (containsScriptlet) {
            // For comments containing scriptlets, preserve the JSP code safely
            // to avoid JDOM exceptions while maintaining the scriptlet content
            String safeComment = "<!-- HTML COMMENT WITH JSP CODE: \n" +
                                commentText.replace("<%", "&lt;%").replace("%>", "%&gt;") +
                                "\n-->";
            addContent(new Text(safeComment));
        } else {
            // Regular HTML comment without JSP code
            addContent(new Comment(commentText));
        }
    }

    private JSPElementNodeConverter converterForNode(JSPParser.JspElementContext node)
    {
        PrefixedName domTag = prefixedNameFor(node.name.getText());
        Optional<JSPNodeConverterSource> converterSource1 = convertersForDomTag(domTag);
        final Optional<JSPElementNodeConverter> converterFor = converterSource1
                .orElseGet(missingTaglib(domTag, node))
                .converterFor(node);

        return converterFor.orElseGet(missingNodeConverter(domTag, node));

    }    @Override
    public void enterScriptlet(ScriptletContext ctx)
    {
        logger.log(Level.WARNING, "Scriptlet detected and converted to comment: " + ctx.getText());
        
        String scriptletText = ctx.getText();
        
        // Identifier le type de scriptlet pour un meilleur commentaire
        String scriptletType = identifyScriptletType(scriptletText);
        String thymeleafEquivalent = getThymeleafEquivalentHint(scriptletType);
        
        // Préserver le script complet dans un commentaire avec une structure plus claire
        StringBuilder commentBuilder = new StringBuilder();
        commentBuilder.append("<!-- JSP SCRIPTLET ").append(scriptletType).append(":\n");
        commentBuilder.append("ORIGINAL: ").append(scriptletText.replace("<%", "&lt;%").replace("%>", "%&gt;")).append("\n");
        commentBuilder.append("THYMELEAF EQUIVALENT: ").append(thymeleafEquivalent).append("\n");
        commentBuilder.append("-->");
        
        // Utiliser Text au lieu de Comment pour éviter les exceptions JDOM
        addContent(new Text(commentBuilder.toString()));
    }
    
    /**
     * Identifie le type d'un scriptlet JSP en fonction de son délimiteur
     * 
     * @param scriptletText Le texte du scriptlet à analyser
     * @return Le type de scriptlet identifié (DECLARATION, DIRECTIVE, EXPRESSION, STANDARD, COMMENT)
     */
    private String identifyScriptletType(String scriptletText) {
        if (scriptletText.startsWith("<%!")) {
            return "DECLARATION";
        } else if (scriptletText.startsWith("<%@")) {
            return "DIRECTIVE";
        } else if (scriptletText.startsWith("<%=")) {
            return "EXPRESSION";
        } else if (scriptletText.startsWith("<%--")) {
            return "COMMENT";
        } else {
            return "STANDARD";
        }
    }
    
    /**
     * Fournit des suggestions pour les équivalents Thymeleaf en fonction du type de scriptlet
     * 
     * @param scriptletType Le type de scriptlet JSP
     * @return Une suggestion d'équivalent Thymeleaf
     */
    private String getThymeleafEquivalentHint(String scriptletType) {
        switch (scriptletType) {
            case "DECLARATION":
                return "Move to a Spring @Component or @Controller class";
            case "DIRECTIVE":
                return "Use appropriate Thymeleaf namespace or configuration";
            case "EXPRESSION":
                return "Use ${...} or *{...} in th:text or th:utext attributes";
            case "COMMENT":
                return "Use <!-- ... --> or th:block th:remove=\"all\"";
            case "STANDARD":
                return "Use th:if, th:each, th:with or other Thymeleaf attributes";
            default:
                return "Use appropriate Thymeleaf constructs";
        }
    }@Override
    public void enterJspDirective(JspDirectiveContext ctx)
    {
        logger.log(Level.FINE, "enterJspDirective" + ctx.getText());

        try
        {
            final Optional<JSPDirectiveConverter> converter = jspDirectiveConverterSource.converterFor(ctx);
            if (converter.isPresent()) {
                final List<Content> content = converter.get().process(ctx, this);
                addContent(content);
            } else {
                logger.log(Level.WARNING, "No converter found for directive: " + ctx.getText());
            }
        } catch (JSP2ThymeLeafException exception)
        {
            problems.add(exception);
        } catch (Exception e) {
            problems.add(new JSPNodeException("Error processing directive: " + e.getMessage(), e, ctx));
        }
    }

    @Override
    public void enterDtd(DtdContext ctx)
    {
        DocType dt = new DocType(ctx.dtdElementName().getText(), THYMELEAF_DTD);

        thymeleafDoc.setDocType(dt);
    }

    private Optional<JSPNodeConverterSource> convertersForDomTag(PrefixedName domTag)
    {
        return scopedJspConverters.forPrefix(domTag.getPrefix());
    }    private Supplier<JSPNodeConverterSource> missingTaglib(PrefixedName domTag, JSPParser.JspElementContext node)
    {
        return () ->
        {
            if (!domTag.getPrefix().isEmpty())
            {
                // Vérifier si c'est une taglib non supportée connue
                if (scopedJspConverters.isUnsupportedTaglib(domTag.getPrefix())) {
                    // Récupérer l'URI pour le message d'avertissement
                    String uri = scopedJspConverters.getUnsupportedTaglibUri(domTag.getPrefix());
                    Logger.getLogger(JSP2ThymeleafTransformerListener.class.getName())
                          .log(Level.WARNING, "Utilisation d'une taglib non supportée: {0} (uri: {1}). La balise sera préservée.", 
                               new Object[]{domTag, uri});
                    
                    // Ajouter un problème comme avertissement mais pas comme erreur bloquante
                    problems.add(MapUtils.rex("No taglib converter found for tag " + domTag + 
                                             " (uri: " + uri + "). Tag will be preserved with comments.", node).get());
                } else {
                    // C'est une taglib inconnue
                    problems.add(MapUtils.rex("No taglib converter found for tag " + domTag + ". You need to add a converter lib", node).get());
                }
            }

            return defaultSource();
        };
    }    private Supplier<JSPElementNodeConverter> missingNodeConverter(PrefixedName domTag, JSPParser.JspElementContext node)
    {
        return () ->
        {
            if (!domTag.getPrefix().isEmpty())
            {
                // Vérifier si le tag fait partie d'une taglib non supportée connue
                if (scopedJspConverters.isUnsupportedTaglib(domTag.getPrefix())) {
                    // Créer une classe anonyme implémentant JSPElementNodeConverter
                    return new JSPElementNodeConverter() {
                        @Override
                        public List<Content> process(JSPParser.JspElementContext tagElement, JSPElementNodeConverter context) {
                            Element element = new Element(tagElement.name.getText());
                            
                            // Ajouter les attributs
                            tagElement.atts.stream().forEach(att -> {
                                element.setAttribute(att.name.getText(), att.value.getText().replaceAll("\"", ""));
                            });
                            
                            // Appeler la méthode handleUnknownTag pour préserver avec commentaire
                            String uri = scopedJspConverters.getUnsupportedTaglibUri(domTag.getPrefix());
                            com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeleafFileConverter.handleUnknownTag(element, domTag.toString(), uri);
                            
                            return Arrays.asList(element);
                        }
                        
                        @Override
                        public boolean canHandle(JSPParser.JspElementContext JSPNode) {
                            return true;
                        }
                        
                        @Override
                        public ScopedJSPConverters getScopedConverters() {
                            return scopedJspConverters;
                        }
                        
                        @Override
                        public void setScopedConverters(ScopedJSPConverters scopedConverters) {
                            // Pas besoin d'implémentation, utilise le contexte parent
                        }
                    };
                } else {
                    problems.add(MapUtils.rex("No node converter found for tag " + domTag, node).get());
                }
            }
            return getDefaultCopyNodeConverter(node);
        };
    }

    private JSPElementNodeConverter getDefaultCopyNodeConverter(JSPParser.JspElementContext node)
    {
        return defaultSource().converterFor(node).get();
    }

    private Element createFragmentDef(List<Content> contents)
    {
        Element html = new Element("html", Namespaces.XMLNS);

        html.addContent(NEWLINE);
        Element head = new Element("head", XMLNS);
        html.addContent(head);
        html.addContent(NEWLINE);
        Element title = new Element("title", XMLNS);
        title.setText("Thymeleaf Fragment Definition");
        head.addContent(NEWLINE);
        head.addContent(title);
        head.addContent(NEWLINE);
        Element body = new Element("body",XMLNS);
        html.addContent(body);
        html.addContent(NEWLINE);
        Element blockContainer = new Element("block", TH);
        body.addContent(blockContainer);
        blockContainer.setAttribute("fragment", "content", TH);
        blockContainer.addContent(contents);
        blockContainer.addContent(NEWLINE);
        currentElement = body;
        namespacesFor(html).stream().filter(it -> !it.getPrefix().equals(html.getNamespace().getPrefix())).forEach(ns -> html.addNamespaceDeclaration(ns));
        return html;
    }

    private static Set<Namespace> namespacesFor(Element html)
    {
        Set<Namespace> namespaces = new HashSet<Namespace>();
        namespaces.addAll(html.getNamespacesInScope());
        namespaces.addAll(html.getChildren().stream().flatMap(it -> namespacesFor(it).stream()).collect(toSet()));
        return namespaces;
    }

    @Override
    public boolean canHandle(JSPParser.JspElementContext JSPNode)
    {
        return true;
    }

    private List<Content> rootContentFor(List<Content> contents)
    {
        List<Content> amendedContents = new java.util.ArrayList<>();
        amendedContents.addAll(contents);
        if (showBanner)
        {
            amendedContents.add(new Comment("Created with JSP2Thymeleaf"));
            amendedContents.add(new Text(NEWLINE));
        }

        final Optional<Content> foundHtmlElement = amendedContents.stream().filter(JSP2ThymeleafTransformerListener::isHtmlElement).findFirst();

        if (foundHtmlElement.isPresent())
        {
            final Element htmlElement = (Element) foundHtmlElement.get();
            amendedContents.remove(htmlElement);
            trimTrailingWhitespace(amendedContents);
            htmlElement.addContent(amendedContents);
            htmlElement.setNamespace(Namespaces.XMLNS);
            htmlElement.setAttribute("fragment", "content", TH);

            return elementWithDocTypeIfNeeded(htmlElement);
        } else
        {
            Element thFragment = createFragmentDef(amendedContents);
            return elementWithDocTypeIfNeeded(thFragment);
        }

    }

    private List<Content> elementWithDocTypeIfNeeded(final Element htmlElement)
    {

        if (thymeleafDoc.getDocType() != null)
        {
            return Arrays.asList(htmlElement);
        }
        return Arrays.asList(new DocType("html", THYMELEAF_DTD), htmlElement);
    }

    private static Boolean isHtmlElement(Content content)
    {
        return content instanceof Element
                && ((Element) content).getName().equals("html");
    }

    private void trimTrailingWhitespace(List<Content> contents)
    {
        while (contents.size() > 0
                && whitespace
                        .matcher(contents.get(contents.size() - 1).getValue())
                        .matches())
        {
            contents.remove(contents.size() - 1);
        }
    }

    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context)
    {
        final PrefixedName prefixedName = PrefixedName.prefixedNameFor(node.name.getText());
        final Optional<JSPNodeConverterSource> converter = scopedJspConverters.forPrefix(prefixedName.getPrefix());
        return converter.get().converterFor(node).get().process(node, this);
    }

    private void addContent(Content... content)
    {
        addContent(Arrays.asList(content));
    }    private void addContent(List<Content> content)
    {
        if (!content.isEmpty())
        {
            if (currentElement == null)
            {
                try {
                    content = rootContentFor(content);
                    Optional<Content> htmlElementOptional = content.stream()
                            .filter(JSP2ThymeleafTransformerListener::isHtmlElement)
                            .findFirst();
                    
                    if (htmlElementOptional.isPresent()) {
                        currentElement = (Element) htmlElementOptional.get();
                        if(content.size()>1){
                            if(content.get(0) instanceof DocType){
                                thymeleafDoc.setDocType((DocType) content.get(0));
                            }
                            thymeleafDoc.addContent(content.stream().skip(1).collect(toList()));
                        } else {
                            thymeleafDoc.addContent(content.stream().collect(toList()));
                        }
                    } else {
                        // No HTML root element found, create a wrapper div
                        logger.log(Level.WARNING, "Aucun élément HTML racine trouvé, création d'un conteneur div");
                        Element wrapperDiv = new Element("div");
                        wrapperDiv.setAttribute("class", "jsp-thymeleaf-wrapper");
                        
                        // Add a comment explaining the wrapper
                        wrapperDiv.addContent(new Comment(
                            " Ce div a été automatiquement créé pour encapsuler les éléments multiples sans racine commune. " +
                            "Vous voudrez peut-être restructurer le document HTML. "
                        ));
                        
                        // Add the content to the wrapper
                        wrapperDiv.addContent(content);
                        thymeleafDoc.addContent(wrapperDiv);
                        currentElement = wrapperDiv;
                    }
                } catch (Exception e) {
                    // Handle the "Cannot add a second root element" error
                    logger.log(Level.WARNING, "Problème lors de l'ajout d'éléments multiples: " + e.getMessage(), e);
                    
                    // If the document already has a root element, try to add the content to it
                    if (thymeleafDoc.hasRootElement()) {
                        Element rootElement = thymeleafDoc.getRootElement();
                        
                        // Add warning comment
                        rootElement.addContent(new Comment(" ATTENTION: Éléments multiples détectés et fusionnés. Structure originale potentiellement modifiée. "));
                        
                        // Add the content to the existing root
                        try {
                            rootElement.addContent(content);
                            currentElement = rootElement;
                        } catch (Exception ex) {
                            // If that fails too, wrap the content in a div and add it to the root
                            logger.log(Level.WARNING, "Impossible d'ajouter directement au nœud racine, utilisation d'un div d'encapsulation.");
                            Element wrapperDiv = new Element("div");
                            wrapperDiv.setAttribute("class", "jsp-content-fragment");
                            wrapperDiv.addContent(content);
                            rootElement.addContent(wrapperDiv);
                            currentElement = wrapperDiv;
                        }
                    }
                }
            } else {
                try {
                    currentElement.addContent(content);
                } catch (Exception e) {
                    // Handle errors when adding content to an existing element
                    logger.log(Level.WARNING, "Erreur lors de l'ajout de contenu: " + e.getMessage(), e);
                    
                    // Create a wrapper div for the content
                    Element wrapperDiv = new Element("div");
                    wrapperDiv.setAttribute("class", "jsp-content-fragment");
                    wrapperDiv.addContent(new Comment(" CONTENU ORIGINAL AVEC ERREUR: " + e.getMessage() + " "));
                    wrapperDiv.addContent(content);
                    
                    try {
                        currentElement.addContent(wrapperDiv);
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Impossible d'ajouter le contenu même après encapsulation: " + ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    private void pushElement(List<Content> content)
    {

        // push to stack if empty push current element (duped) in place of skipped element
        final List<Content> elements = content.stream().filter(it -> it instanceof Element).collect(toList());
        if (!elements.isEmpty())
        {
            currentElement = (Element) elements.get(elements.size() - 1);
        }
    }

    private void popElement()
    {
        if (currentElement != null)
        {
            Element childElement = currentElement;
            currentElement = childElement.getParentElement();
            Attribute replaceParentAttributeName = childElement.getAttribute("data-replace-parent-attribute-name");
            if (replaceParentAttributeName != null)
            {
                String parentAttributeName = replaceParentAttributeName.getValue();
                Attribute replaceParentAttributeValue = childElement.getAttributes()
                        .stream()
                        .filter(it -> it.getName().equals("data-replace-parent-attribute-value"))
                        .findFirst().orElseThrow(() -> new RuntimeException("Unable to convert element embedded in attribute. Add a whenQuotedInAttributeReplaceWith drective to your element converter registration for this element."));
                currentElement.removeAttribute(parentAttributeName);
                currentElement.setAttribute(parentAttributeName, replaceParentAttributeValue.getValue(), replaceParentAttributeValue.getNamespace());
            }
            if (currentElement != null)
            {
                currentElement.getChildren()
                        .stream()
                        .filter(it -> it.getName().equals("deleteme"))
                        .forEach(it -> currentElement.removeChild(it.getName(), it.getNamespace()));
            }
        }
    }

    public void setShowBanner(boolean showBanner)
    {
        this.showBanner = showBanner;
    }

    @Override
    public ScopedJSPConverters getScopedConverters()
    {
        return scopedJspConverters;
    }    @Override
    public void setScopedConverters(ScopedJSPConverters scopedConverters)
    {
        this.scopedJspConverters = scopedConverters;
    }

    /**
     * Assainit le contenu d'un commentaire HTML pour éviter les problèmes avec JDOM
     * Les commentaires HTML ne peuvent pas contenir de double tirets (--)
     * 
     * @param content Le contenu à assainir
     * @return Le contenu assaini
     */
    private String sanitizeCommentContent(String content) {
        if (content == null) {
            return "";
        }
        
        // Enlever les délimiteurs de commentaire HTML s'ils existent
        if (content.startsWith("<!--")) {
            content = content.substring(4);
        }
        if (content.endsWith("-->")) {
            content = content.substring(0, content.length() - 3);
        }
        
        // Remplacer les double tirets par des tirets simples ou une autre séquence
        content = content.replace("--", "- -");
        
        // Échapper les séquences JSP problématiques
        content = content.replace("<%", "&lt;%").replace("%>", "%&gt;");
        
        return content;
    }    /**
     * Vérifie si un texte HTML contient des scriptlets JSP et les préserve
     * comme commentaires HTML si c'est le cas
     * 
     * @param content Le contenu HTML à analyser
     * @return true si des scriptlets ont été détectés et traités, false sinon
     */    /**
     * Vérifie si un texte HTML contient des scriptlets JSP et les préserve
     * comme commentaires HTML si c'est le cas
     * 
     * @param content Le contenu HTML à analyser
     * @return true si des scriptlets ont été détectés et traités, false sinon
     */
    private boolean preserveScriptletsInHtmlContent(String content) {
        // Motifs pour détecter les différents types de scriptlets JSP
        // <%! ... %> : déclaration
        // <% ... %> : scriptlet standard
        // <%= ... %> : expression
        // <%@ ... %> : directive
        // <%-- ... --%> : commentaire

        boolean containsScriptlet = content.contains("<%!") || 
                                   content.contains("<%=") || 
                                   content.contains("<%@") || 
                                   content.contains("<%--") ||
                                   (content.contains("<%") && content.contains("%>"));
                                   
        if (containsScriptlet) {
            logger.log(Level.INFO, "Scriptlets détectés dans du contenu HTML et préservés");
            
            // Si c'est déjà un commentaire HTML, préserver la structure exacte
            boolean isHtmlComment = content.trim().startsWith("<!--") && content.trim().endsWith("-->");
            
            if (isHtmlComment) {
                // Pour éviter les problèmes avec les commentaires JDOM, utiliser Text directement
                // Sans échapper les délimiteurs pour préserver le code JSP exactement comme il est
                addContent(new Text(content));
            } else {
                // Identifier les différents types de scriptlets pour un meilleur commentaire
                String preservedContent = identifyAndPreserveScriptlets(content);
                
                // Envelopper dans un commentaire HTML pour préservation
                addContent(new Text("<!-- JSP CODE PRESERVED: \n" + preservedContent + "\n-->\n" +
                                   "<!-- To convert this JSP code to Thymeleaf, consider using appropriate Thymeleaf constructs -->\n"));
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Identifie et préserve les différents types de scriptlets JSP dans du contenu HTML
     * 
     * @param content Le contenu HTML contenant potentiellement des scriptlets JSP
     * @return Le contenu avec les scriptlets échappés et annotés
     */
    private String identifyAndPreserveScriptlets(String content) {
        String result = content;
        
        // Échapper tous les délimiteurs de scriptlet pour éviter les erreurs de syntaxe
        result = result.replace("<%!", "&lt;%! /* JSP DECLARATION */ ");
        result = result.replace("<%=", "&lt;%= /* JSP EXPRESSION */ ");
        result = result.replace("<%@", "&lt;%@ /* JSP DIRECTIVE */ ");
        result = result.replace("<%--", "&lt;%-- /* JSP COMMENT */ ");
        result = result.replace("<%", "&lt;% /* JSP SCRIPTLET */ ");
        result = result.replace("%>", " */ %&gt;");
        
        return result;
    }

    /**
     * Nouvelle implémentation qui traite les commentaires HTML/JSP et les convertit en commentaires Thymeleaf appropriés
     * Cette méthode gère les commentaires HTML standard et les cas spéciaux
     */    @Override
    public void enterHtmlComment(JSPParser.HtmlCommentContext ctx) {
        logger.log(Level.FINE, "Commentaire HTML détecté: " + ctx.getText());
        
        try {
            String commentText = ctx.getText();
            
            // Vérifier si c'est un commentaire avec des scriptlets JSP complets
            if (commentText.contains("<%!") || 
                commentText.contains("<%@") || 
                commentText.contains("<%=") ||
                (commentText.contains("<%") && commentText.contains("%>"))) {
                
                // C'est un commentaire contenant du code JSP
                logger.log(Level.INFO, "Commentaire HTML contenant du code JSP détecté et préservé");
                
                // Préserver le commentaire tel quel, avec le code JSP à l'intérieur
                // Note: ne pas utiliser un objet Comment JDOM qui lancerait des exceptions
                // avec les doubles tirets
                String preservedContent = commentText;
                
                // S'assurer que les balises de commentaire sont préservées 
                if (!preservedContent.startsWith("<!--")) {
                    preservedContent = "<!-- " + preservedContent;
                }
                if (!preservedContent.endsWith("-->")) {
                    preservedContent = preservedContent + " -->";
                }
                
                // Utiliser Text pour éviter les exceptions JDOM
                addContent(new Text(preservedContent));
            } 
            // Vérifier si c'est un commentaire JSP
            else if (commentText.contains("<%--") && commentText.contains("--%>")) {
                // C'est un commentaire JSP 
                logger.log(Level.INFO, "Commentaire JSP détecté et converti");
                
                // Extraire le contenu entre les balises <%-- et --%>
                int startIndex = commentText.indexOf("<%--") + 4;
                int endIndex = commentText.lastIndexOf("--%>");
                String jspCommentContent = "";
                
                if (startIndex > 3 && endIndex > startIndex) {
                    jspCommentContent = commentText.substring(startIndex, endIndex).trim();
                } else {
                    jspCommentContent = commentText;
                }
                
                // Utiliser Text pour éviter les exceptions JDOM
                addContent(new Text("<!-- JSP COMMENTAIRE: " + jspCommentContent.replace("--", "- -") + " -->"));
            }
            else {
                // Pour les commentaires HTML standards, éviter les exceptions JDOM
                // en assainissant le contenu mais en préservant la sémantique
                String cleanedContent = sanitizeCommentContent(commentText);
                addContent(new Text("<!-- " + cleanedContent + " -->"));
            }
        } catch (Exception e) {
            // En cas d'erreur, logger et créer un placeholder de commentaire simple
            logger.log(Level.WARNING, "Problème lors de la conversion du commentaire: " + e.getMessage(), e);
            // Utiliser Text au lieu de Comment pour éviter les exceptions JDOM
            addContent(new Text("<!-- COMMENTAIRE NON CONVERTIBLE -->"));
        }
    }

    /**
     * Handles JSP parsing errors in a more robust way
     * This method attempts to recover from parsing errors by preserving the problematic content
     * as HTML comments rather than failing completely
     * 
     * @param context The context in which the error occurred
     * @param exception The exception that was thrown
     * @param originalContent The original content that caused the error
     */
    private void handleJspParsingError(Object context, Exception exception, String originalContent) {
        // Log the error for debugging
        logger.log(Level.WARNING, "JSP parsing error: " + exception.getMessage(), exception);
        
        // Create a descriptive comment about the error
        StringBuilder errorComment = new StringBuilder();
        errorComment.append("<!-- JSP PARSING ERROR: ")
                   .append(exception.getClass().getSimpleName())
                   .append(" - ")
                   .append(exception.getMessage())
                   .append(" -->\n");
        
        // Preserve the original content in a safe way
        errorComment.append("<!-- ORIGINAL CONTENT (ESCAPED): \n")
                   .append(originalContent.replace("<", "&lt;").replace(">", "&gt;"))
                   .append("\n-->");
        
        // Add a hint about how to fix the issue
        errorComment.append("\n<!-- TIP: This JSP code couldn't be parsed correctly. ")
                   .append("Consider rewriting it using Thymeleaf syntax instead. -->");
          // Add the error comment to the output
        addContent(new Text(errorComment.toString()));
        
        // Add the problem to the list, but in a way that doesn't block conversion
        problems.add(JSP2ThymeLeafException.jsp2ThymeLeafExceptionBuilder(exception.getMessage(), exception).build());
    }

}
