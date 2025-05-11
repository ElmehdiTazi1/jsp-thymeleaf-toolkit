/*
 * JSP-Thymeleaf-Toolkit - Outils pour la conversion de JSP vers Thymeleaf
 * Copyright (c) 2023 Cybernostics Pty Ltd
 */
package com.cybernostics.jsp2thymeleaf.api.expressions;

import java.util.List;
import org.apache.commons.el.BinaryOperator;
import org.apache.commons.el.BinaryOperatorExpression;
import org.apache.commons.el.ComplexValue;
import org.apache.commons.el.ConditionalExpression;
import org.apache.commons.el.Expression;
import org.apache.commons.el.ExpressionString;
import org.apache.commons.el.FunctionInvocation;
import org.apache.commons.el.Literal;
import org.apache.commons.el.NamedValue;
import org.apache.commons.el.UnaryOperatorExpression;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Interface de visiteur pour parcourir et traiter les expressions JSP.
 * 
 * Cette interface implémente le pattern Visitor pour permettre le traitement
 * de différents types d'expressions JSP (EL - Expression Language) sans modifier
 * les classes d'expression elles-mêmes.
 * 
 * Les implémentations de cette interface peuvent définir des comportements spécifiques
 * pour chaque type d'expression rencontré lors de la traversée de l'arbre d'expressions.
 * Cela permet de transformer les expressions JSP EL en expressions Thymeleaf équivalentes
 * ou d'effectuer d'autres opérations sur ces expressions.
 * 
 * Cette interface est utilisée en conjonction avec {@link ExpressionWalker} qui
 * gère la traversée proprement dite de l'arbre d'expressions.
 *
 * @author jason
 * @version 1.0
 * @see ExpressionWalker
 * @see DefaultExpressionVisitor
 * @see ExpressionWritingVisitor
 */
public interface ExpressionVisitor
{
    /**
     * Visite un objet d'expression générique.
     * 
     * Cette méthode est le point d'entrée pour visiter un objet d'expression dont
     * le type exact n'est pas connu à l'avance. Elle détermine le type réel de l'objet
     * et délègue à la méthode de visite spécifique appropriée.
     * 
     * @param o L'objet d'expression à visiter
     */
    void visit(Object o);

    /**
     * Visite une expression JSP générique.
     * 
     * @param o L'expression à visiter
     */
    void visitExpression(Expression o);

    /**
     * Visite une chaîne d'expression complexe pouvant contenir plusieurs expressions.
     * 
     * @param o L'objet ExpressionString à visiter
     */
    void visitExpressionString(ExpressionString o);

    /**
     * Visite une chaîne de caractères simple (non-expression).
     * 
     * @param o La chaîne de caractères à visiter
     */
    void visitString(String o);

    /**
     * Visite une expression contenant un opérateur binaire (comme +, -, *, /, etc.).
     * 
     * @param o L'expression d'opérateur binaire à visiter
     */
    void visitBinaryOperatorExpression(BinaryOperatorExpression o);

    /**
     * Visite un opérateur binaire spécifique.
     * 
     * @param binaryOperator L'opérateur binaire à visiter
     */
    void visitBinaryOperator(BinaryOperator binaryOperator);

    /**
     * Visite une invocation de fonction dans une expression.
     * 
     * @param functionInvocation L'invocation de fonction à visiter
     */
    void visitFunctionInvocation(FunctionInvocation functionInvocation);

    /**
     * Visite une expression de valeur complexe (comme obj.prop1.prop2).
     * 
     * @param complexValue L'expression de valeur complexe à visiter
     */
    void visitComplexValueExpression(ComplexValue complexValue);

    /**
     * Visite une expression conditionnelle (condition ? valSiVrai : valSiFaux).
     * 
     * @param conditionalExpression L'expression conditionnelle à visiter
     */
    void visitConditionalExpression(ConditionalExpression conditionalExpression);

    /**
     * Visite une expression littérale (valeur constante).
     * 
     * @param literal L'expression littérale à visiter
     */
    void visitLiteralExpression(Literal literal);

    /**
     * Visite une valeur nommée (variable ou propriété).
     * 
     * @param namedValue La valeur nommée à visiter
     */
    void visitNamedValue(NamedValue namedValue);

    /**
     * Visite le début d'une expression d'opérateur binaire.
     * Utile pour les visiteurs qui doivent effectuer des actions spécifiques
     * avant de traiter les opérandes.
     * 
     * @param binaryOperatorExpression L'expression d'opérateur binaire à visiter
     */
    void visitBinaryOperatorExpressionStart(BinaryOperatorExpression binaryOperatorExpression);

    void visitBinaryOperatorSubExpression(Expression expression, ExpressionVisitor v);

    void visitBinaryOperatorExpressionEnd(BinaryOperatorExpression binaryOperatorExpression);

    void visitFunctionInvocationStart(FunctionInvocation functionInvocation);

    void visitFunctionInvocationEnd(FunctionInvocation functionInvocation);

    List<Expression> getFunctionArgumentList(FunctionInvocation functionInvocation);

    List<Pair<? extends BinaryOperator, ? extends Expression>> getBinaryOperatorOperatorExpressionPairs(BinaryOperatorExpression binaryOperatorExpression);

    String getConvertsMethodName();

    void visitParamDivider(Expression e, ExpressionVisitor v);

    void visitUnaryOperator(UnaryOperatorExpression unaryOperatorExpression);

}
