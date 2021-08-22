package rime.source.ast.expressions;

public final class Identifier extends Expression {
	public final String value;
	
	public Identifier(String value) {
		this.value = value;
	}
	
	@Override
	public String contents() {
		return value;
	}
}
