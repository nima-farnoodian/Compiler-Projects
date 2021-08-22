package rime.source.ast.expressions;

public final class DictElement extends Expression {
	public final Expression key;
	public final Expression value;
	
	public DictElement(Expression key, Expression value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String contents() {
		return key + ":" + value;
	}
}
