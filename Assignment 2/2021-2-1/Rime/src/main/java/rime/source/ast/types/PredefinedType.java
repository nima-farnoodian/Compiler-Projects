package rime.source.ast.types;

public abstract class PredefinedType extends RimeType {
	public final String name;
	
	public PredefinedType(String name) {
		this.name = name;
	}
	
	@Override
	public String contents() {
		return name;
	}
}
