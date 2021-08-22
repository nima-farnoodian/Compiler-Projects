package rime.source.ast.declarations;

import rime.source.ast.expressions.Identifier;
import rime.source.ast.expressions.Parameters;
import rime.source.ast.statements.Block;
import rime.source.ast.constants.FunctionKind;
import rime.source.ast.types.RimeType;



public final class FuncDefinition extends Declaration implements FunctionDefinition {
	public final FunctionKind functionKind = FunctionKind.FUNC;
	public final RimeType returnType;
	public final Identifier name;
	public final Parameters parameters;
	public final Block body;
	
	public FuncDefinition(RimeType returnType, Identifier name, Parameters parameters, Block body) {
		this.returnType = returnType;
		this.name = name;
		this.parameters = parameters;
		this.body = body;
	}
	
	@Override
	public String contents() {
		return functionKind.name() + " " + returnType + " " + name + " (" + parameters + ") { " + body + " }";
	}
}
