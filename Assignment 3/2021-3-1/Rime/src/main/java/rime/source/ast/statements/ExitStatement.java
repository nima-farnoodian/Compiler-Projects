package rime.source.ast.statements;

public final class ExitStatement extends Statement {
	public ExitStatement() { }
	
	@Override
	public String contents() {
		return "exit";
	}
}
