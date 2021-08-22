package rime.tests.interpreter;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import norswap.uranium.Reactor;
import norswap.uranium.SemanticError;
import norswap.utils.visitors.Walker;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.examples.java.*;
import rime.source.ast.RimeNode;
import rime.source.parsing.RimeGrammar;
import rime.source.interpreter.Interpreter;
import rime.source.semantic.SemanticAnalysis;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static norswap.utils.Util.cast;



public final class ExampleProgramsTests {
	private static final String dir = "src/main/java/rime/examples/rime/";
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_fibonacciProgram_interpreter() throws IOException {
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
		
		Interpreter interpreter = new Interpreter(reactor);
		ArrayList<String> args = new ArrayList<>() {{
			add("100");
		}};
		
		String expected = generateFibonacciOutput(args);
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_fizzBuzzProgram_interpreter() throws IOException {
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
		
		Interpreter interpreter = new Interpreter(reactor);
		ArrayList<String> args = new ArrayList<>() {{
			add("100");
		}};
		
		String expected = generateFizzBuzzOutput(args);
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_primeProgram_interpreter() throws IOException {
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
		
		Interpreter interpreter = new Interpreter(reactor);
		ArrayList<String> args = new ArrayList<>() {{
			add("100");
		}};
		
		String expected = generatePrimeOutput(args);
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_sortProgram_interpreter() throws IOException {
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
		
		Interpreter interpreter = new Interpreter(reactor);
		ArrayList<String> args = new ArrayList<>();
		for (int i = 0; i < 250; i++) {
			args.add(i + "");
		}
		Collections.shuffle(args);
		
		String expected = generateSortOutput(args);
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_uniqProgram_interpreter() throws IOException {
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
		
		Interpreter interpreter = new Interpreter(reactor);
		ArrayList<String> args = new ArrayList<>();
		Random rng = new Random();
		for (int i = 0; i < 500; i++) {
			final int x = rng.nextInt(10);
			args.add(x + "");
		}
		
		String expected = generateUniqOutput(args);
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	private static String generateFibonacciOutput(List<String> numbers) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(outputStream);
		PrintStream old = System.out;
		System.setOut(ps);
		
		Fibonacci.main(numbers.toArray(new String[numbers.size()]));
		
		System.out.flush();
		System.setOut(old);
		return outputStream.toString();
	}
	
	private static String generateFizzBuzzOutput(List<String> numbers) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(outputStream);
		PrintStream old = System.out;
		System.setOut(ps);
		
		FizzBuzz.main(numbers.toArray(new String[numbers.size()]));
		
		System.out.flush();
		System.setOut(old);
		return outputStream.toString();
	}
	
	private static String generatePrimeOutput(List<String> numbers) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(outputStream);
		PrintStream old = System.out;
		System.setOut(ps);
		
		Prime.main(numbers.toArray(new String[numbers.size()]));
		
		System.out.flush();
		System.setOut(old);
		return outputStream.toString();
	}
	
	private static String generateSortOutput(List<String> numbers) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(outputStream);
		PrintStream old = System.out;
		System.setOut(ps);
		
		Sort.main(numbers.toArray(new String[numbers.size()]));
		
		System.out.flush();
		System.setOut(old);
		return outputStream.toString();
	}
	
	private static String generateUniqOutput(List<String> numbers) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(outputStream);
		PrintStream old = System.out;
		System.setOut(ps);
		
		Uniq.main(numbers.toArray(new String[numbers.size()]));
		
		System.out.flush();
		System.setOut(old);
		return outputStream.toString();
	}
	
	private String getProgramOutput(RimeNode tree, Interpreter interpreter, ArrayList<String> args) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(outputStream);
		PrintStream old = System.out;
		System.setOut(ps);
		
		interpreter.interpret(tree, args);
		
		System.out.flush();
		System.setOut(old);
		return outputStream.toString();
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
