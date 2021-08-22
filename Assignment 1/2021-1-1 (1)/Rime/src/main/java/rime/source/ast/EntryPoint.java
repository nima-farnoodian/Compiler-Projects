package rime.source.ast;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;



@AutoValue
public abstract class EntryPoint implements Declaration {
	public ProcOrFunc procOrFunc() { return ProcOrFunc.PROC; }
	
	public Identifier name() { return Identifier.create("main"); }
	
	public Parameters parameters() {
		return Parameters.create(new ArrayList<Identifier>() {{
			add(Identifier.create("args"));
		}});
	}
	
	public abstract List<Statement> body();
	
	public static EntryPoint create(List<Statement> body) {
		return new AutoValue_EntryPoint(body);
	}
}
