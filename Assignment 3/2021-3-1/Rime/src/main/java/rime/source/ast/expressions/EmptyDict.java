package rime.source.ast.expressions;

import rime.source.ast.types.PrimitiveType;



public final class EmptyDict extends Expression {
	public final PrimitiveType keyType;
	public final PrimitiveType valueType;
	
	public EmptyDict(PrimitiveType keyType, PrimitiveType valueType) {
		this.keyType = keyType;
		this.valueType = valueType;
	}
	
	@Override
	public String contents() {
		return "{}";
	}
}
