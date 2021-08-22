package rime.source.semantic.scope;

import norswap.uranium.Reactor;
import rime.source.ast.Instances;
import rime.source.ast.constants.BasicType;
import rime.source.ast.constants.FunctionKind;
import rime.source.ast.declarations.PredefinedFunction;
import rime.source.ast.declarations.RootNode;
import rime.source.ast.types.*;

import rime.source.parsing.RimeGrammar;
import rime.source.semantic.types.*;

import static rime.source.semantic.AttributeName.*;



public final class RootScope extends Scope {
	// Predefined types
	public final PredefinedTypeDecl boolType = new PredefinedTypeDecl(new PrimitiveType(BasicType.BOOL));
	public final PredefinedTypeDecl intType = new PredefinedTypeDecl(new PrimitiveType(BasicType.INT));
	public final PredefinedTypeDecl stringType = new PredefinedTypeDecl(new PrimitiveType(BasicType.STRING));
	public final PredefinedTypeDecl typeType = new PredefinedTypeDecl(new PrimitiveType(BasicType.TYPE));
	public final PredefinedTypeDecl voidType = new PredefinedTypeDecl(new PrimitiveType(BasicType.VOID));
	
	// Predefined values
	public final PredefinedValueDecl trueValue = new PredefinedValueDecl(new PredefinedValue("TRUE"));
	public final PredefinedValueDecl falseValue = new PredefinedValueDecl(new PredefinedValue("FALSE"));
	public final PredefinedValueDecl nullValue = new PredefinedValueDecl(new PredefinedValue("NULL"));
	public final PredefinedValueDecl mainArgs = new PredefinedValueDecl(new PredefinedValue(RimeGrammar.MAIN_ARGS));
	
	// Predefined functions
	public final PredefinedFunction main = new PredefinedFunction(
		FunctionKind.PROC, "main", null, Instances.STRING_LIST
	);
	
	public final PredefinedFunction print = new PredefinedFunction(
		FunctionKind.PROC, "print", null, Instances.ANY
	);
	
	public final PredefinedFunction parseInt = new PredefinedFunction(
		FunctionKind.FUNC, "parseInt", Instances.INT, Instances.STRING
	);
	
	public final PredefinedFunction length = new PredefinedFunction(
		FunctionKind.FUNC, "length", Instances.INT, new ListTypeNode(Instances.ANY)
	);
	
	public final PredefinedFunction append = new PredefinedFunction(
		FunctionKind.PROC, "append", new ListTypeNode(Instances.ANY), new ListTypeNode(Instances.ANY), Instances.ANY
	);
	
	public final PredefinedFunction add = new PredefinedFunction(
		FunctionKind.PROC, "add", new SetTypeNode(Instances.ANY), new SetTypeNode(Instances.ANY), Instances.ANY
	);
	
	public final PredefinedFunction contains = new PredefinedFunction(
		FunctionKind.FUNC, "contains", Instances.BOOL, new SetTypeNode(Instances.ANY), Instances.ANY
	);
	
	public RootScope(RootNode node, Reactor reactor) {
		super(node, null);
		
		// Predefined types
		declare(boolType.name, boolType);
		declare(intType.name, intType);
		declare(stringType.name, stringType);
		declare(voidType.name, voidType);
		
		reactor.set(boolType.attr(TYPE), TypeType.INSTANCE);
		reactor.set(intType.attr(TYPE), TypeType.INSTANCE);
		reactor.set(stringType.attr(TYPE), TypeType.INSTANCE);
		reactor.set(typeType.attr(TYPE), TypeType.INSTANCE);
		reactor.set(voidType.attr(TYPE), TypeType.INSTANCE);
		
		reactor.set(boolType.attr(DECLARED), BoolType.INSTANCE);
		reactor.set(intType.attr(DECLARED), IntType.INSTANCE);
		reactor.set(stringType.attr(DECLARED), StringType.INSTANCE);
		reactor.set(typeType.attr(DECLARED), TypeType.INSTANCE);
		reactor.set(voidType.attr(DECLARED), VoidType.INSTANCE);
		
		// Predefined values
		declare(trueValue.name, trueValue);
		declare(falseValue.name, falseValue);
		declare(nullValue.name, nullValue);
		declare(mainArgs.name, mainArgs);
		
		reactor.set(trueValue.attr(TYPE), BoolType.INSTANCE);
		reactor.set(falseValue.attr(TYPE), BoolType.INSTANCE);
		reactor.set(nullValue.attr(TYPE), NullType.INSTANCE);
		reactor.set(mainArgs.attr(TYPE), ListType.STRING_LIST);
		
		// Predefined functions
		declare(main.name, main);
		declare(print.name, print);
		declare(parseInt.name, parseInt);
		declare(length.name, length);
		declare(append.name, append);
		declare(add.name, add);
		declare(contains.name, contains);
		
		reactor.set(main.attr(TYPE),
			new FunctionType(VoidType.INSTANCE, new ListType(StringType.INSTANCE))
		);
		
		reactor.set(print.attr(TYPE),
			new FunctionType(VoidType.INSTANCE, AnyPrimitiveType.INSTANCE)
		);
		
		reactor.set(parseInt.attr(TYPE),
			new FunctionType(IntType.INSTANCE, StringType.INSTANCE)
		);
		
		reactor.set(length.attr(TYPE),
			new FunctionType(IntType.INSTANCE, new ListType(AnyPrimitiveType.INSTANCE))
		);
		
		reactor.set(append.attr(TYPE),
			new FunctionType(
				new ListType(AnyPrimitiveType.INSTANCE), new ListType(AnyPrimitiveType.INSTANCE), AnyPrimitiveType.INSTANCE
			)
		);
		
		reactor.set(add.attr(TYPE),
			new FunctionType(
				new SetType(AnyPrimitiveType.INSTANCE), new SetType(AnyPrimitiveType.INSTANCE), AnyPrimitiveType.INSTANCE
			)
		);
		
		reactor.set(contains.attr(TYPE),
			new FunctionType(
				BoolType.INSTANCE, new SetType(AnyPrimitiveType.INSTANCE), AnyPrimitiveType.INSTANCE
			)
		);
	}
}
