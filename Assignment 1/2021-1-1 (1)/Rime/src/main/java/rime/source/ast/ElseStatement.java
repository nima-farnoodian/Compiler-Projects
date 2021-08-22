package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class ElseStatement implements Statement {
	public abstract List<Statement> statements();
	
	public static ElseStatement create(List<Statement> statements) {
		return new AutoValue_ElseStatement(statements);
	}
}
