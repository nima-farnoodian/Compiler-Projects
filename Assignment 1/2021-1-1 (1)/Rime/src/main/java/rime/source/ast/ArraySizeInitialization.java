package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class ArraySizeInitialization implements Expression {
	public abstract Dimension dim();
	
	public static ArraySizeInitialization create(Dimension dim) {
		return new AutoValue_ArraySizeInitialization(dim);
	}
}
