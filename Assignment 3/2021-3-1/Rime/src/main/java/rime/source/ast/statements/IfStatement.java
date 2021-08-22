package rime.source.ast.statements;

import rime.source.ast.expressions.Expression;



public final class IfStatement extends Statement {
	public final Expression condition;
	public final Block trueBody;
	public final Block falseBody;
	
	public IfStatement(Expression condition, Block trueBody, Block falseBody) {
		this.condition = condition;
		this.trueBody = trueBody;
		this.falseBody = falseBody;
	}
	
	@Override
	public String contents() {
		return "if (" + condition + ") {" + trueBody + "} else {" + falseBody + "}";
	}
}
