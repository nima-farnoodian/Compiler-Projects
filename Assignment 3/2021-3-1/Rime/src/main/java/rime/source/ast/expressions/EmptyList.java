package rime.source.ast.expressions;

import rime.source.ast.types.PrimitiveType;



public final class EmptyList extends Expression {
	public final PrimitiveType type;
	
	public EmptyList(PrimitiveType type) {
		this.type = type;
	}
	
	@Override
	public String contents() {
		return "[]";
	}
}
