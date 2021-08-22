package rime.source.grammar;

import norswap.autumn.Grammar;
import norswap.autumn.actions.StackPush;
import rime.source.ast.*;

import java.util.ArrayList;

import static rime.source.ast.BinaryOperator.*;
import static rime.source.ast.UnaryOperator.*;



public final class RimeGrammar extends Grammar {
	//region -------- LEXICAL RULES
	//region ---- WHITESPACE & COMMENTS
	private static final String LINE_COMMENT = "//";
	private static final String MULTI_COMMENT_START = "/*";
	private static final String MULTI_COMMENT_END = "*/";
	
	public final rule newLine = longest(str("\n"), str("\r"), str("\r\n"));
	
	private final rule spaceCharAndNewLines = cpred(Character::isWhitespace);
	private final rule spaceChar = cpred((c) -> (c == ' ' || c == '\t'));
	private final rule notLine = seq(newLine.not(), any);
	private final rule notMultiCommentEnd = seq(str(MULTI_COMMENT_END).not(), any);
	
	public final rule lineComment = seq(
		LINE_COMMENT,
		notLine.at_least(0),
		str("\n").opt()
	);
	
	public final rule multiComment = seq(
		MULTI_COMMENT_START,
		notMultiCommentEnd.at_least(0),
		MULTI_COMMENT_END
	);
	
	public final rule whitespace = choice(spaceChar, spaceCharAndNewLines, lineComment, multiComment);
	
	{
		ws = whitespace.at_least(0);
	}
	//endregion
	
	//region ---- OPERATORS
	private final rule AMP_AMP = word("&&").as_val(LOGICAL_AND);
	private final rule BANG = str("!").as_val(LOGICAL_COMPLEMENT);
	private final rule BANG_EQ = word("!=").as_val(NOT_EQUAL_TO);
	private final rule BAR_BAR = word("||").as_val(LOGICAL_OR);
	private final rule COLON = word(":");
	private final rule COMMA = word(",");
	private final rule DIV = word("/").as_val(DIVIDE);
	private final rule DIV_EQ = word("/=").as_val(DIVIDE_ASSIGNMENT);
	private final rule EQ = word("=").as_val(ASSIGNMENT);
	private final rule EQ_EQ = word("==").as_val(EQUAL_TO);
	private final rule GT = word(">").as_val(GREATER_THAN);
	private final rule GT_EQ = word(">=").as_val(GREATER_THAN_EQUAL);
	private final rule LT = word("<").as_val(LESS_THAN);
	private final rule LT_EQ = word("<=").as_val(LESS_THAN_EQUAL);
	private final rule L_BRACE = word("{");
	private final rule L_BRACKET = word("[");
	private final rule L_PAREN = word("(");
	private final rule UNARY_MINUS = str("-").as_val(NEGATIVE);
	private final rule MINUS = word("-").as_val(SUBTRACT);
	private final rule MINUS_EQ = word("-=").as_val(SUBTRACT_ASSIGNMENT);
	private final rule MULT = word("*").as_val(MULTIPLY);
	private final rule MULT_EQ = word("*=").as_val(MULTIPLY_ASSIGNMENT);
	private final rule PERCENT = word("%").as_val(REMAINDER);
	private final rule PERCENT_EQ = word("%=").as_val(REMAINDER_ASSIGNMENT);
	private final rule PLUS = word("+").as_val(ADD);
	private final rule PLUS_EQ = word("+=").as_val(ADD_ASSIGNMENT);
	private final rule R_BRACE = word("}");
	private final rule R_BRACKET = word("]");
	private final rule R_PAREN = word(")");
	//endregion
	
	//region ---- IDENTIFIERS
	private final rule id_start = cpred(Utils::isValidIdentifierStart);
	
	{
		id_part = cpred(c -> c != 0 && Utils.isValidIdentifierPart(c));
	}
	
	public final rule identifier = identifier(seq(id_start, id_part.at_least(0)))
		.push($ -> Identifier.create($.str()));
	//endregion
	
	//region ---- LITERALS
	//region -- NUMERALS
	private final rule digits = digit.at_least(1);
	private final rule decimalNum = choice("0", digits);
	
	public final rule integerLiteral = seq(choice("+", "-").opt(), decimalNum).word()
		.push($ -> IntLiteral.create(Integer.parseInt($.str())));
	//endregion
	
