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
import rime.source.ast.expressions.Assignment;
import rime.source.grammar.RimeGrammar;
import rime.source.semantic.SemanticAnalysis;

import java.util.Set;

import static norswap.utils.Util.cast;



public class SemanticAnalysisTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	//region -------- IDENTIFIERS & LITERALS
	@Test
	public void test_identifier() {
		String input = "someVar";
		ParseResult result = Autumn.parse(parser.identifier, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: someVar", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_integerLiteral() {
		String input = "12345";
		ParseResult result = Autumn.parse(parser.integerLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_stringLiteral() {
		String input = "\"this is a string\"";
		ParseResult result = Autumn.parse(parser.stringLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_booleanLiteral() {
		String input = "TRUE";
		ParseResult result = Autumn.parse(parser.booleanLiteral, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_literal() {
		String input = "1000";
		ParseResult result = Autumn.parse(parser.literal, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	//endregion
	
	//region -------- TYPES
	@Test
	public void test_primitiveType() {
		String input = "int";
		ParseResult result = Autumn.parse(parser.primitiveType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: int", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_listType() {
		String input = "[string]";
		ParseResult result = Autumn.parse(parser.listType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: string", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_setType() {
		String input = "{int}";
		ParseResult result = Autumn.parse(parser.setType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: int", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_dictType() {
		String input = "{int:string}";
		ParseResult result = Autumn.parse(parser.dictType, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(2, reactor.errors().size());
		String descr1 = ((SemanticError) reactor.errors().toArray()[0]).description;
		String descr2 = ((SemanticError) reactor.errors().toArray()[1]).description;
		Assert.assertTrue(descr1.equals("Could not resolve: int") || descr1.equals("Could not resolve: string"));
		Assert.assertTrue(descr2.equals("Could not resolve: int") || descr2.equals("Could not resolve: string"));
	}
	
	@Test
	public void test_type() {
		String input = "[bool]";
		ParseResult result = Autumn.parse(parser.type, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: bool", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	//endregion
	
	//region -------- EXPRESSIONS
	@Test
	public void test_indexedCollectionAccess() {
		String input = "x[0]";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: x", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_binaryExpression() {
		String input = "-25 + 25";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_unaryExpression() {
		String input = "!TRUE";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_listElementsAssignment() {
		String input = "x = [1, 2, 3]";
		ParseResult result = Autumn.parse(parser.listAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: x", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_setElementsAssignment() {
		String input = "x = {1, 2, 3}";
		ParseResult result = Autumn.parse(parser.setAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: x", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_dictElementsAssignment() {
		String input = "x = {1:1, 2:2, 3:3}";
		ParseResult result = Autumn.parse(parser.dictAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: x", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_functionCall() {
		String input = "someFunction(25, \"hello\")";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: someFunction", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	//endregion
	
	//region -------- STATEMENTS
	@Test
	public void test_ifStatement() {
		String input = "if (TRUE) { return 5 }";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_ifElseStatement() {
		String input = "if (TRUE) { return 5 } else { return 25 }";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_ifElseIfElseStatement() {
		String input = "if (x >= 5) { return 1 } else if (x < 0) { return 2 } else { return 3 }";
		ParseResult result = Autumn.parse(parser.ifStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: x", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_whileStatement() {
		String input = "while (TRUE) { print(\"FIVE\") }";
		ParseResult result = Autumn.parse(parser.whileStatement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(1, reactor.errors().size());
		Assert.assertEquals("Could not resolve: print", ((SemanticError) reactor.errors().toArray()[0]).description);
	}
	
	@Test
	public void test_exitStatement() {
		String input = "exit";
		ParseResult result = Autumn.parse(parser.statement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_returnStatement() {
		String input = "return 5";
		ParseResult result = Autumn.parse(parser.statement, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(0, reactor.errors().size());
	}
	//endregion
	
	//region -------- DECLARATIONS
	@Test
	public void test_variableDefinition() {
		String input = "val string: x = \"test123\"";
		ParseResult result = Autumn.parse(parser.variableDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(2, reactor.errors().size());
		String descr1 = ((SemanticError) reactor.errors().toArray()[0]).description;
		String descr2 = ((SemanticError) reactor.errors().toArray()[1]).description;
		Assert.assertTrue(descr1.equals("Could not resolve: x") || descr1.equals("Could not resolve: string"));
		Assert.assertTrue(descr2.equals("Could not resolve: x") || descr2.equals("Could not resolve: string"));
	}
	
	@Test
	public void test_procDefinition() {
		String input = "proc sayHello() { print(\"hello world\") }";
		ParseResult result = Autumn.parse(parser.procDefinition, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		reactor.run();
		
		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		Assert.assertEquals(2, reactor.errors().size());
		String descr1 = ((SemanticError) reactor.errors().toArray()[0]).description;
		String descr2 = ((SemanticError) reactor.errors().toArray()[1]).description;
		Assert.assertTrue(descr1.equals("Could not resolve: sayHello") || descr1.equals("Could not resolve: print"));
		Assert.assertTrue(descr2.equals("Could not resolve: sayHello") || descr2.equals("Could not resolve: print"));
	}
	
	@Test
	public void test_root1() {
		String input = "proc main([string]: args) { print(\"hello world\") \n val [int]: x = list(int) }";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		//		reactor.run();
		//
		//		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		//		Assert.assertEquals(0, reactor.errors().size());
	}
	
	@Test
	public void test_root2() {
		String input = "proc sayHello() { print(\"hello world\") }"
			+ "proc main([string]: args) { sayHello() }";
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
		
		RimeNode tree = cast(result.topValue());
		Reactor reactor = new Reactor();
		
		Walker<RimeNode> walker = SemanticAnalysis.createWalker(reactor);
		SemanticAnalysis analysis = SemanticAnalysis.getInstance(reactor);
		walker.walk(tree);
		//		reactor.run();
		//
		//		printErrors(reactor.errors(), new Object() {}.getClass().getEnclosingMethod().getName());
		//		Assert.assertEquals(0, reactor.errors().size());
	}
	//endregion
	
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
