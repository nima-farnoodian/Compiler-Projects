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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static norswap.utils.Util.cast;



public final class ExampleProgramsTests {
	private static final String dir = "src/main/java/rime/examples/rime/";
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_fibonacciProgram_semanticAnalysis() throws IOException {
		String input = Files.readString(Paths.get(dir, "Fibonacci.rime"));
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.recordCallStack(true).get());
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
	public void test_fizzBuzzProgram_semanticAnalysis() throws IOException {
		String input = Files.readString(Paths.get(dir, "FizzBuzz.rime"));
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
	public void test_primeProgram_semanticAnalysis() throws IOException {
		String input = Files.readString(Paths.get(dir, "Prime.rime"));
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
	public void test_sortProgram_semanticAnalysis() throws IOException {
		String input = Files.readString(Paths.get(dir, "Sort.rime"));
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
	public void test_uniqProgram_semanticAnalysis() throws IOException {
		String input = Files.readString(Paths.get(dir, "Uniq.rime"));
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
