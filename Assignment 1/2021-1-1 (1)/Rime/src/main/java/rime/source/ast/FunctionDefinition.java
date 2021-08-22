package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class FunctionDefinition implements Declaration {
	public abstract ProcOrFunc procOrFunc();
	
	public abstract Identifier name();
	
	public abstract Parameters parameters();
	
	public abstract List<Statement> body();
	
	public static FunctionDefinition create(ProcOrFunc procOrFunc, Identifier name, Parameters parameters, List<Statement> body) {
		return new AutoValue_FunctionDefinition(procOrFunc, name, parameters, body);
	}
}
