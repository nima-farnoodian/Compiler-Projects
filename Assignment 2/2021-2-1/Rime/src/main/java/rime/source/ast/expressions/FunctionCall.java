package rime.source.ast.expressions;

import rime.source.ast.RimeNode;

import java.util.List;



public final class FunctionCall extends Expression {
	public final Identifier name;
	public final List<RimeNode> arguments;
	
	public FunctionCall(Identifier name, List<RimeNode> arguments) {
		this.name = name;
		this.arguments = arguments;
	}
	
	@Override
	public String contents() {
		final StringBuilder sb = new StringBuilder();
		
		for (RimeNode expression : arguments) {
			sb.append(expression).append(", ");
		}
		
		return name + "(" + sb.toString() + ")";
	}
}
