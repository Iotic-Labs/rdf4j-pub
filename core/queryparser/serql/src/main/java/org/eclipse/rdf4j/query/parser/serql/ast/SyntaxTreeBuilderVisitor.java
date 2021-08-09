/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
/* Generated By:JavaCC: Do not edit this line. SyntaxTreeBuilderVisitor.java Version 5.0 */
package org.eclipse.rdf4j.query.parser.serql.ast;

public interface SyntaxTreeBuilderVisitor {

	Object visit(SimpleNode node, Object data) throws VisitorException;

	Object visit(ASTQueryContainer node, Object data) throws VisitorException;

	Object visit(ASTNamespaceDecl node, Object data) throws VisitorException;

	Object visit(ASTTupleUnion node, Object data) throws VisitorException;

	Object visit(ASTTupleMinus node, Object data) throws VisitorException;

	Object visit(ASTTupleIntersect node, Object data) throws VisitorException;

	Object visit(ASTGraphUnion node, Object data) throws VisitorException;

	Object visit(ASTGraphMinus node, Object data) throws VisitorException;

	Object visit(ASTGraphIntersect node, Object data) throws VisitorException;

	Object visit(ASTSelectQuery node, Object data) throws VisitorException;

	Object visit(ASTSelect node, Object data) throws VisitorException;

	Object visit(ASTProjectionElem node, Object data) throws VisitorException;

	Object visit(ASTConstructQuery node, Object data) throws VisitorException;

	Object visit(ASTConstruct node, Object data) throws VisitorException;

	Object visit(ASTQueryBody node, Object data) throws VisitorException;

	Object visit(ASTFrom node, Object data) throws VisitorException;

	Object visit(ASTWhere node, Object data) throws VisitorException;

	Object visit(ASTOrderBy node, Object data) throws VisitorException;

	Object visit(ASTLimit node, Object data) throws VisitorException;

	Object visit(ASTOffset node, Object data) throws VisitorException;

	Object visit(ASTPathExprList node, Object data) throws VisitorException;

	Object visit(ASTPathExprUnion node, Object data) throws VisitorException;

	Object visit(ASTBasicPathExpr node, Object data) throws VisitorException;

	Object visit(ASTOptPathExpr node, Object data) throws VisitorException;

	Object visit(ASTBasicPathExprTail node, Object data) throws VisitorException;

	Object visit(ASTOptPathExprTail node, Object data) throws VisitorException;

	Object visit(ASTEdge node, Object data) throws VisitorException;

	Object visit(ASTNode node, Object data) throws VisitorException;

	Object visit(ASTNodeElem node, Object data) throws VisitorException;

	Object visit(ASTReifiedStat node, Object data) throws VisitorException;

	Object visit(ASTOrderExpr node, Object data) throws VisitorException;

	Object visit(ASTOr node, Object data) throws VisitorException;

	Object visit(ASTAnd node, Object data) throws VisitorException;

	Object visit(ASTBooleanConstant node, Object data) throws VisitorException;

	Object visit(ASTNot node, Object data) throws VisitorException;

	Object visit(ASTBound node, Object data) throws VisitorException;

	Object visit(ASTSameTerm node, Object data) throws VisitorException;

	Object visit(ASTIsResource node, Object data) throws VisitorException;

	Object visit(ASTIsLiteral node, Object data) throws VisitorException;

	Object visit(ASTIsURI node, Object data) throws VisitorException;

	Object visit(ASTIsBNode node, Object data) throws VisitorException;

	Object visit(ASTLangMatches node, Object data) throws VisitorException;

	Object visit(ASTRegex node, Object data) throws VisitorException;

	Object visit(ASTExists node, Object data) throws VisitorException;

	Object visit(ASTCompare node, Object data) throws VisitorException;

	Object visit(ASTCompareAny node, Object data) throws VisitorException;

	Object visit(ASTCompareAll node, Object data) throws VisitorException;

	Object visit(ASTLike node, Object data) throws VisitorException;

	Object visit(ASTIn node, Object data) throws VisitorException;

	Object visit(ASTInList node, Object data) throws VisitorException;

	Object visit(ASTCompOperator node, Object data) throws VisitorException;

	Object visit(ASTVar node, Object data) throws VisitorException;

	Object visit(ASTDatatype node, Object data) throws VisitorException;

	Object visit(ASTLang node, Object data) throws VisitorException;

	Object visit(ASTLabel node, Object data) throws VisitorException;

	Object visit(ASTNamespace node, Object data) throws VisitorException;

	Object visit(ASTLocalName node, Object data) throws VisitorException;

	Object visit(ASTStr node, Object data) throws VisitorException;

	Object visit(ASTFunctionCall node, Object data) throws VisitorException;

	Object visit(ASTArgList node, Object data) throws VisitorException;

	Object visit(ASTURI node, Object data) throws VisitorException;

	Object visit(ASTQName node, Object data) throws VisitorException;

	Object visit(ASTBNode node, Object data) throws VisitorException;

	Object visit(ASTLiteral node, Object data) throws VisitorException;

	Object visit(ASTString node, Object data) throws VisitorException;

	Object visit(ASTNull node, Object data) throws VisitorException;
}
/* JavaCC - OriginalChecksum=10d665e0123e9d9d5fe36399a555e821 (do not edit this line) */
