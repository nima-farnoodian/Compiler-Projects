package rime.tests.grammar.syntacticRules;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.grammar.RimeGrammar;



public final class TypesTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_type_void_success() {
		String input = "void";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_type_bool_success() {
		String input = "bool";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_type_int_success() {
		String input = "int";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_type_string_success() {
		String input = "string";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_type_failure() {
		String input = "badType101";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
}
