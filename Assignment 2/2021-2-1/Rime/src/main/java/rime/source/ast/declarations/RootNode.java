package rime.source.ast.declarations;

import java.util.List;



public final class RootNode extends Declaration {
	public final List<FunctionDefinition> preMainDefinitions;
	public final EntryPoint entryPoint;
	public final List<FunctionDefinition> postMainDefinitions;
	
	public RootNode(List<FunctionDefinition> preMainDefinitions, EntryPoint entryPoint, List<FunctionDefinition> postMainDefinitions) {
		this.preMainDefinitions = preMainDefinitions;
		this.entryPoint = entryPoint;
		this.postMainDefinitions = postMainDefinitions;
	}
	
	@Override
	public String contents() {
		StringBuilder sb = new StringBuilder();
		
		if (!preMainDefinitions.isEmpty()) {
			for (FunctionDefinition functionDefinition : preMainDefinitions) {
				sb.append(functionDefinition).append("\n");
			}
		}
		
		sb.append(entryPoint);
		
		if (!postMainDefinitions.isEmpty()) {
			for (FunctionDefinition functionDefinition : postMainDefinitions) {
				sb.append(functionDefinition).append("\n");
			}
		}
		
		return sb.toString();
	}
}
