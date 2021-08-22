package rime.source.ast.declarations;

import rime.source.ast.constants.BasicType;
import rime.source.ast.expressions.Identifier;
import rime.source.ast.expressions.Parameters;
import rime.source.ast.statements.Block;
import rime.source.ast.types.ListTypeNode;
import rime.source.ast.types.PrimitiveType;
import rime.source.parsing.RimeGrammar;

import java.util.ArrayList;



public final class EntryPoint extends Declaration {
	public final ProcDefinition definition;
	
	public EntryPoint(Block body) {
		final Parameters parameters = new Parameters(new ArrayList<>() {
			{
				add(new Parameter(new ListTypeNode(new PrimitiveType(BasicType.STRING)), new Identifier(RimeGrammar.MAIN_ARGS)));
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
