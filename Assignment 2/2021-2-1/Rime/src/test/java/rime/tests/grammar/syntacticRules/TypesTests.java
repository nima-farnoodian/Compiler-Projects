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
	public void test_primitiveType_void_success() {
		String input = "void";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_primitiveType_bool_success() {
		String input = "bool";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_primitiveType_int_success() {
		String input = "int";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_primitiveType_string_success() {
		String input = "string";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listType_success() {
		String input = "[string]";
		ParseResult result = Autumn.parse(parser.listType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_setType_success() {
		String input = "{int}";
		ParseResult result = Autumn.parse(parser.setType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_dictType_success() {
		String input = "{int:string}";
		ParseResult result = Autumn.parse(parser.dictType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_type_success1() {
		String input = "{string:bool}";
		ParseResult result = Autumn.parse(parser.type, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_type_success2() {
		String input = "[bool]";
		ParseResult result = Autumn.parse(parser.type, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_type_success3() {
		String input = "int";
		ParseResult result = Autumn.parse(parser.type, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_type_failure() {
		String input = "badType101";
		ParseResult result = Autumn.parse(parser.type, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
}
