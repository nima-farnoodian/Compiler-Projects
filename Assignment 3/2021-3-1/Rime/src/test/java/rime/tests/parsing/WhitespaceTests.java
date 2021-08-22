package rime.tests.parsing;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.parsing.RimeGrammar;



public final class WhitespaceTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_whitespace_success() {
		String input = "  \n   \t\n\t\t\r \n\t\r      ";
		ParseResult result = Autumn.parse(parser.ws, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_whitespace_failure() {
		String input = "\nabc\tde f gh\r";
		ParseResult result = Autumn.parse(parser.ws, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_singleLineComment_success() {
		String input = "//this is a comment";
		ParseResult result = Autumn.parse(parser.lineComment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_singleLineComment_failure() {
		String input = "/ /this is NOT a comment";
		ParseResult result = Autumn.parse(parser.lineComment, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_multiLineComment_success() {
		String input = "/*this \n is \n a \n comment*/";
		ParseResult result = Autumn.parse(parser.multiComment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiLineComment_failure1() {
		String input = "/ *this \n is NOT \n a \n comment*/";
		ParseResult result = Autumn.parse(parser.multiComment, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_multiLineComment_failure2() {
		String input = "/*this \n is NOT \n a \n comment* /";
		ParseResult result = Autumn.parse(parser.multiComment, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_mixed_whitespace_singleLineComment_success() {
		String input = " \n\t  //  \t this is a comment\t\n";
		ParseResult result = Autumn.parse(parser.ws, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_mixed_whitespace_multiLineComment_success() {
		String input = " \n\t  /*  \t th   is\t is \na\n comment\t\n  */";
		ParseResult result = Autumn.parse(parser.ws, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
}
