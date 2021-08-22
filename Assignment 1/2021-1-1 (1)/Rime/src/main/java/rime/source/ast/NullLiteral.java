package rime.source.ast;

public final class NullLiteral implements Expression {
	public static final NullLiteral NULL = new NullLiteral();
	
	private NullLiteral() {}
	
	@Override
	public String toString() {
		return "NULL";
	}
}
