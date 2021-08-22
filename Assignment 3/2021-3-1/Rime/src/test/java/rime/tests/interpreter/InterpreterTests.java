package rime.tests.interpreter;

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
import rime.source.interpreter.Interpreter;
import rime.source.semantic.SemanticAnalysis;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import static norswap.utils.Util.cast;



public class InterpreterTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_helloWorld_1() {
		String input = """
			proc main([string]: _args_) {
				print("Hello, World")
			}
			""";
		String expected = "\"Hello, World\"" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_helloWorld_2() {
		String input = """
						proc sayHello() {
							print("Hello, World")
						}
						
			proc main([string]: _args_) {
				sayHello()
			}
			""";
		String expected = "\"Hello, World\"" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_arithmetic() {
		String input = """
			proc main([string]: _args_) {
				val int: x = 11 % 6
				val int: y = -(x + 2 * 5) / 3
				val int: z = -y * 5
				print(z)
			}
			""";
		String expected = "25" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_booleans_1() {
		String input = """
			proc main([string]: _args_) {
				val int: x = 3
				val int: y = 11
				val bool: lessThan10 = (x < 10 && y < 10)
				val bool: moreThan10 = !lessThan10
				print(moreThan10)
			}
			""";
		String expected = "true" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_booleans_2() {
		String input = """
			proc main([string]: _args_) {
				val int: x = -5
				val int: y = 5
				val bool: atLeastOnePositive = (x >= 1 || y >= 1)
				print(atLeastOnePositive)
			}
			""";
		String expected = "true" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_lists_1() {
		String input = """
			proc main([string]: _args_) {
				val [int]: numbers = [1, 2, 3, 4, 5]
				var int: i = 0
				
				while (i < length(numbers)) {
					print(numbers[i])
					i = i + 1
				}
			}
			""";
		String expected = "1" + System.lineSeparator()
			+ "2" + System.lineSeparator()
			+ "3" + System.lineSeparator()
			+ "4" + System.lineSeparator()
			+ "5" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_lists_2() {
		String input = """
			proc main([string]: _args_) {
				val [int]: numbers = [1, 2, 3, 4, 5]
				val int: i = 4
				
				numbers[i] = 10
				numbers[i] = 15
				print(numbers[i])
			}
			""";
		String expected = "15" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_lists_3() {
		String input = """
			proc main([string]: _args_) {
				var [int]: numbers = [1, 2, 3, 4, 5]
				numbers = append(numbers, 6)
				print(numbers[5])
			}
			""";
		String expected = "6" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_lists_4() {
		String input = """
			proc main([string]: _args_) {
				val [int]: numbers = [1, 2, 3, 4, 5]
				numbers[2] = 99
				
				var int: i = 0
				
				while (i < length(numbers)) {
					print(numbers[i])
					i = i + 1
				}
			}
			""";
		String expected = "1" + System.lineSeparator()
			+ "2" + System.lineSeparator()
			+ "99" + System.lineSeparator()
			+ "4" + System.lineSeparator()
			+ "5" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_lists_5() {
		String input = """
			proc main([string]: _args_) {
				var [int]: numbers = [int]
				numbers = append(numbers, 1)
				numbers = append(numbers, 2)
				numbers = append(numbers, 3)
				
				print(numbers[0])
				print(numbers[1])
				print(numbers[2])
			}
			""";
		String expected = "1" + System.lineSeparator()
			+ "2" + System.lineSeparator()
			+ "3" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_sets_1() {
		String input = """
			proc main([string]: _args_) {
				var {int}: numbers = {int}
				numbers = add(numbers, 1)
				numbers = add(numbers, 1)
				numbers = add(numbers, 1)
				numbers = add(numbers, 2)
				numbers = add(numbers, 3)
				
				print(contains(numbers, 1))
				print(contains(numbers, 2))
				print(contains(numbers, 3))
				print(contains(numbers, 4))
			}
			""";
		String expected = "true" + System.lineSeparator()
			+ "true" + System.lineSeparator()
			+ "true" + System.lineSeparator()
			+ "false" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_sets_2() {
		String input = """
			proc main([string]: _args_) {
				val {int}: numbers = {1, 2, 3, 4, 5}
				
				print(contains(numbers, 1))
				print(contains(numbers, 6))
			}
			""";
		String expected = "true" + System.lineSeparator()
			+ "false" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_dicts_1() {
		String input = """
			proc main([string]: _args_) {
				val {string:int}: ageByName = {"Tam": 12, "Tem": 26, "Tim": 30}
				
				print(ageByName["Tem"])
			}
			""";
		String expected = "26" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_dicts_2() {
		String input = """
			proc main([string]: _args_) {
				val {string:int}: ageByName = {string:int}
				
				ageByName["Tom"] = 15
				ageByName["Tum"] = 44
				ageByName["Tym"] = 22
				
				print(ageByName["Tum"])
			}
			""";
		String expected = "44" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_functions_1() {
		String input = """
			func int square(int: x) {
				return x * x
			}
						
			proc main([string]: _args_) {
				val int: n = 9
				print(square(9))
			}
			""";
		String expected = "81" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_functions_2() {
		String input = """
			func int sum(int: x, int: y) {
				return x + y
			}
						
			func int square(int: x) {
				return x * x
			}
						
			proc main([string]: _args_) {
				val int: m = 9
				val int: n = 5
				val int: res = square(sum(m, n))
				
				print(res)
			}
			""";
		String expected = "196" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_functions_3() {
		String input = """
			func int sumList([int]: numbers) {
				var int: i = 0
				var int: total = 0
				
				while (i < length(numbers)) {
					total = total + numbers[i]
					i = i + 1
				}
				
				return total
			}
						
			proc main([string]: _args_) {
				val [int]: numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9]
				val int: res = sumList(numbers)
				
				print(res)
			}
			""";
		String expected = "45" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_functions_4() {
		String input = """
			func int square(int: x) {
				return x * x
			}
			
			func [int] squareList([int]: numbers) {
				var int: i = 0
				var [int]: list = [int]
				
				while (i < length(numbers)) {
					list = append(list, square(numbers[i]))
					i = i + 1
				}
				
				return list
			}
						
			proc main([string]: _args_) {
				val [int]: numbers = [1, 2, 3, 4, 5]
				val [int]: squaredNumbers = squareList(numbers)
				
				var int: i = 0
				while (i < length(squaredNumbers)) {
					print(squaredNumbers[i])
					i = i + 1
				}
			}
			""";
		String expected = "1" + System.lineSeparator()
			+ "4" + System.lineSeparator()
			+ "9" + System.lineSeparator()
			+ "16" + System.lineSeparator()
			+ "25" + System.lineSeparator();
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
	}
	
	@Test
	public void test_functions_5() {
		String input = """
			proc doNothing() {
				pass
			}
						
			proc main([string]: _args_) {
				doNothing()
			}
			""";
		String expected = "";
		
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
		
		String output = getProgramOutput(tree, interpreter, args);
		
		Assert.assertEquals(expected, output);
		interpreter.interpret(tree, args); // rerun to print the results
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
