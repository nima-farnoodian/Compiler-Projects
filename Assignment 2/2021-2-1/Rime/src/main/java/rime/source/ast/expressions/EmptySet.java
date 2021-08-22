package rime.source.ast.expressions;

import rime.source.ast.types.PrimitiveType;

import java.util.List;



public final class EmptySet extends Expression {
	public final List<Expression> elements;
	public final PrimitiveType type;
	
	public EmptySet(List<Expression> elements, PrimitiveType type) {
		this.elements = elements;
		this.type = type;
	}
	
	@Override
	public String contents() {
		return "{}";
	}
}
