package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class ReturnStatement implements Expression {
	public abstract Expression expression();
	
	public static ReturnStatement create(Expression expression) {
		return new AutoValue_ReturnStatement(expression);
	}
}
