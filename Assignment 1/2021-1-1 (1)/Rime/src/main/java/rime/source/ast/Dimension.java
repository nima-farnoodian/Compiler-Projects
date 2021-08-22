package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class Dimension implements Expression {
	public abstract Expression value();
	
	public static Dimension create(Expression value) {
		return new AutoValue_Dimension(value);
	}
}
