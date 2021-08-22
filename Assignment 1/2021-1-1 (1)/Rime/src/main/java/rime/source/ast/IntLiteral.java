package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class IntLiteral implements Expression {
	public abstract Integer value();
	
	public static IntLiteral create(Integer value) {
		return new AutoValue_IntLiteral(value);
	}
}
