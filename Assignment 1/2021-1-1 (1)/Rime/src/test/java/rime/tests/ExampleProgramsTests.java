package rime.tests;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.grammar.RimeGrammar;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;



public final class ExampleProgramsTests {
	private static final String dir = "examples";
	private static final RimeGrammar parser = new RimeGrammar();
	
	@Test
	public void test_fibonacciProgram_canBeParsed() throws IOException {
		String input = Files.readString(Paths.get(dir, "Fibonacci.rime"));
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_fizzBuzzProgram_canBeParsed() throws IOException {
		String input = Files.readString(Paths.get(dir, "FizzBuzz.rime"));
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_primeProgram_canBeParsed() throws IOException {
		String input = Files.readString(Paths.get(dir, "Prime.rime"));
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_sortProgram_canBeParsed() throws IOException {
		String input = Files.readString(Paths.get(dir, "Sort.rime"));
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.recordCallStack(true).get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_uniqProgram_canBeParsed() throws IOException {
		String input = Files.readString(Paths.get(dir, "Uniq.rime"));
		ParseResult result = Autumn.parse(parser.root, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
}
