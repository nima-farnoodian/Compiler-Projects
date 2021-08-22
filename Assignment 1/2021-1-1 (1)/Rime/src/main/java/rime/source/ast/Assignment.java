package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class Assignment implements Expression {
	public abstract Object left();
	
	public abstract BinaryOperator operator();
	
	public abstract Expression expression();
	
	public static Assignment create(Object left, BinaryOperator operator, Expression expression) {
		return new AutoValue_Assignment(left, operator, expression);
	}
}
