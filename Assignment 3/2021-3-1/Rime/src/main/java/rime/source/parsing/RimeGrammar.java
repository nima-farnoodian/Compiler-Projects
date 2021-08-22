package rime.source.parsing;

import norswap.autumn.Grammar;
import norswap.autumn.actions.StackPush;
import rime.source.ast.constants.BasicType;
import rime.source.ast.constants.VariableKind;
import rime.source.ast.declarations.*;
import rime.source.ast.expressions.literals.*;
import rime.source.ast.statements.*;
import rime.source.ast.expressions.*;
import rime.source.ast.types.*;

import static rime.source.ast.constants.BinaryOperator.*;
import static rime.source.ast.constants.UnaryOperator.*;



public final class RimeGrammar extends Grammar {
	public static final String MAIN_ARGS = "_args_";
	
	//region -------- LEXICAL RULES
	//region ---- WHITESPACE & COMMENTS
	private static final String LINE_COMMENT = "//";
	private static final String MULTI_COMMENT_START = "/*";
	private static final String MULTI_COMMENT_END = "*/";
	
	private final rule newLine = longest(str("\n"), str("\r"), str("\r\n"));
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
	private final rule MULT = word("*").as_val(MULTIPLY);
	private final rule PERCENT = word("%").as_val(REMAINDER);
	private final rule PLUS = word("+").as_val(ADD);
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
		.push($ -> new Identifier($.str()));
	//endregion
	
	//region ---- LITERALS
	//region -- NUMERALS
	private final rule digits = digit.at_least(1);
	private final rule decimalNum = choice("0", digits);
	
	public final rule integerLiteral = seq(choice("+", "-").opt(), decimalNum).word()
		.push($ -> new IntLiteral(Integer.parseInt($.str())));
	//endregion
	
	//region -- STRINGS
	private final rule escapeCharSuffix = set("btnfr\"'\\");
	private final rule escapeChar = seq("\\", escapeCharSuffix);
	private final rule rawStrChar = choice(escapeChar, seq(set("\"\\\n\r").not(), any));
	
	public final rule stringLiteral = seq("\"", rawStrChar.at_least(0), "\"").word()
		.push($ -> new StringLiteral($.str()));
	//endregion
	
	//region -- BOOLEANS
	private final rule kw_trueLiteral = reserved("TRUE");
	private final rule kw_falseLiteral = reserved("FALSE");
	
	public final rule booleanLiteral = choice(kw_trueLiteral, kw_falseLiteral).word()
		.push($ -> new BoolLiteral(Boolean.parseBoolean($.str())));
	//endregion
	
	//region -- LITERAL
	private final rule kw_nullLiteral = reserved("NULL").word().as_val(NullLiteral.NULL);
	
	public final rule literal = choice(booleanLiteral, integerLiteral, stringLiteral, kw_nullLiteral);
	//endregion
	//endregion
	
	//region ---- RESERVED WORDS
	private final rule kw_void = reserved("void");
	private final rule kw_bool = reserved("bool");
	private final rule kw_int = reserved("int");
	private final rule kw_string = reserved("string");
	
	private final rule kw_val = reserved("val");
	private final rule kw_var = reserved("var");
	
	private final rule kw_pass = reserved("pass");
	private final rule kw_if = reserved("if");
	private final rule kw_else = reserved("else");
	private final rule kw_while = reserved("while");
	
	private final rule kw_main = reserved("main");
	private final rule kw_proc = reserved("proc");
	private final rule kw_func = reserved("func");
	private final rule kw_exit = reserved("exit");
	private final rule kw_return = reserved("return");
	//endregion
	//endregion
	
	//region -------- SYNTACTIC RULES
	//region ---- TYPES
	public final rule primitiveType = choice(kw_void, kw_bool, kw_int, kw_string).word()
		.push($ -> new PrimitiveType(BasicType.valueOf($.str().trim().toUpperCase())));
	
	public final rule listType = seq(L_BRACKET, primitiveType, R_BRACKET)
		.push($ -> new ListTypeNode($.$0()));
	
	public final rule setType = seq(L_BRACE, primitiveType, R_BRACE)
		.push($ -> new SetTypeNode($.$0()));
	
	public final rule dictType = seq(L_BRACE, primitiveType, COLON, primitiveType, R_BRACE)
		.push($ -> new DictTypeNode($.$0(), $.$1()));
	
	public final rule type = longest(primitiveType, listType, setType, dictType);
	//endregion
	
	//region ---- EXPRESSIONS
	//region -- LIST & DICT EXPRESSIONS
	public final rule indexedCollectionAccess = seq(
		identifier,
		L_BRACKET,
		lazy(() -> this.expression),
		R_BRACKET
	).push($ -> new IndexedCollectionAccess($.$0(), $.$1()));
	//endregion
	