	//region -- STRINGS
	private final rule escapeCharSuffix = set("btnfr\"'\\");
	private final rule escapeChar = seq("\\", escapeCharSuffix);
	private final rule rawStrChar = choice(escapeChar, seq(set("\"\\\n\r").not(), any));
	
	public final rule stringLiteral = seq("\"", rawStrChar.at_least(0), "\"").word()
		.push($ -> StringLiteral.create($.str()));
	//endregion
	
	//region -- BOOLEANS
	private final rule kw_trueLiteral = reserved("TRUE");
	private final rule kw_falseLiteral = reserved("FALSE");
	
	public final rule booleanLiteral = choice(kw_trueLiteral, kw_falseLiteral).word()
		.push($ -> BoolLiteral.create(Boolean.parseBoolean($.str())));
	//endregion
	
	//region -- LITERAL
	private final rule kw_nullLiteral = reserved("NULL").word().as_val(NullLiteral.NULL);
	
	public final rule literal = choice(booleanLiteral, integerLiteral, stringLiteral, kw_nullLiteral)
		.push($ -> Literal.create($.$[0]));
	//endregion
	//endregion
	
	//region ---- RESERVED WORDS
	public final rule kw_void = reserved("void");
	public final rule kw_bool = reserved("bool");
	public final rule kw_int = reserved("int");
	public final rule kw_string = reserved("string");
	
	public final rule kw_val = reserved("val");
	public final rule kw_var = reserved("var");
	
	public final rule kw_if = reserved("if");
	public final rule kw_else = reserved("else");
	public final rule kw_while = reserved("while");
	
	public final rule kw_main = reserved("main");
	public final rule kw_proc = reserved("proc");
	public final rule kw_func = reserved("func");
	public final rule kw_exit = reserved("exit");
	public final rule kw_return = reserved("return");
	
	public final rule kw_array = reserved("array");
	public final rule kw_setInit = reserved("set()");
	public final rule kw_mapInit = reserved("map()");
	//endregion
	//endregion
	
	//region -------- SYNTACTIC RULES
	//region ---- TYPES
	public final rule primitiveType = choice(kw_void, kw_bool, kw_int, kw_string).word()
		.push($ -> BasicType.valueOf($.str().trim().toUpperCase()));
	//endregion
	
	//region ---- EXPRESSIONS
	//region -- ARRAY & MAP EXPRESSIONS
	public final rule indexedCollectionAccess = seq(
		identifier,
		L_BRACKET,
		lazy(() -> longest(literal, this.identifierOrIndexedAccessOrFunctionCall)),
		R_BRACKET
	).push($ -> IndexedCollectionAccess.create($.$0(), $.$1()));
	//endregion
	
	//region -- BINARY EXPRESSIONS
	private final StackPush binaryExprPush = $ -> BinaryExpression.create($.$0(), $.$1(), $.$2());
	
	private final rule multiplicationOperators = choice(MULT, DIV, PERCENT);
	private final rule additionOperators = choice(PLUS, MINUS);
	private final rule comparisonOperators = choice(LT_EQ, LT, GT_EQ, GT);
	private final rule equalityOperators = choice(EQ_EQ, BANG_EQ);
	
	public final rule multiplicationExpression = left_expression()
		.operand(
			lazy(() -> longest(
				integerLiteral,
				this.identifierOrIndexedAccessOrFunctionCall
			))
		)
		.infix(multiplicationOperators, binaryExprPush);
	
	public final rule parenAdditionExpression = lazy(() -> seq(L_PAREN, this.additionExpression, R_PAREN));
	
	public final rule additionExpression = choice(
		left_expression()
			.operand(
				lazy(() -> longest(
					multiplicationExpression,
					this.identifierOrIndexedAccessOrFunctionCall
				))
			)
			.infix(additionOperators, binaryExprPush),
		parenAdditionExpression);
	
	public final rule comparisonExpression = seq(additionExpression, comparisonOperators, additionExpression)
		.push(binaryExprPush);
	
	public final rule equalityExpression = choice(
		seq(additionExpression, equalityOperators, additionExpression),
		seq(comparisonExpression, equalityOperators, comparisonExpression)
	).push(binaryExprPush);
	
	public final rule parenEqualityExpression = seq(L_PAREN, equalityExpression, R_PAREN);
	
