package rime.source.semantic.types;

import norswap.utils.NArrays;



public final class FunctionType extends Type {
	public final Type returnType;
	public final Type[] paramTypes;
	
	public FunctionType(Type returnType, Type... paramTypes) {
		super("(" + String.join(",", NArrays.map(paramTypes, new String[0], Type::toString)) + ") -> " + returnType
		);
		this.returnType = returnType;
		this.paramTypes = paramTypes;
	}
}