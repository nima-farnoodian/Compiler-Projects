package rime.source.ast.statements;

public final class ElseStatement extends Statement {
	public final Block body;
	
	public ElseStatement(Block body) {
		this.body = body;
	}
	
	@Override
	public String contents() {
		return "else " + body;
	}
}
