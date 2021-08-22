package rime.source.ast.expressions;

import rime.source.ast.constants.BinaryOperator;



public final class Assignment extends Expression {
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
