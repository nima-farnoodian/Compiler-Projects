package rime.tests.ast;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.ast.*;
import rime.source.grammar.RimeGrammar;

import java.util.List;



public class ASTTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	/**
	 * The tests suite for the AST generation is not fully complete yet.
	 * Writing these tests is very time consuming, and we have not found a way
	 * to make it doable in reasonable time.
	 *
	 * However, the fact the the parser is still able to parse all other inputs
	 * successfully is a good indication that, at the very least, the AST can be
	 * built without any errors. Their correctness still remains to be tested properly.
	 */
	
	@Test
	public void test_identifier() {
		String input = "myVariable";
		ParseResult result = Autumn.parse(parser.identifier, input, ParseOptions.get());
		Identifier identifier = result.topValue();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(identifier.name().equals("myVariable"));
	}
	
	@Test
	public void test_literal() {
		String input = "12345";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Literal value = result.topValue();
		IntLiteral intValue = (IntLiteral) value.value();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(intValue.value().equals(12345));
	}
	
	@Test
	public void test_type() {
		String input = "string";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		BasicType type = result.topValue();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(type.name().equals("STRING"));
	}
	
	@Test
	public void test_indexCollectionAccess() {
		String input = "numbers[25]";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		IndexedCollectionAccess expression = result.topValue();
		Identifier identifier = (Identifier) expression.operand();
		Literal literal = (Literal) expression.index();
		IntLiteral index = (IntLiteral) literal.value();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(identifier.name().equals("numbers"));
		Assert.assertTrue(index.value().equals(25));
	}
	
	@Test
	public void test_functionCall() {
		String input = "sum(x, 10)";
		ParseResult result = Autumn.parse(parser.functionCall, input, ParseOptions.get());
		FunctionCall functionCall = result.topValue();
		Identifier identifier = functionCall.name();
		List<Expression> args = functionCall.args();
		Identifier arg1 = (Identifier) args.get(0);
		IntLiteral arg2 = (IntLiteral) ((Literal) args.get(1)).value();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(identifier.name().equals("sum"));
		Assert.assertTrue(arg1.name().equals("x"));
		Assert.assertTrue(arg2.value().equals(10));
	}
	
	@Test
	public void test_multiplication() {
		String input = "x * 15";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		BinaryExpression expression = result.topValue();
		Identifier left = (Identifier) expression.left();
		BinaryOperator op = expression.operator();
		IntLiteral right = (IntLiteral) expression.right();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(left.name().equals("x"));
		Assert.assertTrue(op.name().equals(BinaryOperator.MULTIPLY.name()));
		Assert.assertTrue(right.value().equals(15));
	}
	
	@Test
	public void test_addition() {
		String input = "25 + y";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		BinaryExpression expression = result.topValue();
		IntLiteral left = (IntLiteral) expression.left();
		BinaryOperator op = expression.operator();
		Identifier right = (Identifier) expression.right();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(left.value().equals(25));
		Assert.assertTrue(op.name().equals(BinaryOperator.ADD.name()));
		Assert.assertTrue(right.name().equals("y"));
	}
	
	@Test
	public void test_comparison() {
		String input = "25 > y";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		BinaryExpression expression = result.topValue();
		IntLiteral left = (IntLiteral) expression.left();
		BinaryOperator op = expression.operator();
		Identifier right = (Identifier) expression.right();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(left.value().equals(25));
		Assert.assertTrue(op.name().equals(BinaryOperator.GREATER_THAN.name()));
		Assert.assertTrue(right.name().equals("y"));
	}
	
	@Test
	public void test_equality() {
		String input = "25 == y";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		BinaryExpression expression = result.topValue();
		IntLiteral left = (IntLiteral) expression.left();
		BinaryOperator op = expression.operator();
		Identifier right = (Identifier) expression.right();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(left.value().equals(25));
		Assert.assertTrue(op.name().equals(BinaryOperator.EQUAL_TO.name()));
		Assert.assertTrue(right.name().equals("y"));
	}
	
	@Test
	public void test_booleanAnd() {
		String input = "x && TRUE";
		ParseResult result = Autumn.parse(parser.booleanAndExpression, input, ParseOptions.recordCallStack(true).get());
		BinaryExpression expression = result.topValue();
		Identifier left = (Identifier) expression.left();
		BinaryOperator op = expression.operator();
		BoolLiteral right = (BoolLiteral) ((Literal) expression.right()).value();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(left.name().equals("x"));
		Assert.assertTrue(op.name().equals(BinaryOperator.LOGICAL_AND.name()));
		Assert.assertTrue(right.value().equals(true));
	}
	
	@Test
	public void test_booleanOr() {
		String input = "x || TRUE";
		ParseResult result = Autumn.parse(parser.booleanOrExpression, input, ParseOptions.recordCallStack(true).get());
		BinaryExpression expression = result.topValue();
		Identifier left = (Identifier) expression.left();
		BinaryOperator op = expression.operator();
		BoolLiteral right = (BoolLiteral) ((Literal) expression.right()).value();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(left.name().equals("x"));
		Assert.assertTrue(op.name().equals(BinaryOperator.LOGICAL_OR.name()));
		Assert.assertTrue(right.value().equals(true));
	}
	
	@Test
	public void test_unaryMinus() {
		String input = "-numbers[100]";
		ParseResult result = Autumn.parse(parser.unaryMinusExpression, input, ParseOptions.get());
		UnaryExpression expression = result.topValue();
		UnaryOperator op = expression.operator();
		IndexedCollectionAccess operand = (IndexedCollectionAccess) expression.operand();
		Identifier identifier = (Identifier) operand.operand();
		Literal literal = (Literal) operand.index();
		IntLiteral index = (IntLiteral) literal.value();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(op.name().equals(UnaryOperator.NEGATIVE.name()));
		Assert.assertTrue(identifier.name().equals("numbers"));
		Assert.assertTrue(index.value().equals(100));
	}
	
	@Test
	public void test_unaryNot() {
		String input = "!isTrue";
		ParseResult result = Autumn.parse(parser.booleanNotExpression, input, ParseOptions.get());
		UnaryExpression expression = result.topValue();
		UnaryOperator op = expression.operator();
		Identifier identifier = (Identifier) expression.operand();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(op.name().equals(UnaryOperator.LOGICAL_COMPLEMENT.name()));
		Assert.assertTrue(identifier.name().equals("isTrue"));
	}
	
	@Test
	public void test_expression() {
		/**
		 * Below is the output obtained from valueStack at the end of the successful parse.
		 * It can be easily checked that it matches the expected AST obtained from this expression,
		 * though it may be quite laborious.
		 *
		 * Writing proper tests for longer expressions such as this one will be done later,
		 * as this is rather time consuming.
		 *
		 * 	- first, there is a nested BinaryExpression matching "5 / f(x)"
		 * 	- this BinaryExpression is itself nested inside a BinaryExpression, matching "x + 5 / f(x)"
		 * 	- finally, a BinaryExpression matching the entire expression corresponding to "x + 5 / f(x) - y[25]"
		 *
		 * "BinaryExpression{"
		 * 		"left=BinaryExpression{"
		 * 			"left=Identifier{name=x},"
		 * 			"operator=ADD,"
		 * 			"right=BinaryExpression{"
		 * 				"left=IntLiteral{value=5},"
		 * 				"operator=DIVIDE,"
		 * 				"right=FunctionCall{"
		 * 					"name=Identifier{name=f},"
		 * 					"args=["
		 * 						"Identifier{name=x}"
		 * 					"]"
		 * 				"}"
		 * 			"}"
		 * 		"},"
		 * 		"operator=SUBTRACT,"
		 * 		"right=IndexedCollectionAccess{"
		 * 			"operand=Identifier{name=y},"
		 * 			"index=Literal{"
		 * 				"value=IntLiteral{value=25}"
		 * 			"}"
		 * 		"}"
		 * 	"}"
		 */
		
		String input = "x + 5 / f(x) - y[25]";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_simpleAssignment() {
		String input = "x[0] += 5";
		ParseResult result = Autumn.parse(parser.simpleAssignment, input, ParseOptions.get());
		Assignment assignment = result.topValue();
		IndexedCollectionAccess left = (IndexedCollectionAccess) assignment.left();
		Identifier identifier = (Identifier) left.operand();
		IntLiteral index = (IntLiteral) ((Literal) left.index()).value();
		BinaryOperator op = assignment.operator();
		IntLiteral right = (IntLiteral) ((Literal) assignment.expression()).value();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(identifier.name().equals("x"));
		Assert.assertTrue(index.value().equals(0));
		Assert.assertTrue(op.name().equals(BinaryOperator.ADD_ASSIGNMENT.name()));
		Assert.assertTrue(right.value().equals(5));
	}
	
	@Test
	public void test_variableDeclaration() {
		String input = "val constant";
		ParseResult result = Autumn.parse(parser.variableDeclaration, input, ParseOptions.get());
		VariableDeclaration declaration = result.topValue();
		ValOrVar valOrVar = declaration.valOrVar();
		Identifier identifier = declaration.identifier();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertTrue(valOrVar.equals(ValOrVar.VAL));
		Assert.assertTrue(identifier.name().equals("constant"));
	}
	
	@Test
	public void test_variableDefinition() {
		String input = "val x = 123";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		
		Assert.assertTrue(result.fullMatch);
	}
}
