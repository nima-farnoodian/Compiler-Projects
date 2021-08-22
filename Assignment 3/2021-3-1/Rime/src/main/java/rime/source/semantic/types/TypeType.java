package rime.source.semantic.types;

public final class TypeType extends Type {
	public static final TypeType INSTANCE = new TypeType();
	
	private TypeType() {
		super("type");
	}
}
