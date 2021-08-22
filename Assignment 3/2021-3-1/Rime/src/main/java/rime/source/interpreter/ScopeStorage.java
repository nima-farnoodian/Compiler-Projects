package rime.source.interpreter;

import rime.source.parsing.RimeGrammar;
import rime.source.semantic.scope.RootScope;
import rime.source.semantic.scope.Scope;

import java.util.HashMap;



public final class ScopeStorage {
	public final Scope scope;
	public final ScopeStorage parent;
	private final HashMap<String, Object> values = new HashMap<>();
	
	public ScopeStorage(Scope scope, ScopeStorage parent) {
		this.scope = scope;
		this.parent = parent;
	}
	
	public Object get(Scope scope, String name) {
		if (name.equals(RimeGrammar.MAIN_ARGS)) {
			if (parent != null) {
				return parent.get(scope, name);
			}
			else {
				return scope.lookup(name);
			}
		}
		
		if (scope == this.scope) {
			return values.get(name);
		}
		else if (parent != null) {
			return parent.get(scope, name);
		}
		else {
			throw new Error("Could not lookup name: " + name);
		}
	}
	
	public void set(Scope scope, String name, Object value) {
		if (scope == this.scope) {
			values.put(name, value);
		}
		else {
			parent.set(scope, name, value);
		}
	}
	
	public void initRoot(RootScope root) {
		set(root, root.trueValue.name, true);
		set(root, root.falseValue.name, false);
		set(root, root.nullValue.name, null);
	}
}
