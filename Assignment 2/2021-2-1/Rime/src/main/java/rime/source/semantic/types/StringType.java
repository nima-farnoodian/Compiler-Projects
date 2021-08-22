package rime.source.semantic.types;

public final class StringType extends Type {
	public static final StringType INSTANCE = new StringType();
	
	private StringType() {
		super("string");
	}
}
