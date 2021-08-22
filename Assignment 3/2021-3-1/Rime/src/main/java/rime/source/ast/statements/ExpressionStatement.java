package rime.source.ast.statements;

import norswap.utils.Util;
import rime.source.ast.expressions.Expression;



public final class ExpressionStatement extends Statement {
	public final Expression expression;
	
	public ExpressionStatement(Object expression) {
		this.expression = Util.cast(expression, Expression.class);
	}
	
	@Override
	public String contents() {
		return expression.contents();
	}
}
