package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class StringLiteral implements Expression {
	public abstract String value();
	
	public static StringLiteral create(String value) {
		return new AutoValue_StringLiteral(value);
	}
}
