package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class MapElement implements Expression {
	public abstract Expression key();
	
	public abstract Expression value();
	
	public static MapElement create(Expression key, Expression value) {
		return new AutoValue_MapElement(key, value);
	}
}
