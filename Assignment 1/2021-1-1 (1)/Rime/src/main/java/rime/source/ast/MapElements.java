package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class MapElements implements Expression {
	public abstract List<Expression> elements();
	
	public static MapElements create(List<Expression> elements) {
		return new AutoValue_MapElements(elements);
	}
}
