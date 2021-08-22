package rime.source.ast;

import rime.source.ast.constants.BasicType;
import rime.source.ast.types.AnyPrimitiveTypeNode;
import rime.source.ast.types.ListTypeNode;
import rime.source.ast.types.PrimitiveType;



public final class Instances {
	public static final AnyPrimitiveTypeNode ANY = new AnyPrimitiveTypeNode();
	
	public static final PrimitiveType BOOL = new PrimitiveType(BasicType.BOOL);
	public static final PrimitiveType INT = new PrimitiveType(BasicType.INT);
	public static final PrimitiveType STRING = new PrimitiveType(BasicType.STRING);
	public static final PrimitiveType TYPE = new PrimitiveType(BasicType.TYPE);
	public static final PrimitiveType VOID = new PrimitiveType(BasicType.VOID);
	
	public static final ListTypeNode STRING_LIST = new ListTypeNode(new PrimitiveType(BasicType.STRING));
}
