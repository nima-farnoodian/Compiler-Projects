package rime.source.semantic.scope;

import rime.source.ast.declarations.Declaration;



public final class DeclarationContext {
	public final Scope scope;
	public final Declaration declaration;
	
	public DeclarationContext(Scope scope, Declaration declaration) {
		this.scope = scope;
		this.declaration = declaration;
	}
}
