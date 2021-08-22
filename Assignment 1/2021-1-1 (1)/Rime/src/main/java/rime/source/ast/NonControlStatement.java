package rime.source.ast;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class NonControlStatement implements Statement {
	public abstract Statement statement();
	
	public static NonControlStatement create(Statement statement) {
		return new AutoValue_NonControlStatement(statement);
	}
}
