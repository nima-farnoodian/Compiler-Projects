package rime.source.ast.expressions;

import rime.source.ast.declarations.Declaration;



public final class ParameterDecl extends Declaration {
	public final Parameter parameter;
	
	public ParameterDecl(Parameter parameter) {
		this.parameter = parameter;
	}
	
	@Override
	public String contents() {
		return parameter.contents();
	}
}
