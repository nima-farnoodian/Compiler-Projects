package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class ExitStatement implements Expression {
	
	public static ExitStatement create() {
		return new AutoValue_ExitStatement();
	}
}
