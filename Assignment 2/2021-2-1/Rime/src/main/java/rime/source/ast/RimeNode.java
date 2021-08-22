package rime.source.ast;

import norswap.uranium.Attribute;



public abstract class RimeNode {
	protected RimeNode() { }
	
	public final Attribute attr(String name) {
		return new Attribute(this, name);
	}
	
	public abstract String contents();
	
	@Override
	public final String toString() {
		final String className = getClass().getSimpleName();
		return className + "(" + contents() + ")";
	}
}
