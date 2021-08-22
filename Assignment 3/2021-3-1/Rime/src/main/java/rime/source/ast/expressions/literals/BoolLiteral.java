package rime.source.ast.expressions.literals;

import rime.source.ast.expressions.Expression;



public final class BoolLiteral extends Expression {
	public final Boolean value;
	
	public BoolLiteral(Boolean value) {
		this.value = value;
	}
	
	@Override
	public String contents() {
		return value.toString();
	}
}
