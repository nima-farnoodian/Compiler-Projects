package rime.source.ast.declarations;

import java.util.List;



public final class RootNode extends Declaration {
	public final List<FunctionDefinition> preMainDefinitions;
	public final EntryPoint entryPoint;
	
	public RootNode(List<FunctionDefinition> preMainDefinitions, EntryPoint entryPoint) {
		this.preMainDefinitions = preMainDefinitions;
		this.entryPoint = entryPoint;
	}
	
	@Override
	public String contents() {
		final StringBuilder sb = new StringBuilder();
		
		if (!preMainDefinitions.isEmpty()) {
			for (FunctionDefinition functionDefinition : preMainDefinitions) {
				sb.append(functionDefinition).append("\n");
			}
		}
		
		sb.append(entryPoint);
		
		return sb.toString();
	}
}
