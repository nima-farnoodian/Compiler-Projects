package rime.source.ast.types;

import rime.source.ast.declarations.Declaration;



public final class PredefinedValueDecl extends Declaration {
	public final PredefinedValue value;
	public final String name;
	
	public PredefinedValueDecl(PredefinedValue value) {
		this.value = value;
		this.name = value.name;
	}
	
	@Override
	public String contents() {
		return name;
	}
}
