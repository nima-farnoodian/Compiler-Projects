package rime.source.ast.expressions;

import java.util.ArrayList;
import java.util.List;



public final class FunctionCall extends Expression {
	public final Identifier name;
	public final List<Expression> arguments;
	
	public FunctionCall(Identifier name, List<Expression> arguments) {
		this.name = name;
		this.arguments = new ArrayList<>(arguments);
	}
	
	@Override
	public String contents() {
		final StringBuilder sb = new StringBuilder();
		
		for (Expression expression : arguments) {
			sb.append(expression).append(", ");
		}
		
		return name + "(" + sb.toString() + ")";
	}
}
