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



public class SemanticStatementsTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_assignment() {
		String input = """
			proc main([string]: _args_) {
				val string: str = "abcdef"
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
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_assignment_varReassignment() {
		String input = """
			proc main([string]: _args_) {
				var string: str = "abcdef"
				str = "failure"
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
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_assignment_valReassignment_failure() {
		String input = """
			proc main([string]: _args_) {
				val string: str = "abcdef"
				str = "failure"
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
	public void test_assignment_nonMatchingTypes_failure() {
		String input = """
			proc main([string]: _args_) {
				val string: str = 25
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
	public void test_ifStatement() {
		String input = """
			proc main([string]: _args_) {
				var int: x = 25
				if (x > 0) {
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
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_ifElseStatement() {
		String input = """
			proc main([string]: _args_) {
				var int: x = 25
				if (x > 0) {
					print("HIGH")
				}
				else {
					print("LOW")
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
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_whileStatement() {
		String input = """
			proc main([string]: _args_) {
				var int: x = 25
				while (x < 100) {
					x = x + 5
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
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_exitStatement() {
		String input = """
			proc main([string]: _args_) {
				var int: x = 25
				exit
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
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_returnStatement() {
		String input = """
						func int square(int: x) {return x * x}
						
			proc main([string]: _args_) {
				var int: x = square(5)
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
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_passStatement() {
		String input = """
			proc doNothing() {pass}
			
			proc main([string]: _args_) {
				doNothing()
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
		Assert.assertEquals(0, reactor.errors().size());
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
