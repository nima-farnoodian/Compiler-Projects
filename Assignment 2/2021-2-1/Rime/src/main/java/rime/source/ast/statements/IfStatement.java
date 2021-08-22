package rime.source.ast.statements;

import rime.source.ast.expressions.Expression;



public final class IfStatement extends Statement {
	public final Expression condition;
	public final Block body;
	
	public IfStatement(Expression condition, Block body) {
		this.condition = condition;
		this.body = body;
	}
	
	@Override
	public String contents() {
		return "if (" + condition + ") {" + body + "}";
	}
}