	public final rule booleanAndExpression = left_expression()
		.operand(
			lazy(() -> longest(
				comparisonExpression,
				equalityExpression,
				parenEqualityExpression,
				this.identifierOrIndexedAccessOrFunctionCall,
				literal
			))
		)
		.infix(AMP_AMP, binaryExprPush);
	
	public final rule booleanOrExpression = left_expression()
		.operand(
			lazy(() -> longest(
				booleanAndExpression,
				this.identifierOrIndexedAccessOrFunctionCall
			))
		)
		.infix(BAR_BAR, binaryExprPush);
	//endregion
	
	//region -- UNARY EXPRESSIONS
	public final rule booleanNotExpression = seq(
		BANG,
		lazy(() -> choice(
			this.identifierOrIndexedAccessOrFunctionCall,
			seq(L_PAREN, booleanOrExpression, R_PAREN)
		))
	).push($ -> UnaryExpression.create($.$0(), $.$1()));
	
	public final rule unaryMinusExpression = seq(
		UNARY_MINUS,
		lazy(() -> choice(
			this.identifierOrIndexedAccessOrFunctionCall,
			seq(L_PAREN, this.arithmeticExpression, R_PAREN)
		))
	).push($ -> UnaryExpression.create($.$0(), $.$1()));
	//endregion
	
	//region -- EXPRESSION
	private final rule arguments = lazy(() -> seq(
		L_PAREN,
		this.expression.sep(0, COMMA),
		R_PAREN
	).as_list(Expression.class));
	
	public final rule functionCall = seq(identifier, arguments)
		.push($ -> FunctionCall.create($.$0(), $.$1()));
	
	public final rule arithmeticExpression = left_expression()
		.operand(choice(additionExpression, unaryMinusExpression))
		.infix(choice(additionOperators, multiplicationOperators), binaryExprPush);
	
	public final rule booleanExpression = left_expression()
		.operand(longest(booleanOrExpression, booleanNotExpression, booleanLiteral))
		.infix(choice(AMP_AMP, BAR_BAR), binaryExprPush);
	
	private final rule identifierOrIndexedAccessOrFunctionCall = longest(identifier, indexedCollectionAccess, functionCall);
	
	public final rule parenExpression = seq(L_PAREN, longest(booleanExpression, arithmeticExpression), R_PAREN);
	public final rule expression = longest(booleanExpression, arithmeticExpression, parenExpression, functionCall, literal);
	//endregion
	
	//region -- ASSIGNMENT EXPRESSIONS
	private final rule assignmentOperators = choice(EQ, PLUS_EQ, MINUS_EQ, MULT_EQ, DIV_EQ, PERCENT_EQ);
	
	private final rule element = choice(identifier, literal, functionCall);
	private final rule mapElement = seq(element, COLON, element)
		.push($ -> MapElement.create($.$0(), $.$1()));
	
	private final rule arrayElements = element.sep(1, COMMA)
		.push($ -> ArrayElements.create($.$list()));
	
	private final rule setElements = element.sep(1, COMMA)
		.push($ -> SetElements.create($.$list()));
	
	private final rule mapElements = mapElement.sep(1, COMMA)
		.push($ -> MapElements.create($.$list()));
	
	private final rule emptyArray = seq(L_BRACKET, R_BRACKET)
		.push($ -> ArrayElements.create(new ArrayList<>()));
	
	private final rule emptySet = seq(kw_setInit)
		.push($ -> SetElements.create(new ArrayList<>()));
	
	private final rule emptyMap = seq(choice(seq(L_BRACE, R_BRACE), kw_mapInit))
		.push($ -> MapElements.create(new ArrayList<>()));
	
	private final rule arraySizeExpression = seq(
		kw_array,
		L_PAREN,
		longest(integerLiteral, identifierOrIndexedAccessOrFunctionCall).push($ -> Dimension.create($.$0())),
		R_PAREN
	).push($ -> ArraySizeInitialization.create($.$0()));
	
	private final rule arrayInitialization = seq(L_BRACKET, arrayElements, R_BRACKET);
	private final rule setInitialization = seq(L_BRACE, setElements, R_BRACE);
	private final rule mapInitialization = seq(L_BRACE, mapElements, R_BRACE);
	
	public final rule simpleAssignment = seq(
		longest(identifier, indexedCollectionAccess),
		assignmentOperators,
		expression
	).push($ -> Assignment.create($.$0(), $.$1(), $.$2()));
	
