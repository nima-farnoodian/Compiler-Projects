package rime.source.ast.statements;

import rime.source.ast.constants.BinaryOperator;
import rime.source.ast.expressions.Expression;
import rime.source.ast.statements.Statement;



public final class Assignment extends Statement {
	public final Expression left;
	public final BinaryOperator operator;
	public final Expression right;
	
	public Assignment(Expression left, BinaryOperator operator, Expression right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	
	@Override
	public String contents() {
		return left + " " + operator.name() + " " + right;
	}
}
