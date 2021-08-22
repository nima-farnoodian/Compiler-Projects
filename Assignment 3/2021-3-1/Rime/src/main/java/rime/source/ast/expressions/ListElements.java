package rime.source.ast.expressions;

import java.util.List;



public final class ListElements extends Expression {
	public final List<Expression> elements;
	
	public ListElements(List<Expression> elements) {
		this.elements = elements;
	}
	
	@Override
	public String contents() {
		if (elements.isEmpty()) {
			return "[]";
		}
		
		final StringBuilder sb = new StringBuilder();
		
		for (Expression element : elements) {
			sb.append(element).append(", ");
		}
		
		return "[" + sb.toString() + "]";
	}
}
