package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class IndexedCollectionAccess implements Expression {
	public abstract Expression operand();
	
	public abstract Expression index();
	
	public static IndexedCollectionAccess create(Expression operand, Expression index) {
		return new AutoValue_IndexedCollectionAccess(operand, index);
	}
}
