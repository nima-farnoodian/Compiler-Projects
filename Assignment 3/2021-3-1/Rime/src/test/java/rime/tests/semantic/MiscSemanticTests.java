package rime.tests.semantic;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import norswap.uranium.Reactor;
import norswap.uranium.SemanticError;
import norswap.utils.visitors.Walker;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.ast.RimeNode;
import rime.source.parsing.RimeGrammar;
import rime.source.semantic.SemanticAnalysis;

import java.util.Set;

import static norswap.utils.Util.cast;



public class MiscSemanticTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_undefinedVariable_failure() {
		String input = """
			proc main([string]: _args_) {
				var int: x = y
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_listInitialization_mismatchedTypes_failure() {
		String input = """
			proc main([string]: _args_) {
				val [int]: numbers = [1, 2, 3, 4, "5"]
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_setInitialization_mismatchedTypes_failure() {
		String input = """
			proc main([string]: _args_) {
				val {int}: numbers = {1, 2, 3, 4, "5"}
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_dictInitialization_mismatchedTypes_failure() {
		String input = """
			proc main([string]: _args_) {
				val {int:string}: x = {1: "a", 2: "a", 3: "a", 4: "a", 5: 25}
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_functionCall_badNumberOfArgs_failure() {
		String input = """
			proc main([string]: _args_) {
				print("hello", "world")
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_if_nonBooleanCondition_failure() {
		String input = """
			proc main([string]: _args_) {
				if (5 + 5) {
					print("Hello")
				}
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_while_nonBooleanCondition_failure() {
		String input = """
			proc main([string]: _args_) {
				while (5 + 5) {
					print("Hello")
				}
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_exit_insideFunc_failure() {
		String input = """
						func int f() {exit}
						
			proc main([string]: _args_) {
				print("Hello")
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(2, reactor.errors().size());
	}
	
	@Test
	public void test_return_insideProc_failure() {
		String input = """
						proc f() {return 5}
						
			proc main([string]: _args_) {
				print("Hello")
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_reassignFunctionParameter_failure() {
		String input = """
			func int f(int: x) {
				x = 15
				return x
			}
			
			proc main([string]: _args_) {
				print("Hello")
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_return_failure() {
		String input = """
						func int f() {return "abc"}
						
			proc main([string]: _args_) {
				print("Hello")
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_binaryArithmetic_mismatchedTypes_failure() {
		String input = """
			proc main([string]: _args_) {
				print(25 + TRUE)
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_binaryComparison_mismatchedTypes_failure() {
		String input = """
			proc main([string]: _args_) {
				print(TRUE + 25)
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	@Test
	public void test_binaryEquality_mismatchedTypes_failure() {
		String input = """
			proc main([string]: _args_) {
				print(25 == "25")
			}
			""";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
	}
	
	private static void printErrors(Set<SemanticError> errors, String testName) {
		if (!errors.isEmpty()) {
			System.out.println("\n-------- " + testName + " --------");
			for (SemanticError e : errors) {
				System.out.println("\tERROR: " + e);
			}
			System.out.println("-------- " + testName + " --------\n");
		}
	}
}
