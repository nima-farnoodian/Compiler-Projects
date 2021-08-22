package rime.source.ast.types;

public final class ListTypeNode extends PredefinedType {
	public final PrimitiveType type;
	
	public ListTypeNode(PrimitiveType type) {
		super("[" + type + "]");
		this.type = type;
	}
}
