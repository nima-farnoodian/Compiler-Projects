package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class FunctionCall implements Expression {
	public abstract Identifier name();
	
	public abstract List<Expression> args();
	
	public static FunctionCall create(Identifier name, List<Expression> args) {
		return new AutoValue_FunctionCall(name, args);
	}
}
