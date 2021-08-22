package rime.source.ast.statements;

import rime.source.ast.expressions.Expression;



public final class ReturnStatement extends Statement {
	public final Expression expression;
	
	public ReturnStatement(Expression expression) {
		this.expression = expression;
	}
	
	@Override
	public String contents() {
		return "return " + expression;
	}
}
