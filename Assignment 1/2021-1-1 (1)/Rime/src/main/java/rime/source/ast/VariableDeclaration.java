package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class VariableDeclaration implements Expression {
	public abstract ValOrVar valOrVar();
	
	public abstract Identifier identifier();
	
	public static VariableDeclaration create(ValOrVar valOrVar, Identifier identifier) {
		return new AutoValue_VariableDeclaration(valOrVar, identifier);
	}
}
