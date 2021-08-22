package rime.tests.parsing;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.parsing.RimeGrammar;



public final class LiteralsTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	//region -------- NUMERALS
	@Test
	public void test_integerLiteral_positive_success1() {
		String input = "5";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_integerLiteral_positive_success2() {
		String input = "+5";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_integerLiteral_positiveLarge_success() {
		String input = "400000000";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_integerLiteral_negative_success() {
		String input = "-5";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_integerLiteral_negativeLarge_success() {
		String input = "-400000000";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_integerLiteral_startWithZero_failure1() {
		String input = "05";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_integerLiteral_startWithPlusMinus_failure2() {
		String input = "+-5";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_integerLiteral_spaceAfterSign_failure3() {
		String input = "+ 5";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_integerLiteral_nonNumeric_failure4() {
		String input = "abc123";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	//endregion
	
	//region -------- STRINGS
	@Test
	public void test_stringLiteral_success() {
		String input = "\"this is a string\"";
		ParseResult result = Autumn.parse(parser.stringLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_stringLiteral_singleQuotes_failure1() {
		String input = "'this is NOT a string'";
		ParseResult result = Autumn.parse(parser.stringLiteral, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_stringLiteral_mixedQuotes_failure2() {
		String input = "\"this is NOT a string'";
		ParseResult result = Autumn.parse(parser.stringLiteral, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	//endregion
	
	//region -------- BOOLEANS
	@Test
	public void test_booleanLiteral_TRUE_success() {
		String input = "TRUE";
		ParseResult result = Autumn.parse(parser.booleanLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanLiteral_FALSE_success() {
		String input = "FALSE";
		ParseResult result = Autumn.parse(parser.booleanLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanLiteral_wrongCapitalization_failure1() {
		String input = "true";
		ParseResult result = Autumn.parse(parser.booleanLiteral, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_booleanLiteral_wrongCapitalization_failure2() {
		String input = "False";
		ParseResult result = Autumn.parse(parser.booleanLiteral, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_booleanLiteral_failure() {
		String input = "abc123";
		ParseResult result = Autumn.parse(parser.booleanLiteral, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	//endregion
	
	//region -------- LITERAL
	@Test
	public void test_literal_integer_success() {
		String input = "-123456";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_literal_string_success() {
		String input = "\"Aouhfuhfdsq^7465415çà_('&ezarouh'uhf65aez14\"";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_literal_boolean_success() {
		String input = "TRUE";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_literal_null_success() {
		String input = "NULL";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_literal_integer_failure() {
		String input = "0123456";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_literal_string_failure() {
		String input = "'Aouhfuhfdsq^7465415çà_('&ezarouh'uhf65aez14'";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_literal_boolean_failure() {
		String input = "trUE";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_literal_null_failure() {
		String input = "nuLL";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	//endregion
}
