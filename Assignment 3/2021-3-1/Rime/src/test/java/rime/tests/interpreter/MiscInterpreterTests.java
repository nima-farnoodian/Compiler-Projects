package rime.tests.interpreter;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import norswap.uranium.Reactor;
import norswap.uranium.SemanticError;
import norswap.utils.visitors.Walker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import rime.source.ast.RimeNode;
import rime.source.interpreter.Interpreter;
import rime.source.interpreter.exceptions.InterpreterException;
import rime.source.parsing.RimeGrammar;
import rime.source.semantic.SemanticAnalysis;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import static norswap.utils.Util.cast;



public class MiscInterpreterTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_negativeIndex_failure() {
		String input = """
			proc main([string]: _args_) {
				print(_args_[-1])
			}
			""";
		
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
		ArrayList<String> args = new ArrayList<>();
		
		try {
			interpreter.interpret(tree, args);
			Assert.fail();
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof ArrayIndexOutOfBoundsException);
		}
	}
	
	@Test
	public void test_divisionByZero_failure() {
		String input = """
			proc main([string]: _args_) {
				print(5 / 0)
			}
			""";
		
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
		ArrayList<String> args = new ArrayList<>();
		
		try {
			interpreter.interpret(tree, args);
			Assert.fail();
		}
		catch (Exception e) {
			Assert.assertTrue(e instanceof InterpreterException);
		}
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
