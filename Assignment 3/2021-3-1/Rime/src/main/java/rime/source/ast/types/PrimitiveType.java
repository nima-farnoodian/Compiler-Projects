package rime.source.ast.types;

import rime.source.ast.constants.BasicType;



public class PrimitiveType extends PredefinedType {
	public final BasicType type;
	
	public PrimitiveType(BasicType type) {
		super(type.name().toLowerCase());
		this.type = type;
	}
}
