package rime.tests.ast;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.ast.RimeNode;
import rime.source.ast.constants.BinaryOperator;
import rime.source.ast.constants.UnaryOperator;
import rime.source.ast.constants.VariableKind;
import rime.source.ast.declarations.VariableDefinition;
import rime.source.ast.expressions.*;
import rime.source.ast.expressions.literals.BoolLiteral;
import rime.source.ast.expressions.literals.IntLiteral;
import rime.source.ast.types.PrimitiveType;
import rime.source.ast.types.RimeType;
import rime.source.grammar.RimeGrammar;

import java.util.List;



public class ASTTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	/**
	 * The tests suite for the AST generation is not fully complete yet.
	 * Writing these tests is very time consuming, and we have not found a way
	 * to make it doable in reasonable time.
	 * <p>
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
		Assert.assertEquals("myVariable", identifier.value);
	}
	
	@Test
	public void test_literal() {
		String input = "12345";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		IntLiteral value = result.topValue();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals(12345, (int) value.value);
	}
	
	@Test
	public void test_type() {
		String input = "string";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		PrimitiveType type = result.topValue();
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals("STRING", type.type.name());
	}
	
	@Test
	public void test_indexCollectionAccess() {
		String input = "numbers[25]";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		IndexedCollectionAccess expression = result.topValue();
		Identifier identifier = expression.identifier;
		IntLiteral index = (IntLiteral) expression.index;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals("numbers", identifier.value);
		Assert.assertEquals(25, (int) index.value);
	}
	
	@Test
	public void test_functionCall() {
		String input = "sum(x, 10)";
		ParseResult result = Autumn.parse(parser.functionCall, input, ParseOptions.get());
		FunctionCall functionCall = result.topValue();
		Identifier identifier = functionCall.name;
		List<RimeNode> args = functionCall.arguments;
		Identifier arg1 = (Identifier) args.get(0);
		IntLiteral arg2 = (IntLiteral) args.get(1);
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals("sum", identifier.value);
		Assert.assertEquals("x", arg1.value);
		Assert.assertEquals(10, (int) arg2.value);
	}
	
	@Test
	public void test_multiplication() {
		String input = "x * 15";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		BinaryExpression expression = result.topValue();
		Identifier left = (Identifier) expression.left;
		BinaryOperator op = expression.operator;
		IntLiteral right = (IntLiteral) expression.right;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals("x", left.value);
		Assert.assertEquals(op.name(), BinaryOperator.MULTIPLY.name());
		Assert.assertEquals(15, (int) right.value);
	}
	
	@Test
	public void test_addition() {
		String input = "25 + y";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		BinaryExpression expression = result.topValue();
		IntLiteral left = (IntLiteral) expression.left;
		BinaryOperator op = expression.operator;
		Identifier right = (Identifier) expression.right;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals(25, (int) left.value);
		Assert.assertEquals(op.name(), BinaryOperator.ADD.name());
		Assert.assertEquals("y", right.value);
	}
	
	@Test
	public void test_comparison() {
		String input = "25 > y";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		BinaryExpression expression = result.topValue();
		IntLiteral left = (IntLiteral) expression.left;
		BinaryOperator op = expression.operator;
		Identifier right = (Identifier) expression.right;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals(25, (int) left.value);
		Assert.assertEquals(op.name(), BinaryOperator.GREATER_THAN.name());
		Assert.assertEquals("y", right.value);
	}
	
	@Test
	public void test_equality() {
		String input = "25 == y";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		BinaryExpression expression = result.topValue();
		IntLiteral left = (IntLiteral) expression.left;
		BinaryOperator op = expression.operator;
		Identifier right = (Identifier) expression.right;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals(25, (int) left.value);
		Assert.assertEquals(op.name(), BinaryOperator.EQUAL_TO.name());
		Assert.assertEquals("y", right.value);
	}
	
	@Test
	public void test_booleanAnd() {
		String input = "x && TRUE";
		ParseResult result = Autumn.parse(parser.booleanAndExpression, input, ParseOptions.recordCallStack(true).get());
		BinaryExpression expression = result.topValue();
		Identifier left = (Identifier) expression.left;
		BinaryOperator op = expression.operator;
		BoolLiteral right = (BoolLiteral) expression.right;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals("x", left.value);
		Assert.assertEquals(op.name(), BinaryOperator.LOGICAL_AND.name());
		Assert.assertTrue(right.value);
	}
	
	@Test
	public void test_booleanOr() {
		String input = "x || TRUE";
		ParseResult result = Autumn.parse(parser.booleanOrExpression, input, ParseOptions.recordCallStack(true).get());
		BinaryExpression expression = result.topValue();
		Identifier left = (Identifier) expression.left;
		BinaryOperator op = expression.operator;
		BoolLiteral right = (BoolLiteral) expression.right;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals("x", left.value);
		Assert.assertEquals(op.name(), BinaryOperator.LOGICAL_OR.name());
		Assert.assertTrue(right.value);
	}
	
	@Test
	public void test_unaryMinus() {
		String input = "-numbers[100]";
		ParseResult result = Autumn.parse(parser.unaryMinusExpression, input, ParseOptions.get());
		UnaryExpression expression = result.topValue();
		UnaryOperator op = expression.operator;
		IndexedCollectionAccess operand = (IndexedCollectionAccess) expression.operand;
		Identifier identifier = operand.identifier;
		IntLiteral index = (IntLiteral) operand.index;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals(op.name(), UnaryOperator.NEGATIVE.name());
		Assert.assertEquals("numbers", identifier.value);
		Assert.assertEquals(100, (int) index.value);
	}
	
	@Test
	public void test_unaryNot() {
		String input = "!isTrue";
		ParseResult result = Autumn.parse(parser.booleanNotExpression, input, ParseOptions.get());
		UnaryExpression expression = result.topValue();
		UnaryOperator op = expression.operator;
		Identifier identifier = (Identifier) expression.operand;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals(op.name(), UnaryOperator.LOGICAL_COMPLEMENT.name());
		Assert.assertEquals("isTrue", identifier.value);
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
		IndexedCollectionAccess left = (IndexedCollectionAccess) assignment.left;
		Identifier identifier = left.identifier;
		IntLiteral index = (IntLiteral) left.index;
		BinaryOperator op = assignment.operator;
		IntLiteral right = (IntLiteral) assignment.right;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals("x", identifier.value);
		Assert.assertEquals(0, (int) index.value);
		Assert.assertEquals(op.name(), BinaryOperator.ADD_ASSIGNMENT.name());
		Assert.assertEquals(5, (int) right.value);
	}
	
	@Test
	public void test_variableDefinition() {
		String input = "val int: x = 123";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		VariableDefinition definition = result.topValue();
		VariableKind variableKind = definition.variableKind;
		RimeType type = definition.type;
		Identifier identifier = (Identifier) definition.assignment.left;
		IntLiteral value = (IntLiteral) definition.assignment.right;
		BinaryOperator op = definition.assignment.operator;
		
		Assert.assertTrue(result.fullMatch);
		Assert.assertEquals(variableKind, VariableKind.VAL);
		Assert.assertEquals("int", type.contents());
		Assert.assertEquals("x", identifier.value);
		Assert.assertEquals(op.name(), BinaryOperator.ASSIGNMENT.name());
		Assert.assertEquals(123, (int) value.value);
	}
}
