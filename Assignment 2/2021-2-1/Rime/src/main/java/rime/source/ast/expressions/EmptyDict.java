package rime.source.ast.expressions;

import rime.source.ast.types.PrimitiveType;

import java.util.List;



public final class EmptyDict extends Expression {
	public final List<DictElement> elements;
	public final PrimitiveType keyType;
	public final PrimitiveType valueType;
	
	public EmptyDict(List<DictElement> elements, PrimitiveType keyType, PrimitiveType valueType) {
		this.elements = elements;
		this.keyType = keyType;
		this.valueType = valueType;
	}
	
	@Override
	public String contents() {
		return "{}";
	}
}
