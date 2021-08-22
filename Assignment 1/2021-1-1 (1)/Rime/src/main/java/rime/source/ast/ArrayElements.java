package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class ArrayElements implements Expression {
	public abstract List<Expression> elements();
	
	public static ArrayElements create(List<Expression> elements) {
		return new AutoValue_ArrayElements(elements);
	}
}
