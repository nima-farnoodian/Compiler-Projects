package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.List;



@AutoValue
public abstract class WhileStatement implements Statement {
	public abstract Expression condition();
	
	public abstract List<Statement> statements();
	
	public static WhileStatement create(Expression condition, List<Statement> statements) {
		return new AutoValue_WhileStatement(condition, statements);
	}
}
