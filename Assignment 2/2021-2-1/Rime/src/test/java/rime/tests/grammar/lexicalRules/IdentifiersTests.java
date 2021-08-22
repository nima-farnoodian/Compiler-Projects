package rime.tests.grammar.lexicalRules;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.grammar.RimeGrammar;



public final class IdentifiersTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_identifier_success1() {
		String input = "x";
		ParseResult result = Autumn.parse(parser.identifier, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_identifier_success2() {
		String input = "validIdentifier";
		ParseResult result = Autumn.parse(parser.identifier, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_identifier_success3() {
		String input = "validIdentifier123";
		ParseResult result = Autumn.parse(parser.identifier, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_identifier_success4() {
		String input = "_validIdentifier";
		ParseResult result = Autumn.parse(parser.identifier, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_identifier_startWithNumber_failure1() {
		String input = "0x";
		ParseResult result = Autumn.parse(parser.identifier, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_identifier_onlyNumber_failure2() {
		String input = "5";
		ParseResult result = Autumn.parse(parser.identifier, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_identifier_startWithInvalidCharacter_failure3() {
		String input = "-invalid";
		ParseResult result = Autumn.parse(parser.identifier, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
}
