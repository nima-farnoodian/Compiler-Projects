package rime.source.ast.types;

import rime.source.ast.constants.BasicType;



public final class AnyPrimitiveTypeNode extends PrimitiveType {
	public AnyPrimitiveTypeNode() {
		super(BasicType.ANY);
	}
}
