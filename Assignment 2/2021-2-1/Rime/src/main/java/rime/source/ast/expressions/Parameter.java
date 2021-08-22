package rime.source.ast.expressions;

import rime.source.ast.types.RimeType;



public final class Parameter extends Expression {
	public final RimeType type;
	public final Identifier identifier;
	
	public Parameter(RimeType type, Identifier identifier) {
		this.type = type;
		this.identifier = identifier;
	}
	
	@Override
	public String contents() {
		return type + ": " + identifier;
	}
}
