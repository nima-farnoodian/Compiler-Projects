package rime.source.ast.types;

public final class DictTypeNode extends PredefinedType {
	public final PrimitiveType keyType;
	public final PrimitiveType valueType;
	
	public DictTypeNode(PrimitiveType keyType, PrimitiveType valueType) {
		super("{" + keyType + ":" + valueType + "}");
		this.keyType = keyType;
		this.valueType = valueType;
	}
}
