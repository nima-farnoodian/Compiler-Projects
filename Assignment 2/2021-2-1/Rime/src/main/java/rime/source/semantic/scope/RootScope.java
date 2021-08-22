package rime.source.semantic.scope;

import norswap.uranium.Reactor;
import rime.source.ast.Instances;
import rime.source.ast.constants.BasicType;
import rime.source.ast.constants.FunctionKind;
import rime.source.ast.declarations.PredefinedFunction;
import rime.source.ast.declarations.RootNode;
import rime.source.ast.types.*;
import rime.source.semantic.types.*;



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
	
	// Predefined functions
	public final PredefinedFunction main = new PredefinedFunction(
		FunctionKind.PROC, "main", null, Instances.STRING_LIST
	);
	
	public final PredefinedFunction print = new PredefinedFunction(
		FunctionKind.PROC, "print", null, Instances.STRING
	);
	
	public final PredefinedFunction parseInt = new PredefinedFunction(
		FunctionKind.FUNC, "parseInt", Instances.INT, Instances.STRING
	);
	
	public final PredefinedFunction list = new PredefinedFunction(
		FunctionKind.FUNC, "list", new ListTypeNode(Instances.ANY), Instances.TYPE
	);
	
	public final PredefinedFunction set = new PredefinedFunction(
		FunctionKind.FUNC, "set", new SetTypeNode(Instances.ANY), Instances.TYPE
	);
	
	public final PredefinedFunction dict = new PredefinedFunction(
		FunctionKind.FUNC, "dict", new DictTypeNode(Instances.ANY, Instances.ANY), Instances.TYPE, Instances.TYPE
	);
	
	public final PredefinedFunction length = new PredefinedFunction(
		FunctionKind.FUNC, "length", Instances.INT, new ListTypeNode(Instances.ANY)
	);
	
	public final PredefinedFunction append = new PredefinedFunction(
		FunctionKind.PROC, "append", null, new ListTypeNode(Instances.ANY), Instances.ANY
	);
	
	public final PredefinedFunction add = new PredefinedFunction(
		FunctionKind.PROC, "add", null, new SetTypeNode(Instances.ANY), Instances.ANY
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
		
		reactor.set(boolType.attr("type"), TypeType.INSTANCE);
		reactor.set(intType.attr("type"), TypeType.INSTANCE);
		reactor.set(stringType.attr("type"), TypeType.INSTANCE);
		reactor.set(typeType.attr("type"), TypeType.INSTANCE);
		reactor.set(voidType.attr("type"), TypeType.INSTANCE);
		
		reactor.set(boolType.attr("declared"), BoolType.INSTANCE);
		reactor.set(intType.attr("declared"), IntType.INSTANCE);
		reactor.set(stringType.attr("declared"), StringType.INSTANCE);
		reactor.set(typeType.attr("declared"), TypeType.INSTANCE);
		reactor.set(voidType.attr("declared"), VoidType.INSTANCE);
		
		// Predefined values
		declare(trueValue.name, trueValue);
		declare(falseValue.name, falseValue);
		declare(nullValue.name, nullValue);
		
		reactor.set(trueValue.attr("type"), BoolType.INSTANCE);
		reactor.set(falseValue.attr("type"), BoolType.INSTANCE);
		reactor.set(nullValue.attr("type"), NullType.INSTANCE);
		
		// Predefined functions
		declare(main.name, main);
		declare(print.name, print);
		declare(parseInt.name, parseInt);
		declare(list.name, list);
		declare(set.name, set);
		declare(dict.name, dict);
		declare(length.name, length);
		declare(append.name, append);
		declare(add.name, add);
		declare(contains.name, contains);
		
		reactor.set(main.attr("type"),
			new FunctionType(VoidType.INSTANCE, new ListType(StringType.INSTANCE))
		);
		
		reactor.set(print.attr("type"),
			new FunctionType(VoidType.INSTANCE, StringType.INSTANCE)
		);
		
		reactor.set(parseInt.attr("type"),
			new FunctionType(IntType.INSTANCE, StringType.INSTANCE)
		);
		
		reactor.set(list.attr("type"),
			new FunctionType(new ListType(AnyPrimitiveType.INSTANCE), TypeType.INSTANCE)
		);
		
		reactor.set(set.attr("type"),
			new FunctionType(new SetType(AnyPrimitiveType.INSTANCE), TypeType.INSTANCE)
		);
		
		reactor.set(dict.attr("type"),
			new FunctionType(
				new DictType(AnyPrimitiveType.INSTANCE, AnyPrimitiveType.INSTANCE),
				TypeType.INSTANCE, TypeType.INSTANCE
			)
		);
		
		reactor.set(length.attr("type"),
			new FunctionType(IntType.INSTANCE, new ListType(AnyPrimitiveType.INSTANCE))
		);
		
		reactor.set(append.attr("type"),
			new FunctionType(
				VoidType.INSTANCE, new ListType(AnyPrimitiveType.INSTANCE), AnyPrimitiveType.INSTANCE
			)
		);
		
		reactor.set(add.attr("type"),
			new FunctionType(
				VoidType.INSTANCE, new SetType(AnyPrimitiveType.INSTANCE), AnyPrimitiveType.INSTANCE
			)
		);
		
		reactor.set(contains.attr("type"),
			new FunctionType(
				BoolType.INSTANCE, new SetType(AnyPrimitiveType.INSTANCE), AnyPrimitiveType.INSTANCE
			)
		);
	}
}
