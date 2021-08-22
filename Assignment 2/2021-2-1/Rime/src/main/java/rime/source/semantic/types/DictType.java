package rime.source.semantic.types;

public final class DictType extends Type {
	public final Type keyType;
	public final Type valueType;
	
	public DictType(Type keyType, Type valueType) {
		super("{" + keyType.name + ":" + valueType + "}");
		this.keyType = keyType;
		this.valueType = valueType;
	}
	
	@Override
	public boolean isPrimitive() {
		return false;
	}
}

