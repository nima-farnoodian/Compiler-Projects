package rime.tests.grammar.syntacticRules;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.grammar.RimeGrammar;



public final class TopLevelTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_entryPoint_singleStatement_success() {
		String input = "proc main(args) {\nval x = args[0]\n}";
		ParseResult result = Autumn.parse(parser.entryPoint, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_entryPoint_multipleStatements_success() {
		String input = "proc main(args) {\nval x = args[0]\nval y = args[1]\n}";
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
	public void test_root_functionBeforeAndAfterMain_multipleStatements_success() {
		String input = "" +
			"func square(x) {\n" +
			"	return x * x\n" +
			"}\n" +
			"\n" +
			"proc main(args) {\n" +
			"	val x = 5\n" +
			"	val xSquared = square(x)\n" +
			"}\n" +
			"\n" +
			"func add(x, y, z) {\n" +
			"	return x + y + z\n" +
			"}\n";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_root_missingMain_failure() {
		String input = "" +
			"func square(x) {\n" +
			"	return x * x\n" +
			"}\n" +
			"\n" +
			"func add(x, y, z) {\n" +
			"	return x + y + z\n" +
			"}\n";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
}
