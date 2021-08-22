package rime.tests.grammar.syntacticRules;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.grammar.RimeGrammar;



public final class StatementsTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	//region ---- CONTROL STATEMENTS
	@Test
	public void test_if_singleLine_success() {
		String input = "if (x && y < 21 || !z) {res = 1}";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_if_singleStatement_success() {
		String input = "if (x || y >= 5 && !z) {\nval res = 1\n}";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_if_multipleStatements_success() {
		String input = "if (x || y >= 5 && !z) {\nval res = 1\n\nnumbers=[]\nmap = {1:1, 2:2}\n}";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_if_nested_success() {
		String input = "if (x || y >= 5 && !z) {\nif (!x && y + z == 5) {\nval nested = 123\n}\n}";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_if_emptyCondition_failure() {
		String input = "if () {x = 5}";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_ifElse_singleLine_success() {
		String input = "if (z == 123) {x = 5} else {x = 10}";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_ifElse_multipleStatements_success() {
		String input = "if (z == 123) {\nval x = 1\nvar y = x + 5\n} else {\nval z = 10\n}";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_ifElse_nested_success() {
		String input = "if (z == 123) {\nval x = 1\nvar y = x + 5\n} else {\nif (z == 123) {x += 5}\n}";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_ifElseElse_failure() {
		String input = "if (z == 123) {x=1} else {x=2} else {x=3}";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_while_singleLine_success() {
		String input = "while (x && y < 21 || !z) {res = 1}";
		ParseResult result = Autumn.parse(parser.whileStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_while_singleStatement_success() {
		String input = "while (x || y >= 5 && !z) {\nval res = 1\n}";
		ParseResult result = Autumn.parse(parser.whileStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_while_multipleStatements_success() {
		String input = "while (x || y >= 5 && !z) {\nval res = 1\n\nnumbers=[]\nmap = {1:1, 2:2}\n}";
		ParseResult result = Autumn.parse(parser.whileStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_while_nested_success() {
		String input = "while (x || y >= 5 && !z) {\nwhile (!x && y + z == 5) {\nval nested = 123\n}\n}";
		ParseResult result = Autumn.parse(parser.whileStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_while_emptyCondition_failure() {
		String input = "while () {x = 5}";
		ParseResult result = Autumn.parse(parser.whileStatement, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	//endregion
	
	//region ---- FUNCTIONS
	@Test
	public void test_proc_singleStatement_success() {
		String input = "proc f(x) {val y = x}";
		ParseResult result = Autumn.parse(parser.functionDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_proc_multipleArgumentsAndStatements_success() {
		String input = "proc f(x, y, z) {\nval a = x + y\nvar b = [x, y, z]}";
		ParseResult result = Autumn.parse(parser.functionDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_proc_longIdentifier_multipleArgumentsAndStatements_success() {
		String input = "proc someProc(x, y, z) {\nval a = x + y\nvar b = [x, y, z]}";
		ParseResult result = Autumn.parse(parser.functionDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_func_singleStatement_success() {
		String input = "func f(x) {return 5+5}";
		ParseResult result = Autumn.parse(parser.functionDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_func_multipleArguments_success() {
		String input = "func add(x, y) {return x + y}";
		ParseResult result = Autumn.parse(parser.functionDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_func_longIdentifier_multipleArgumentsAndStatements_success() {
		String input = "func product(x, y, z) {\nreturn 5\nval res = x*y*z\nreturn res\n}";
		ParseResult result = Autumn.parse(parser.functionDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	//endregion
}
