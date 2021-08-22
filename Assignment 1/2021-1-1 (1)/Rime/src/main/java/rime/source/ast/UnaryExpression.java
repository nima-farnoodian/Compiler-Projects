package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class UnaryExpression implements Expression {
	public abstract UnaryOperator operator();
	
	public abstract Expression operand();
	
	public static UnaryExpression create(UnaryOperator operator, Expression operand) {
		return new AutoValue_UnaryExpression(operator, operand);
	}
}
