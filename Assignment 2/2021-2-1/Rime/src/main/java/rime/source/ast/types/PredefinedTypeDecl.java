package rime.source.ast.types;

import rime.source.ast.declarations.Declaration;



public final class PredefinedTypeDecl extends Declaration {
	public final PredefinedType type;
	public final String name;
	
	public PredefinedTypeDecl(PredefinedType type) {
		this.type = type;
		this.name = type.name;
	}
	
	@Override
	public String contents() {
		return name;
	}
}
