package rime.source.ast.expressions;

import rime.source.ast.types.PrimitiveType;



public final class EmptySet extends Expression {
	public final PrimitiveType type;
	
	public EmptySet(PrimitiveType type) {
		this.type = type;
	}
	
	@Override
	public String contents() {
		return "{}";
	}
}
