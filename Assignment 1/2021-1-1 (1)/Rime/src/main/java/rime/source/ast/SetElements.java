package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class SetElements implements Expression {
	public abstract List<Expression> elements();
	
	public static SetElements create(List<Expression> elements) {
		return new AutoValue_SetElements(elements);
	}
}
