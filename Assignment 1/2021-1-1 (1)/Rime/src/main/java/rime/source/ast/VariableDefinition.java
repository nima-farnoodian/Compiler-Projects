package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class VariableDefinition implements Expression {
	public abstract ValOrVar valOrVar();
	
	public abstract Assignment assignment();
	
	public static VariableDefinition create(ValOrVar valOrVar, Assignment assignment) {
		return new AutoValue_VariableDefinition(valOrVar, assignment);
	}
}
