package rime.source.ast.expressions;

import rime.source.ast.types.PrimitiveType;

import java.util.List;



public final class EmptyList extends Expression {
	public final List<Expression> elements;
	public final PrimitiveType type;
	
	public EmptyList(List<Expression> elements, PrimitiveType type) {
		this.elements = elements;
		this.type = type;
	}
	
	@Override
	public String contents() {
		return "[]";
	}
}
