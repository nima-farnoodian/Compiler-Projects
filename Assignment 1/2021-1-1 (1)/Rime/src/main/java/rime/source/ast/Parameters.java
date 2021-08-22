package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class Parameters implements Params {
	public abstract List<Identifier> identifiers();
	
	public static Parameters create(List<Identifier> identifiers) {
		return new AutoValue_Parameters(identifiers);
	}
}
