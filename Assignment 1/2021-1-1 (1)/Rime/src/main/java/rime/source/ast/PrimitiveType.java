package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class PrimitiveType implements RimeType {
	public abstract BasicType name();
	
	public static PrimitiveType create(BasicType name) {
		return new AutoValue_PrimitiveType(name);
	}
}
