package rime.source.semantic.types;

public final class NullType extends Type {
	public static final NullType INSTANCE = new NullType();
	
	private NullType() {
		super("NULL");
	}
}
