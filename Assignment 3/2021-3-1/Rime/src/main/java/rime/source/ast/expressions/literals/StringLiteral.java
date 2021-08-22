package rime.source.ast.expressions.literals;

import rime.source.ast.expressions.Expression;



public final class StringLiteral extends Expression {
	public final String value;
	
	public StringLiteral(String value) {
		this.value = value;
	}
	
	@Override
	public String contents() {
		return value;
	}
}
