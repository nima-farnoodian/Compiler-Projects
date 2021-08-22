package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class BinaryExpression implements Expression {
	public abstract Expression left();
	
	public abstract BinaryOperator operator();
	
	public abstract Expression right();
	
	public static BinaryExpression create(Expression left, BinaryOperator operator, Expression right) {
		return new AutoValue_BinaryExpression(left, operator, right);
	}
}
