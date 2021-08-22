package rime.source.semantic.scope;

import rime.source.ast.RimeNode;
import rime.source.ast.declarations.Declaration;

import java.util.HashMap;



public class Scope {
	public final RimeNode node;
	public final Scope parent;
	private final HashMap<String, Declaration> declarations = new HashMap<>();
	
	public Scope(RimeNode node, Scope parent) {
		this.node = node;
		this.parent = parent;
	}
	
	public void declare(String identifier, Declaration node) {
		declarations.put(identifier, node);
	}
	
	public DeclarationContext lookup(String name) {
		final Declaration declaration = declarations.get(name);
		
		if (declaration != null) {
			return new DeclarationContext(this, declaration);
		}
		else {
			return parent != null ? parent.lookup(name) : null;
		}
	}
}
