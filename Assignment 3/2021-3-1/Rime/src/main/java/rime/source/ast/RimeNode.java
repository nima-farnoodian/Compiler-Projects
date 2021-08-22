package rime.source.ast;

import norswap.uranium.Attribute;
import rime.source.semantic.AttributeName;



public abstract class RimeNode {
	protected RimeNode() { }
	
	public final Attribute attr(AttributeName attribute) {
		return new Attribute(this, attribute.name());
	}
	
	public abstract String contents();
	
	@Override
	public final String toString() {
		final String className = getClass().getSimpleName();
		return className + "(" + contents() + ")";
	}
}
