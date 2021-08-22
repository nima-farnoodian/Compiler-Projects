package rime.source.semantic.types;

public final class AnyPrimitiveType extends Type {
	public static final AnyPrimitiveType INSTANCE = new AnyPrimitiveType();
	
	public AnyPrimitiveType() {
		super("any");
	}
}
