package rime.source.ast.types;

public final class PredefinedValue extends RimeType {
	public final String name;
	
	public PredefinedValue(String name) {
		this.name = name;
	}
	
	@Override
	public String contents() {
		return name;
	}
}
