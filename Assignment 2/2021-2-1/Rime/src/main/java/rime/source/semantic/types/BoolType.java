package rime.source.semantic.types;

public final class BoolType extends Type {
	public static final BoolType INSTANCE = new BoolType();
	
	private BoolType() {
		super("bool");
	}
}