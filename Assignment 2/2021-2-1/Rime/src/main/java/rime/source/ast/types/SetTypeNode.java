package rime.source.ast.types;

public final class SetTypeNode extends PredefinedType {
	public final PrimitiveType type;
	
	public SetTypeNode(PrimitiveType type) {
		super("{" + type + "}");
		this.type = type;
	}
}
