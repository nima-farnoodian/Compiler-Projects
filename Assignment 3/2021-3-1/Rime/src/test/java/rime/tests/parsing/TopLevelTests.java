package rime.tests.parsing;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.parsing.RimeGrammar;



public final class TopLevelTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_entryPoint_singleStatement_success() {
		String input = """
			proc main([string]: _args_) {
			val string: x = args[0]
			}
			""";
		ParseResult result = Autumn.parse(parser.entryPoint, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_entryPoint_multipleStatements_success() {
		String input = """
			proc main([string]: _args_) {
			val string: x = args[0]
			val string: y = args[1]
			}
			""";
		ParseResult result = Autumn.parse(parser.entryPoint, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_entryPoint_missingArgs_failure() {
		String input = "proc main() {}";
		ParseResult result = Autumn.parse(parser.entryPoint, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_root_functionBeforeMain_multipleStatements_success() {
		String input = """
			func int square(int: x) {
				return x * x
			}
			
			proc main([string]: _args_) {
				val int: x = 5
				val int: xSquared = square(x)
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_root_missingMain_failure() {
		String input = """
			func int square(int: x) {
				return x * x
			}
			
			func int add(int: x, int: y, int: z) {
				return x + y + z
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
}
