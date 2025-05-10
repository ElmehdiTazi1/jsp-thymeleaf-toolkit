// Generated from com\cybernostics\jsp\parser\JSPParser.g4 by ANTLR 4.0
package com.cybernostics.jsp.parser;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.Token;

public interface JSPParserListener extends ParseTreeListener {
	void enterSystemId(JSPParser.SystemIdContext ctx);
	void exitSystemId(JSPParser.SystemIdContext ctx);

	void enterHtmlComment(JSPParser.HtmlCommentContext ctx);
	void exitHtmlComment(JSPParser.HtmlCommentContext ctx);

	void enterJspElements(JSPParser.JspElementsContext ctx);
	void exitJspElements(JSPParser.JspElementsContext ctx);

	void enterHtmlAttributeValueExpr(JSPParser.HtmlAttributeValueExprContext ctx);
	void exitHtmlAttributeValueExpr(JSPParser.HtmlAttributeValueExprContext ctx);

	void enterHtmlCommentText(JSPParser.HtmlCommentTextContext ctx);
	void exitHtmlCommentText(JSPParser.HtmlCommentTextContext ctx);

	void enterHtmlAttributeName(JSPParser.HtmlAttributeNameContext ctx);
	void exitHtmlAttributeName(JSPParser.HtmlAttributeNameContext ctx);

	void enterHtmlTagName(JSPParser.HtmlTagNameContext ctx);
	void exitHtmlTagName(JSPParser.HtmlTagNameContext ctx);

	void enterHtmlChardata(JSPParser.HtmlChardataContext ctx);
	void exitHtmlChardata(JSPParser.HtmlChardataContext ctx);

	void enterDtdElementName(JSPParser.DtdElementNameContext ctx);
	void exitDtdElementName(JSPParser.DtdElementNameContext ctx);

	void enterHtmlContent(JSPParser.HtmlContentContext ctx);
	void exitHtmlContent(JSPParser.HtmlContentContext ctx);

	void enterXhtmlCDATA(JSPParser.XhtmlCDATAContext ctx);
	void exitXhtmlCDATA(JSPParser.XhtmlCDATAContext ctx);

	void enterJspElement(JSPParser.JspElementContext ctx);
	void exitJspElement(JSPParser.JspElementContext ctx);

	void enterHtmlConditionalCommentText(JSPParser.HtmlConditionalCommentTextContext ctx);
	void exitHtmlConditionalCommentText(JSPParser.HtmlConditionalCommentTextContext ctx);

	void enterHtmlMisc(JSPParser.HtmlMiscContext ctx);
	void exitHtmlMisc(JSPParser.HtmlMiscContext ctx);

	void enterJspDocument(JSPParser.JspDocumentContext ctx);
	void exitJspDocument(JSPParser.JspDocumentContext ctx);

	void enterHtmlAttributeValueConstant(JSPParser.HtmlAttributeValueConstantContext ctx);
	void exitHtmlAttributeValueConstant(JSPParser.HtmlAttributeValueConstantContext ctx);

	void enterDtd(JSPParser.DtdContext ctx);
	void exitDtd(JSPParser.DtdContext ctx);

	void enterJspDirective(JSPParser.JspDirectiveContext ctx);
	void exitJspDirective(JSPParser.JspDirectiveContext ctx);

	void enterJspExpression(JSPParser.JspExpressionContext ctx);
	void exitJspExpression(JSPParser.JspExpressionContext ctx);

	void enterXml(JSPParser.XmlContext ctx);
	void exitXml(JSPParser.XmlContext ctx);

	void enterScriptlet(JSPParser.ScriptletContext ctx);
	void exitScriptlet(JSPParser.ScriptletContext ctx);

	void enterHtmlAttribute(JSPParser.HtmlAttributeContext ctx);
	void exitHtmlAttribute(JSPParser.HtmlAttributeContext ctx);

	void enterPublicId(JSPParser.PublicIdContext ctx);
	void exitPublicId(JSPParser.PublicIdContext ctx);

	void enterHtmlAttributeValue(JSPParser.HtmlAttributeValueContext ctx);
	void exitHtmlAttributeValue(JSPParser.HtmlAttributeValueContext ctx);
}