

/**
 * Grammaire du parser JSP pour ANTLR4
 *
 * Cette grammaire définit les règles syntaxiques pour analyser les fichiers JSP.
 * Elle se base sur les tokens générés par JSPLexer et construit un arbre syntaxique
 * représentant la structure du document JSP.
 * 
 * Les principales règles grammaticales incluent:
 * - jspDocument: point d'entrée de l'analyse, représente le document JSP complet
 * - jspElements: représente les éléments JSP (balises, directives, etc.)
 * - jspElement: représente une balise JSP/HTML avec ses attributs et contenu
 * - jspDirective: représente une directive JSP (<%@ ... %>)
 * - htmlContent: représente le contenu HTML, y compris les expressions JSP
 * - jspExpression: représente une expression JSP (${...})
 * - htmlAttribute: représente un attribut HTML avec son nom et sa valeur
 * 
 * Cette grammaire permet de construire une représentation structurée d'un document JSP
 * qui peut être ensuite utilisée pour la conversion vers Thymeleaf ou d'autres formats.
 */
parser grammar JSPParser;

options { tokenVocab=JSPLexer; }

jspDocument
    : (jspDirective | scriptlet | WHITESPACES)* xml? (jspDirective | scriptlet | WHITESPACES)* dtd? (jspDirective | scriptlet | WHITESPACES)* jspElements*
    ;

jspElements
    : htmlMisc* (jspElement|jspDirective| scriptlet) htmlMisc*
    ;

jspElement
    : TAG_BEGIN name=htmlTagName atts+=htmlAttribute* TAG_END htmlContent* CLOSE_TAG_BEGIN htmlTagName TAG_END
    | TAG_BEGIN name=htmlTagName atts+=htmlAttribute* TAG_SLASH_END
    | TAG_BEGIN name=htmlTagName atts+=htmlAttribute* TAG_END;


jspDirective
    : DIRECTIVE_BEGIN name=htmlTagName atts+=htmlAttribute*? TAG_WHITESPACE* DIRECTIVE_END
    ;

htmlContent
    : htmlChardata
    | jspExpression
    | jspElement 
    | xhtmlCDATA 
    | htmlComment 
    | scriptlet
    | jspDirective
    ;

jspExpression
    : EL_EXPR
    ;

htmlAttribute
    : 
      jspElement
    | name=htmlAttributeName EQUALS value=htmlAttributeValue
    | name=htmlAttributeName
    | scriptlet
    ;

htmlAttributeName
    : TAG_IDENTIFIER
    ;

htmlAttributeValue
    : QUOTE jspElement QUOTE
    | QUOTE? htmlAttributeValueExpr  QUOTE?
    | QUOTE htmlAttributeValueConstant? QUOTE
    ;

htmlAttributeValueExpr
    : EL_EXPR
    ;

htmlAttributeValueConstant
    : ATTVAL_ATTRIBUTE
    ;

htmlTagName
    : TAG_IDENTIFIER
    ;

htmlChardata
    : JSP_STATIC_CONTENT_CHARS_MIXED
    | JSP_STATIC_CONTENT_CHARS
    | WHITESPACES
    ;

htmlMisc
    : htmlComment
    | htmlChardata
    | jspExpression
    | scriptlet
    ;

htmlComment
    : JSP_COMMENT_START htmlCommentText? JSP_COMMENT_END 
    | JSP_CONDITIONAL_COMMENT_START htmlConditionalCommentText? JSP_CONDITIONAL_COMMENT_END
    ;

htmlCommentText
    : JSP_COMMENT_TEXT+?
    ;

htmlConditionalCommentText
    : JSP_CONDITIONAL_COMMENT
    ;
xhtmlCDATA
    : CDATA
    ;

dtd
    : DTD dtdElementName (DTD_PUBLIC publicId)? (DTD_SYSTEM systemId)?  TAG_END
    ;

dtdElementName
    : DTD_IDENTIFIER
    ;

publicId
    : DTD_QUOTED;

systemId
    : DTD_QUOTED;

xml: XML_DECLARATION name=htmlTagName atts+=htmlAttribute*? TAG_END
    ;

scriptlet
    : SCRIPTLET_OPEN BLOB_CONTENT JSP_END    ;

