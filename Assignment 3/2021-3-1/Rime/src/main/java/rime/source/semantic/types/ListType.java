package rime.source.semantic.types;

public final class ListType extends Type {
	public static final ListType STRING_LIST = new ListType(StringType.INSTANCE);
	
	public final Type elementType;
	
	public ListType(Type elementType) {
		super("[" + elementType.name + "]");
		this.elementType = elementType;
	}
	
	@Override
	public boolean isPrimitive() {
		return false;
	}
}

