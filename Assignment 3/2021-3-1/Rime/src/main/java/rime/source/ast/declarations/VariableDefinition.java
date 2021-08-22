package rime.source.ast.declarations;

import rime.source.ast.constants.VariableKind;
import rime.source.ast.statements.Assignment;
import rime.source.ast.types.RimeType;



public final class VariableDefinition extends Declaration {
	public final VariableKind variableKind;
	public final RimeType type;
	public final Assignment assignment;
	
	public VariableDefinition(VariableKind variableKind, RimeType type, Assignment assignment) {
		this.variableKind = variableKind;
		this.type = type;
		this.assignment = assignment;
	}
	
	@Override
	public String contents() {
		return variableKind.name() + " " + type + ": " + assignment;
	}
}
