package rime.source.semantic.types;

public final class VoidType extends Type {
	public static final VoidType INSTANCE = new VoidType();
	
	private VoidType() {
		super("void");
	}
}
