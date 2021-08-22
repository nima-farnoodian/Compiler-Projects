package rime.source.ast.statements;

import java.util.List;



public final class Block extends Statement {
	public final List<Statement> statements;
	
	public Block(List<Statement> statements) {
		this.statements = statements;
	}
	
	@Override
	public String contents() {
		if (statements.isEmpty()) {
			return "{}";
		}
		
		return "{" + statements.get(0) + "}";
	}
}
