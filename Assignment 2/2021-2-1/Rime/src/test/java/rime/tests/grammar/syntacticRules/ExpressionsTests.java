package rime.tests.grammar.syntacticRules;

import norswap.autumn.Autumn;
import norswap.autumn.ParseOptions;
import norswap.autumn.ParseResult;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import rime.source.grammar.RimeGrammar;



public final class ExpressionsTests {
	private static final RimeGrammar parser = new RimeGrammar();
	
	//region ---- LIST & DICT EXPRESSIONS
	@Test
	public void test_listAccess_success1() {
		String input = "x[0]";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAccess_success2() {
		String input = "numbers[100]";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAccess_success3() {
		String input = "numbers[i]";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAccess_badBrackets_failure() {
		String input = "x{1}";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_dictAccess_success1() {
		String input = "x[5]";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_dictAccess_success2() {
		String input = "x[\"string\"]";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_dictAccess_success3() {
		String input = "x[str]";
		ParseResult result = Autumn.parse(parser.indexedCollectionAccess, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	//endregion
	
	//region ---- BINARY EXPRESSIONS
	//region -- MULTIPLICATION
	@Test
	public void test_multiplication_multiplication_success() {
		String input = "5*5";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_division_success() {
		String input = "5/6";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_modulo_success() {
		String input = "5%5";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_withVariables_success1() {
		String input = "x";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_withVariables_success2() {
		String input = "y * x / 2";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_whitespace_success1() {
		String input = "5 *5";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_whitespace_success2() {
		String input = "5 / 5";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_whitespace_success3() {
		String input = "5% 5";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_singleOperand_success() {
		String input = "5";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_chained_success() {
		String input = "5 * 3 / 2 % 4";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_multiplication_wrongOperator_failure() {
		String input = "5 + 5";
		ParseResult result = Autumn.parse(parser.multiplicationExpression, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	//endregion
	
	//region -- ADDITION
	@Test
	public void test_addition_plus_success() {
		String input = "5+5";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_minus_success() {
		String input = "5-5";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_withVariables_success1() {
		String input = "x";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_withVariables_success2() {
		String input = "x + 5 * y - 2";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_withFunctionCalls_success() {
		String input = "x + g(x + 2) * y - 2 * f(x)";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_whitespace_success1() {
		String input = "5 +5";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_whitespace_success2() {
		String input = "5 - 5";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_whitespace_success3() {
		String input = "5+ 5";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_singleOperand_success() {
		String input = "5";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_chained_success1() {
		String input = "5 + 5 - 4";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_addition_chained_success2() {
		String input = "5 + 5 * 2 + 3 / 4";
		ParseResult result = Autumn.parse(parser.additionExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	//endregion
	
	//region -- COMPARISON
	@Test
	public void test_comparison_lessThan_success() {
		String input = "1<2";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_lessThanOrEqual_success() {
		String input = "1<=2";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_greaterThan_success() {
		String input = "1>2";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_greaterThanOrEqual_success() {
		String input = "1>=2";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_withVariables_success1() {
		String input = "x < 1+5";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_withVariables_success2() {
		String input = "x >= y";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_whitespace_success1() {
		String input = "1 <2";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_whitespace_success2() {
		String input = "1> 2";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_whitespace_success3() {
		String input = "1 >= 2";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_chained_success1() {
		String input = "5 + 3 > 4";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_chained_success2() {
		String input = "5 + 3 > 4 - 8 * 4";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_comparison_chained_failure() {
		String input = "5 > 4 > 3";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_comparison_badArguments_failure() {
		String input = "agsfgds";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_comparison_wrongOperator_failure() {
		String input = "1 >> 2";
		ParseResult result = Autumn.parse(parser.comparisonExpression, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	//endregion
	
	//region -- EQUALITY
	@Test
	public void test_equality_equal_success() {
		String input = "1==2";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_equality_notEqual_success() {
		String input = "1!=2";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_equality_withVariables_success1() {
		String input = "x == y";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_equality_withVariables_success2() {
		String input = "x == 5 + 5";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_equality_whitespace_success1() {
		String input = "1== 2";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_equality_whitespace_success2() {
		String input = "1 != 2";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_equality_chained_success1() {
		String input = "1 + 3 * 5 == 1/2 - 2";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_equality_chained_success2() {
		String input = "1 > 5 == 2 <= 7";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_equality_failure1() {
		String input = "1 == 2 > 7";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_equality_failure2() {
		String input = "1 + 5 == 2 <= 7";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_equality_badArguments_failure() {
		String input = "lkfjdsmqkj";
		ParseResult result = Autumn.parse(parser.equalityExpression, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	//endregion
	
	//region -- BOOLEAN_AND
	@Test
	public void test_booleanAnd_success1() {
		String input = "1 == 1 && 2 != 3";
		ParseResult result = Autumn.parse(parser.booleanAndExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanAnd_success2() {
		String input = "2 != 3";
		ParseResult result = Autumn.parse(parser.booleanAndExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanAnd_success3() {
		String input = "2 >= 3";
		ParseResult result = Autumn.parse(parser.booleanAndExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanAnd_withVariables_success() {
		String input = "x < 9 && y > 5 && z";
		ParseResult result = Autumn.parse(parser.booleanAndExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanAnd_chained_success() {
		String input = "1 == 1 && 2 < 3 && 0==3 && 2 >5";
		ParseResult result = Autumn.parse(parser.booleanAndExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	//endregion
	
	//region -- BOOLEAN_OR
	@Test
	public void test_booleanOr_success1() {
		String input = "1 == 1 || 2 != 3";
		ParseResult result = Autumn.parse(parser.booleanOrExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanOr_success2() {
		String input = "1 == 1";
		ParseResult result = Autumn.parse(parser.booleanOrExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanOr_success3() {
		String input = "1 > 1 && 1 < 2 || 3==3";
		ParseResult result = Autumn.parse(parser.booleanOrExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanOr_withVariables_success() {
		String input = "x && y < 5+2 || z";
		ParseResult result = Autumn.parse(parser.booleanOrExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanOr_chained_success() {
		String input = "1 == 1 || 2 < 3 || 0==3 || 2 > 5";
		ParseResult result = Autumn.parse(parser.booleanOrExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	//endregion
	//endregion
	
	//region ---- UNARY EXPRESSIONS
	@Test
	public void test_booleanNot_success1() {
		String input = "!(1 == 2)";
		ParseResult result = Autumn.parse(parser.booleanNotExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanNot_success2() {
		String input = "!(1 == 2 && 1 < 2 || 5 + 6 > 9)";
		ParseResult result = Autumn.parse(parser.booleanNotExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanNot_withVariables_success1() {
		String input = "!x";
		ParseResult result = Autumn.parse(parser.booleanNotExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanNot_withVariables_success2() {
		String input = "!(x || y < 5)";
		ParseResult result = Autumn.parse(parser.booleanNotExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_booleanNot_failure() {
		String input = "! 1 == 2";
		ParseResult result = Autumn.parse(parser.booleanNotExpression, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_unaryMinus_success() {
		String input = "-(10 * 5 - 8%7)";
		ParseResult result = Autumn.parse(parser.unaryMinusExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_parenExpression_success() {
		String input = "(5 - 78 / 9 % 3)";
		ParseResult result = Autumn.parse(parser.parenExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_unaryMinus_withVariables_success1() {
		String input = "-x";
		ParseResult result = Autumn.parse(parser.unaryMinusExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_unaryMinus_withVariables_success2() {
		String input = "-(x + 5/8)";
		ParseResult result = Autumn.parse(parser.unaryMinusExpression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	//endregion
	
	//region ---- ASSIGNMENT EXPRESSIONS
	@Test
	public void test_simpleAssignment_null_success() {
		String input = "x = NULL";
		ParseResult result = Autumn.parse(parser.simpleAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_simpleAssignment_bool_success() {
		String input = "x = TRUE";
		ParseResult result = Autumn.parse(parser.simpleAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_simpleAssignment_int_success() {
		String input = "x = 5";
		ParseResult result = Autumn.parse(parser.simpleAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_simpleAssignment_string_success() {
		String input = "x = \"this is a string\"";
		ParseResult result = Autumn.parse(parser.simpleAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_simpleAssignment_whitespace_success() {
		String input = "x+=25";
		ParseResult result = Autumn.parse(parser.simpleAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAssignment_empty_success1() {
		String input = "x = [int]";
		ParseResult result = Autumn.parse(parser.listAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAssignment_empty_success2() {
		String input = "x = list(int)";
		ParseResult result = Autumn.parse(parser.listAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAssignment_ints_success() {
		String input = "x = [1, 2, 3]";
		ParseResult result = Autumn.parse(parser.listAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAssignment_strings_success() {
		String input = "x = [\"string1\", \"string2\", \"string3\"]";
		ParseResult result = Autumn.parse(parser.listAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAssignment_singleElement_success() {
		String input = "x = [1]";
		ParseResult result = Autumn.parse(parser.listAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAssignment_whitespace_success() {
		String input = "x = [ 1,2 , 3]";
		ParseResult result = Autumn.parse(parser.listAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_listAssignment_wrongBrackets_failure() {
		String input = "x = { 1,2 , 3}";
		ParseResult result = Autumn.parse(parser.listAssignment, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_listAssignment_trailingComma_failure() {
		String input = "x = [1,]";
		ParseResult result = Autumn.parse(parser.listAssignment, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_dictAssignment_empty_success() {
		String input = "x = dict(string, int)";
		ParseResult result = Autumn.parse(parser.dictAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_dictAssignment_success1() {
		String input = "x = {1:1, 2:2, 3:3}";
		ParseResult result = Autumn.parse(parser.dictAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_dictAssignment_success2() {
		String input = "x = {1:\"str1\", 2:\"str2\", 3:\"str3\"}";
		ParseResult result = Autumn.parse(parser.dictAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_dictAssignment_failure() {
		String input = "x = {1:, 2:, 3:\"str3\"}";
		ParseResult result = Autumn.parse(parser.dictAssignment, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_dictAssignment_trailingComma_failure() {
		String input = "x = {1:1,}";
		ParseResult result = Autumn.parse(parser.dictAssignment, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_setAssignment_empty_success() {
		String input = "x = set(int)";
		ParseResult result = Autumn.parse(parser.setAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_setAssignment_success1() {
		String input = "x = {1, 2, 3}";
		ParseResult result = Autumn.parse(parser.setAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_setAssignment_success2() {
		String input = "x = {1}";
		ParseResult result = Autumn.parse(parser.setAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_setAssignment_success3() {
		String input = "x = {NULL, \"string\", 2}";
		ParseResult result = Autumn.parse(parser.setAssignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_setAssignment_trailingComma_failure() {
		String input = "x = {1, 2,}";
		ParseResult result = Autumn.parse(parser.setAssignment, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_setAssignment_badBraces_failure() {
		String input = "x = {1, 2, 3]";
		ParseResult result = Autumn.parse(parser.setAssignment, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_assignment_simple_success1() {
		String input = "x = 5";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_simple_success2() {
		String input = "someVar += 5";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_simple_success3() {
		String input = "someVar = \"S T R I N G\"";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_list_success1() {
		String input = "x = [1, 2, 3]";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_list_success2() {
		String input = "x = [bool]";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_set_success1() {
		String input = "x = {1, 2, 3}";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_set_success2() {
		String input = "x = set()";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_dict_success1() {
		String input = "x = {1:1, 2:2, 3:3}";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_dict_success2() {
		String input = "x = dict(int, int)";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_withVariables_success1() {
		String input = "x = y + 5 * z";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_withVariables_success2() {
		String input = "x = y && x < 5";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_mixed_success1() {
		String input = "x[0] = TRUE";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_mixed_success2() {
		String input = "x[0] = y + 2*5 / z";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_assignment_mixed_success3() {
		String input = "x[i] = y && !z || numbers[100] == 123";
		ParseResult result = Autumn.parse(parser.assignment, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	//endregion
	
	//region ---- EXPRESSION
	@Test
	public void test_expression_arithmetic_success1() {
		String input = "2+3";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_arithmetic_success2() {
		String input = "42 + 15 - 4 / 8 * 3 % 18";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_arithmetic_success3() {
		String input = "-(1 + 3/5 * 78 - 1)";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_arithmetic_success4() {
		String input = "x + (1 + 3/5 * 78 - 1) / y";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_boolean_success1() {
		String input = "x && 5==5";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.recordCallStack(true).get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_boolean_success2() {
		String input = "1 < 5 && 3+8>9 || 18<20";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.recordCallStack(true).get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_boolean_success3() {
		String input = "!(5==1 && 5 < y || z) && y==1 || (x == 5)";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_literal_success1() {
		String input = "12345";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_literal_success2() {
		String input = "\"this is a string\"";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_literal_success3() {
		String input = "TRUE";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_literal_success4() {
		String input = "NULL";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_literal_failure() {
		String input = "123 abc NULL AAAAAAAAAAAAAAAAH";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertFalse(result.fullMatch);
	}
	
	@Test
	public void test_expression_mixedBoolean_success() {
		String input = "x[0] && x[i] || !(1 == 2)";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_mixedArithmetic_success() {
		String input = "-(x + y) * z[i] / 2";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_arithmeticWithFunctionCall_success() {
		String input = "-(x + y) * z[i] / f(x+y, z, 5==1, 123)";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	
	@Test
	public void test_expression_booleansWithFunctionCall_success() {
		String input = "f(x[i]) && a[ind(x, y, 25)] && TRUE || !(g(x, y, z) && x==1)";
		ParseResult result = Autumn.parse(parser.expression, input, ParseOptions.get());
		Assert.assertTrue(result.fullMatch);
	}
	//endregion
}
