package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class IfStatement implements Statement {
	public abstract Expression condition();
	
	public abstract List<Statement> statements();
	
	public static IfStatement create(Expression condition, List<Statement> statements) {
		return new AutoValue_IfStatement(condition, statements);
	}
}
