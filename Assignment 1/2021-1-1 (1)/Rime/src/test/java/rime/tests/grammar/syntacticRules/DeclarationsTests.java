package rime.tests.grammar.syntacticRules;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.grammar.RimeGrammar;



public final class DeclarationsTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_variableDeclaration_success1() {
		String input = "val x";
		ParseResult result = Autumn.parse(parser.variableDeclaration, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDeclaration_success2() {
		String input = "var someVar";
		ParseResult result = Autumn.parse(parser.variableDeclaration, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDeclaration_success3() {
		String input = "val xyz";
		ParseResult result = Autumn.parse(parser.variableDeclaration, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDeclaration_illegalName_failure() {
		String input = "val 123";
		ParseResult result = Autumn.parse(parser.variableDeclaration, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_variableDeclaration_assignment_failure() {
		String input = "val x = 5";
		ParseResult result = Autumn.parse(parser.variableDeclaration, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success1() {
		String input = "val x = 5";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success2() {
		String input = "val numbers = [1, 2, 3]";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success3() {
		String input = "var number = 12";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success4() {
		String input = "var map = map()";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success5() {
		String input = "val set = {7, 8, 9}";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success6() {
		String input = "val str = \"this is a string\"";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success7() {
		String input = "val x = y - 5 + 3 * z";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success8() {
		String input = "val x = [a, b, c, 4]";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success9() {
		String input = "var x = y && 5 < 3 || z";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_success10() {
		String input = "var x = !(5==1 && 5 < y || z) && y==1";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_variableDefinition_noAssignment_failure() {
		String input = "val x";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
}
