package rime.source.ast.expressions.literals;

import rime.source.ast.expressions.Expression;



public final class NullLiteral extends Expression {
	public static final NullLiteral NULL = new NullLiteral();
	
	private NullLiteral() {}
	
	public String contents() {
		return "NULL";
	}
}
