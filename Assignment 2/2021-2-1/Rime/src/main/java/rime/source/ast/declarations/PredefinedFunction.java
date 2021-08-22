package rime.source.ast.declarations;

import norswap.utils.NArrays;
import rime.source.ast.constants.FunctionKind;
import rime.source.ast.types.RimeType;



public final class PredefinedFunction extends Declaration {
	public final FunctionKind functionKind;
	public final String name;
	public final RimeType returnType;
	public final RimeType[] parameterTypes;
	
	public PredefinedFunction(FunctionKind functionKind, String name, RimeType returnType, RimeType... parameterTypes) {
		this.functionKind = functionKind;
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
	}
	
	@Override
	public String contents() {
		return functionKind.name() + " " +
			(returnType != null ? returnType : "") + " " +
			name +
			"(" + String.join(",", NArrays.map(parameterTypes, new String[0], RimeType::toString)) + ")";
	}
}