	public final rule arrayAssignment = seq(identifier, EQ, choice(emptyArray, arrayInitialization, arraySizeExpression))
		.push($ -> Assignment.create($.$0(), $.$1(), $.$2()));
	
	public final rule setAssignment = seq(identifier, EQ, choice(emptySet, setInitialization))
		.push($ -> Assignment.create($.$0(), $.$1(), $.$2()));
	
	public final rule mapAssignment = seq(identifier, EQ, choice(emptyMap, mapInitialization))
		.push($ -> Assignment.create($.$0(), $.$1(), $.$2()));
	
	public final rule assignment = choice(simpleAssignment, arrayAssignment, setAssignment, mapAssignment);
	//endregion
	//endregion
	
	//region ---- DECLARATIONS
	private final rule simpleEqAssignment = seq(identifier, EQ, expression)
		.push($ -> Assignment.create($.$0(), $.$1(), $.$2()));
	
	private final rule varOrVal = choice(
		kw_val.as_val(ValOrVar.VAL),
		kw_var.as_val(ValOrVar.VAR)
	);
	
	public final rule variableDeclaration = seq(varOrVal, identifier)
		.push($ -> VariableDeclaration.create($.$0(), $.$1()));
	
	public final rule variableDefinition = seq(
		varOrVal,
		choice(
			simpleEqAssignment,
			arrayAssignment,
			setAssignment,
			mapAssignment
		)
	).push($ -> VariableDefinition.create($.$0(), $.$1()));
	//endregion
	
	//region ---- STATEMENTS
	//region -- CONTROL STATEMENTS
	private final rule ifCondition = seq(kw_if, L_PAREN, booleanExpression, R_PAREN);
	private final rule whileCondition = seq(kw_while, L_PAREN, booleanExpression, R_PAREN);
	
	public final rule ifStatement = lazy(() -> seq(
		ifCondition,
		L_BRACE,
		this.statement,
		R_BRACE,
		this.elseIfStatement.opt(),
		this.elseStatement.opt()
	)).push($ -> IfStatement.create($.$0(), $.$1()));
	
	public final rule elseIfStatement = seq(kw_else, ifStatement);
	
	public final rule elseStatement = lazy(() -> seq(
		kw_else,
		L_BRACE,
		this.statement,
		R_BRACE
	)).push($ -> ElseStatement.create($.$0()));
	
	public final rule whileStatement = lazy(() -> seq(
		whileCondition,
		L_BRACE,
		this.statement,
		R_BRACE
	)).push($ -> WhileStatement.create($.$0(), $.$1()));
	
	public final rule controlStatement = choice(ifStatement, whileStatement);
	
	//endregion
	
	//region -- FUNCTIONS
	private final rule parameters = seq(L_PAREN, identifier.sep(0, COMMA), R_PAREN)
		.push($ -> Parameters.create($.$list()));
	
	private final rule exitStatement = kw_exit.
		push($ -> ExitStatement.create());
	
	private final rule returnStatement = seq(kw_return, expression)
		.push($ -> ReturnStatement.create($.$0()));
	
	public final rule functionDefinition = lazy(() -> seq(
		choice(kw_proc.as_val(ProcOrFunc.PROC), kw_func.as_val(ProcOrFunc.FUNC)),
		identifier,
		parameters,
		L_BRACE,
		this.statement,
		R_BRACE
	)).push($ -> FunctionDefinition.create($.$0(), $.$1(), $.$2(), $.$3()));
	
	//endregion
	
	//region -- STATEMENT
	private final rule nonControlStatement = choice(variableDefinition, variableDeclaration, assignment, functionCall);
	
	public final rule statement =
		longest(
			controlStatement,
			nonControlStatement,
			exitStatement,
			returnStatement
		).at_least(1).as_list(Statement.class);
	
	//endregion
	//endregion
	//endregion
	
	//region -------- TOP-LEVEL RULES
	private final rule functionDefinitions = functionDefinition.at_least(0).as_list(FunctionDefinition.class);
	
	public final rule entryPoint = seq(
		kw_proc,
		kw_main,
		seq(L_PAREN, str("args"), R_PAREN),
		L_BRACE,
		this.statement,
		R_BRACE
	).push($ -> EntryPoint.create($.$0()));
	
	public final rule root = seq(functionDefinitions, entryPoint, functionDefinitions);
	
	@Override
	public final rule root() { return root; }
	//endregion
}
