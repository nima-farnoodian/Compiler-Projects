package rime.source.ast.expressions;

import rime.source.ast.constants.UnaryOperator;
import rime.source.ast.expressions.Expression;



public final class UnaryExpression extends Expression {
	public final UnaryOperator operator;
	public final Expression operand;
	
	public UnaryExpression(UnaryOperator operator, Expression operand) {
		this.operator = operator;
		this.operand = operand;
	}
	
	@Override
	public String contents() {
		return operator.name() + operand;
	}
}
