package rime.source.semantic.types;

public final class IntType extends Type {
	public static final IntType INSTANCE = new IntType();
	
	private IntType() {
		super("int");
	}
}
