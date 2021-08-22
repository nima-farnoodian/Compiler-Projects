package rime.source.ast.expressions;

import java.util.List;



public final class Parameters extends Expression {
	public final List<Parameter> params;
	public final int count;
	
	public Parameters(List<Parameter> params) {
		this.params = params;
		this.count = params.size();
	}
	
	@Override
	public String contents() {
		final StringBuilder sb = new StringBuilder();
		
		for (Parameter parameter : params) {
			sb.append(parameter).append(", ");
		}
		
		return sb.toString();
	}
}
