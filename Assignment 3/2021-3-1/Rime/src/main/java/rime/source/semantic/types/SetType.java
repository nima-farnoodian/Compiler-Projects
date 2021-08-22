package rime.source.semantic.types;

public final class SetType extends Type {
	public final Type elementType;
	
	public SetType(Type elementType) {
		super("{" + elementType.name + "}");
		this.elementType = elementType;
	}
	
	@Override
	public boolean isPrimitive() {
		return false;
	}
}

