package rime.source.ast.expressions;

public final class IndexedCollectionAccess extends Expression {
	public final Identifier identifier;
	public final Expression index;
	
	public IndexedCollectionAccess(Identifier identifier, Expression index) {
		this.identifier = identifier;
		this.index = index;
	}
	
	@Override
	public String contents() {
		return identifier + "[" + index + "]";
	}
}
