package rime.source.ast.declarations;

import rime.source.ast.expressions.Identifier;
import rime.source.ast.expressions.Parameters;
import rime.source.ast.statements.Block;
import rime.source.ast.constants.FunctionKind;



public final class ProcDefinition extends FunctionDefinition {
	public final FunctionKind functionKind = FunctionKind.PROC;
	public final Identifier name;
	public final Parameters parameters;
	public final Block body;
	
	public ProcDefinition(Identifier name, Parameters parameters, Block body) {
		this.name = name;
		this.parameters = parameters;
		this.body = body;
	}
	
	@Override
	public String contents() {
		return functionKind.name() + " " + name + " (" + parameters + ") { " + body + " }";
	}
}
