package rime.source.ast.declarations;

import rime.source.ast.constants.BasicType;
import rime.source.ast.expressions.Identifier;
import rime.source.ast.expressions.Parameter;
import rime.source.ast.expressions.Parameters;
import rime.source.ast.statements.Block;
import rime.source.ast.types.ListTypeNode;
import rime.source.ast.types.PrimitiveType;

import java.util.ArrayList;



public final class EntryPoint extends Declaration {
	public final ProcDefinition definition;
	
	public EntryPoint(Block body) {
		Parameters parameters = new Parameters(new ArrayList<>() {
			{
				add(new Parameter(new ListTypeNode(new PrimitiveType(BasicType.STRING)), new Identifier("args")));
			}
		});
		
		definition = new ProcDefinition(new Identifier("main"), parameters, body);
	}
	
	@Override
	public String contents() {
		return definition.functionKind.name() + " " + definition.name +
			" (" + definition.parameters + ") { " + definition.body + " }";
	}
}
