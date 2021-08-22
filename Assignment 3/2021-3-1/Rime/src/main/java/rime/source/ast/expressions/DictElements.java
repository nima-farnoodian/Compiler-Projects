package rime.source.ast.expressions;

import java.util.List;



public final class DictElements extends Expression {
	public final List<DictElement> elements;
	
	public DictElements(List<DictElement> elements) {
		this.elements = elements;
	}
	
	@Override
	public String contents() {
		if (elements.isEmpty()) {
			return "{}";
		}
		
		final StringBuilder sb = new StringBuilder();
		
		for (DictElement element : elements) {
			sb.append(element).append(", ");
		}
		
		return "{" + sb.toString() + "}";
	}
}
