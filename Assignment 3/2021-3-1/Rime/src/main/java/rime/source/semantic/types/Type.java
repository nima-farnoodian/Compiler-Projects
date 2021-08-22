package rime.source.semantic.types;

public abstract class Type {
	public final String name;
	
	protected Type(String name) {
		this.name = name;
	}
	
	public boolean isPrimitive() {
		return true;
	}
	
	public boolean isReference() {
		return !isPrimitive();
	}
	
	@Override
	public String toString() {
		return name;
	}
}