	//region -- BINARY EXPRESSIONS
	private final StackPush binaryExprPush = $ -> new BinaryExpression($.$0(), $.$1(), $.$2());
	
	private final rule multiplicationOperator = choice(MULT, DIV, PERCENT);
	private final rule additionOperator = choice(PLUS, MINUS);
	private final rule comparisonOperators = choice(LT_EQ, LT, GT_EQ, GT);
	private final rule equalityOperators = choice(EQ_EQ, BANG_EQ);
	
	public final rule multiplicationExpression = left_expression()
		.operand(
			lazy(() -> longest(
				literal,
				this.identifierOrIndexedAccessOrFunctionCall
			))
		)
		.infix(multiplicationOperator, binaryExprPush);
	
	public final rule parenAdditionExpression = lazy(() -> seq(L_PAREN, this.additionExpression, R_PAREN));
	
	public final rule additionExpression = choice(
		left_expression()
			.operand(
				lazy(() -> longest(
					multiplicationExpression,
					this.identifierOrIndexedAccessOrFunctionCall
				))
			)
			.infix(additionOperator, binaryExprPush),
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
			literal,
			this.identifierOrIndexedAccessOrFunctionCall,
			seq(L_PAREN, booleanOrExpression, R_PAREN)
		))
	).push($ -> new UnaryExpression($.$0(), $.$1()));
	
	public final rule unaryMinusExpression = seq(
		UNARY_MINUS,
		lazy(() -> choice(
			literal,
			this.identifierOrIndexedAccessOrFunctionCall,
			seq(L_PAREN, this.arithmeticExpression, R_PAREN)
		))
	).push($ -> new UnaryExpression($.$0(), $.$1()));
	//endregion
	
	//region -- EXPRESSION
	private final rule arguments = lazy(() -> seq(
		L_PAREN,
		this.expression.sep(0, COMMA),
		R_PAREN
	).as_list(Expression.class));
	
	public final rule functionCall = seq(identifier, arguments)
		.push($ -> new FunctionCall($.$0(), $.$1()));
	
	public final rule arithmeticExpression = left_expression()
		.operand(choice(additionExpression, unaryMinusExpression))
		.infix(choice(additionOperator, multiplicationOperator), binaryExprPush);
	
	public final rule booleanExpression = left_expression()
		.operand(longest(booleanOrExpression, booleanNotExpression, booleanLiteral))
		.infix(choice(AMP_AMP, BAR_BAR), binaryExprPush);
	
	private final rule identifierOrIndexedAccessOrFunctionCall = longest(identifier, indexedCollectionAccess, functionCall);
	
	public final rule parenExpression = seq(L_PAREN, longest(booleanExpression, arithmeticExpression), R_PAREN);
	public final rule expression =
		longest(booleanExpression, arithmeticExpression, parenExpression, functionCall, literal, primitiveType);
	//endregion
	
	//region -- ASSIGNMENTS
	private final rule assignmentOperator = EQ;
	
	private final rule element = choice(identifier, literal, functionCall);
	private final rule dictElement = seq(element, COLON, element)
		.push($ -> new DictElement($.$0(), $.$1()));
	
	private final rule listElements = element.sep(1, COMMA)
		.push($ -> new ListElements($.$list()));
	
	private final rule setElements = element.sep(1, COMMA)
		.push($ -> new SetElements($.$list()));
	
	private final rule dictElements = dictElement.sep(1, COMMA)
		.push($ -> new DictElements($.$list()));
	
	private final rule emptyList = seq(L_BRACKET, primitiveType, R_BRACKET).push($ -> new EmptyList($.$0()));
	
	private final rule emptySet = seq(L_BRACE, primitiveType, R_BRACE).push($ -> new EmptySet($.$0()));
	
	private final rule emptyDict = seq(L_BRACE, primitiveType, COLON, primitiveType, R_BRACE).push($ -> new EmptyDict($.$0(), $.$1()));
	
	private final rule listInitialization = seq(L_BRACKET, listElements, R_BRACKET);
	private final rule setInitialization = seq(L_BRACE, setElements, R_BRACE);
	private final rule dictInitialization = seq(L_BRACE, dictElements, R_BRACE);
	
	public final rule simpleAssignment = seq(
		longest(identifier, indexedCollectionAccess),
		assignmentOperator,
		expression
	).push($ -> new Assignment($.$0(), $.$1(), $.$2()));
	
	public final rule listAssignment = seq(identifier, EQ, choice(emptyList, listInitialization))
		.push($ -> new Assignment($.$0(), $.$1(), $.$2()));
	
	public final rule setAssignment = seq(identifier, EQ, choice(emptySet, setInitialization))
		.push($ -> new Assignment($.$0(), $.$1(), $.$2()));
	
	public final rule dictAssignment = seq(identifier, EQ, choice(emptyDict, dictInitialization))
		.push($ -> new Assignment($.$0(), $.$1(), $.$2()));
	
	public final rule assignment = choice(simpleAssignment, listAssignment, setAssignment, dictAssignment);
	//endregion
	//endregion
	
	//region ---- DECLARATIONS
	//region -- FUNCTIONS
	private final rule parameter = seq(type, COLON, identifier)
		.push($ -> new Parameter($.$0(), $.$1()));
	
	private final rule parameters = seq(L_PAREN, parameter.sep(0, COMMA), R_PAREN)
		.push($ -> new Parameters($.$list()));
	
	private final rule exitStatement = kw_exit.
		push($ -> new ExitStatement());
	
	private final rule returnStatement = seq(kw_return, expression)
		.push($ -> new ReturnStatement($.$0()));
	
	public final rule procDefinition = lazy(() -> seq(
		kw_proc,
		identifier,
		parameters,
		L_BRACE,
		this.statement,
		R_BRACE
	)).push($ -> new ProcDefinition($.$0(), $.$1(), $.$2()));
	
	public final rule funcDefinition = lazy(() -> seq(
		kw_func,
		type,
		identifier,
		parameters,
		L_BRACE,
		this.statement,
		R_BRACE
	)).push($ -> new FuncDefinition($.$0(), $.$1(), $.$2(), $.$3()));
	
	public final rule functionDefinition = choice(procDefinition, funcDefinition);
	//endregion
	
	//region -- VARIABLES
	private final rule simpleEqAssignment = seq(identifier, EQ, expression)
		.push($ -> new Assignment($.$0(), $.$1(), $.$2()));
	
	private final rule variableType = choice(
		kw_val.as_val(VariableKind.VAL),
		kw_var.as_val(VariableKind.VAR)
	);
	
	public final rule variableDefinition = seq(
		seq(variableType, type, COLON),
		choice(
			simpleEqAssignment,
			listAssignment,
			setAssignment,
			dictAssignment
		)
	).push($ -> new VariableDefinition($.$0(), $.$1(), $.$2()));
	//endregion
	//endregion
	
	//region ---- STATEMENTS
	//region -- CONTROL STATEMENTS
	private final rule condition = seq(L_PAREN, expression, R_PAREN);
	
	private final rule expressionStatement = expression
		.filter($ -> {
			if ($.$[0] instanceof FunctionCall) {
				$.push(new ExpressionStatement($.$[0]));
				return true;
			}
			else {
				return false;
			}
		});
	
	public final rule emptyStatement = kw_pass.push($ -> new EmptyStatement());
	
	public final rule ifStatement = lazy(() -> seq(
		kw_if,
		condition,
		L_BRACE,
		this.statement,
		R_BRACE,
		seq(kw_else, L_BRACE, this.statement, R_BRACE).or_push_null()
	)).push($ -> new IfStatement($.$0(), $.$1(), $.$2()));
	
	public final rule whileStatement = lazy(() -> seq(
		kw_while,
		condition,
		L_BRACE,
		this.statement,
		R_BRACE
	)).push($ -> new WhileStatement($.$0(), $.$1()));
	
	private final rule controlStatement = choice(ifStatement, whileStatement);
	
	//endregion
	
	//region -- STATEMENT
	private final rule nonControlStatement = choice(variableDefinition, assignment, functionCall);
	
	public final rule statement =
		longest(
			emptyStatement,
			expressionStatement,
			controlStatement,
			nonControlStatement,
			exitStatement,
			returnStatement
		).at_least(1).as_list(Statement.class).push($ -> new Block($.$0()));
	
	//endregion
	//endregion
	//endregion
	
	//region -------- TOP-LEVEL RULES
	private final rule functionDefinitions = functionDefinition.at_least(0).as_list(FunctionDefinition.class);
	
	public final rule entryPoint = seq(
		kw_proc,
		kw_main,
		seq(L_PAREN, str("[string]: " + MAIN_ARGS), R_PAREN),
		L_BRACE,
		statement,
		R_BRACE
	).push($ -> new EntryPoint($.$0()));
	
	public final rule root = seq(ws, functionDefinitions, entryPoint)
		.push($ -> new RootNode($.$0(), $.$1()));
	
	@Override
	public final rule root() { return root; }
	//endregion
}
