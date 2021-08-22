package rime.source.ast.statements;

public final class EmptyStatement extends Statement {
	public EmptyStatement() { }
	
	@Override
	public String contents() {
		return "pass";
	}
}
