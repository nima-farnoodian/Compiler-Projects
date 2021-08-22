package rime.source.ast.expressions.literals;

import rime.source.ast.expressions.Expression;



public final class IntLiteral extends Expression {
	public final Integer value;
	
	public IntLiteral(Integer value) {
		this.value = value;
	}
	
	@Override
	public String contents() {
		return value.toString();
	}
}
