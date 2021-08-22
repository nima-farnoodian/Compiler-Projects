package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class BoolLiteral implements Expression {
	public abstract Boolean value();
	
	public static BoolLiteral create(Boolean value) {
		return new AutoValue_BoolLiteral(value);
	}
}
