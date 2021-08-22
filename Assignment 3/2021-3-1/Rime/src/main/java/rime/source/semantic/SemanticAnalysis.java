package rime.source.semantic;

import norswap.uranium.Attribute;
import norswap.uranium.Reactor;
import norswap.uranium.Rule;
import norswap.utils.visitors.ReflectiveFieldWalker;
import norswap.utils.visitors.Walker;
import rime.source.ast.RimeNode;
import rime.source.ast.constants.BinaryOperator;
import rime.source.ast.constants.VariableKind;
import rime.source.ast.declarations.*;
import rime.source.ast.expressions.*;
import rime.source.ast.expressions.literals.BoolLiteral;
import rime.source.ast.expressions.literals.IntLiteral;
import rime.source.ast.expressions.literals.StringLiteral;
import rime.source.ast.statements.*;
import rime.source.ast.types.*;
import rime.source.semantic.scope.DeclarationContext;
import rime.source.semantic.scope.RootScope;
import rime.source.semantic.scope.Scope;
import rime.source.semantic.types.*;

import java.util.*;
import java.util.stream.IntStream;

import static norswap.utils.Util.cast;
import static norswap.utils.Vanilla.forEachIndexed;
import static norswap.utils.visitors.WalkVisitType.POST_VISIT;
import static norswap.utils.visitors.WalkVisitType.PRE_VISIT;
import static rime.source.ast.constants.BinaryOperator.*;
import static rime.source.ast.constants.UnaryOperator.*;
import static rime.source.semantic.AttributeName.*;



public final class SemanticAnalysis {
	private static final HashMap<Reactor, SemanticAnalysis> instances = new HashMap<>();
	private final Reactor R;
	private Scope scope;
	private RimeNode inferenceContext;
	
	private SemanticAnalysis(Reactor reactor) {
		this.R = reactor;
	}
	
	public static SemanticAnalysis getInstance(Reactor R) {
		return instances.get(R);
	}
	
	public static Walker<RimeNode> createWalker(Reactor reactor) {
		final ReflectiveFieldWalker<RimeNode> walker = new ReflectiveFieldWalker<>(RimeNode.class, PRE_VISIT, POST_VISIT);
		final SemanticAnalysis analysis = new SemanticAnalysis(reactor);
		
		instances.put(reactor, analysis);
		
		// IDENTIFIERS
		walker.register(Identifier.class, PRE_VISIT, analysis::identifier);
		
		// LITERALS
		walker.register(BoolLiteral.class, PRE_VISIT, analysis::boolLiteral);
		walker.register(IntLiteral.class, PRE_VISIT, analysis::intLiteral);
		walker.register(StringLiteral.class, PRE_VISIT, analysis::stringLiteral);
		
		// TYPES
		walker.register(PrimitiveType.class, PRE_VISIT, analysis::primitiveType);
		walker.register(ListTypeNode.class, PRE_VISIT, analysis::listType);
		walker.register(SetTypeNode.class, PRE_VISIT, analysis::setType);
		walker.register(DictTypeNode.class, PRE_VISIT, analysis::dictType);
		
		// EXPRESSIONS
		walker.register(IndexedCollectionAccess.class, PRE_VISIT, analysis::indexedCollectionAccess);
		walker.register(BinaryExpression.class, PRE_VISIT, analysis::binaryExpression);
		walker.register(UnaryExpression.class, PRE_VISIT, analysis::unaryExpression);
		walker.register(EmptyList.class, PRE_VISIT, analysis::emptyList);
		walker.register(EmptySet.class, PRE_VISIT, analysis::emptySet);
		walker.register(EmptyDict.class, PRE_VISIT, analysis::emptyDict);
		walker.register(ListElements.class, PRE_VISIT, analysis::listElements);
		walker.register(SetElements.class, PRE_VISIT, analysis::setElements);
		walker.register(DictElement.class, PRE_VISIT, analysis::dictElement);
		walker.register(DictElements.class, PRE_VISIT, analysis::dictElements);
		walker.register(Assignment.class, PRE_VISIT, analysis::assignment);
		walker.register(Parameter.class, PRE_VISIT, analysis::parameter);
		walker.register(Parameters.class, PRE_VISIT, analysis::parameters);
		walker.register(FunctionCall.class, PRE_VISIT, analysis::functionCall);
		
		// STATEMENTS
		walker.register(Block.class, PRE_VISIT, analysis::block);
		walker.register(ExpressionStatement.class, PRE_VISIT, analysis::expressionStatement);
		walker.register(EmptyStatement.class, PRE_VISIT, analysis::emptyStatement);
		walker.register(IfStatement.class, PRE_VISIT, analysis::ifStatement);
		walker.register(WhileStatement.class, PRE_VISIT, analysis::whileStatement);
		walker.register(ExitStatement.class, PRE_VISIT, analysis::exitStatement);
		walker.register(ReturnStatement.class, PRE_VISIT, analysis::returnStatement);
		
		walker.register(Block.class, POST_VISIT, analysis::popScope);
		
		// DECLARATIONS
		walker.register(VariableDefinition.class, PRE_VISIT, analysis::variableDefinition);
		walker.register(ProcDefinition.class, PRE_VISIT, analysis::procDefinition);
		walker.register(FuncDefinition.class, PRE_VISIT, analysis::funcDefinition);
		walker.register(EntryPoint.class, PRE_VISIT, analysis::entryPoint);
		walker.register(RootNode.class, PRE_VISIT, analysis::root);
		
		walker.register(ProcDefinition.class, POST_VISIT, analysis::popScope);
		walker.register(FuncDefinition.class, POST_VISIT, analysis::popScope);
		walker.register(RootNode.class, POST_VISIT, analysis::popScope);
		
		// FALLBACK
		walker.registerFallback(POST_VISIT, node -> {});
		
		return walker;
	}
	
	//region IDENTIFIERS
	private void identifier(Identifier node) {
		if (scope == null) {
			System.out.println("Error, cannot lookup in the scope, scope is null");
			return;
		}
		
		final DeclarationContext contextOrNull = scope.lookup(node.value);
		
		if (contextOrNull != null) {
			R.set(node.attr(DECL), contextOrNull.declaration);
			R.set(node.attr(SCOPE), contextOrNull.scope);
			
			R.rule(node.attr(TYPE))
				.using(contextOrNull.declaration.attr(TYPE))
				.by(Rule::copyFirst);
			
			return;
		}
		
		R.rule(node.attr(DECL), node.attr(SCOPE))
			.by(r -> {
				final DeclarationContext context = (scope != null) ? scope.lookup(node.value) : null;
				final Declaration declaration = (context != null) ? context.declaration : null;
				
				if (context == null) {
					r.errorFor("Could not resolve identifier: " + node.value, node, node.attr(DECL), node.attr(SCOPE), node.attr(TYPE));
				}
				else {
					r.set(node.attr(SCOPE), context.scope);
					r.set(node.attr(DECL), declaration);
					
					if (declaration instanceof VariableDefinition) {
						r.errorFor("Variable used before declaration: " + node.value, node, node.attr(TYPE));
					}
					else {
						R.rule(node.attr(TYPE))
							.using(declaration.attr(TYPE))
							.by(Rule::copyFirst);
					}
				}
			});
	}
	//endregion
	
	//region LITERALS
	private void boolLiteral(BoolLiteral node) {
		R.set(node.attr(TYPE), BoolType.INSTANCE);
	}
	
	private void intLiteral(IntLiteral node) {
		R.set(node.attr(TYPE), IntType.INSTANCE);
	}
	
	private void stringLiteral(StringLiteral node) {
		R.set(node.attr(TYPE), StringType.INSTANCE);
	}
	//endregion
	
	//region TYPES
	private void primitiveType(PrimitiveType node) {
		final Scope scope = this.scope;
		
		R.rule()
			.by(r -> {
				final DeclarationContext context = (scope != null) ? scope.lookup(node.name) : null;
				final Declaration decl = context == null ? null : context.declaration;
				
				if (context == null) {
					r.errorFor("could not resolve: " + node.name, node, node.attr(VALUE));
				}
				else {
					R.rule(node.attr(TYPE))
						.by(r2 -> r2.set(0, TypeType.INSTANCE));
					
					R.rule(node.attr(VALUE))
						.using(decl.attr(DECLARED))
						.by(Rule::copyFirst);
				}
			});
	}
	
	private void listType(ListTypeNode node) {
		R.rule(node.attr(VALUE))
			.using(node.type.attr(VALUE))
			.by(r -> r.set(0, new ListType(r.get(0))));
	}
	
	private void setType(SetTypeNode node) {
		R.rule(node.attr(VALUE))
			.using(node.type.attr(VALUE))
			.by(r -> r.set(0, new SetType(r.get(0))));
	}
	
	private void dictType(DictTypeNode node) {
		R.rule(node.attr(VALUE))
			.using(node.keyType.attr(VALUE), node.valueType.attr(VALUE))
			.by(r -> r.set(0, new DictType(r.get(0), r.get(1))));
	}
	//endregion
	
	//region EXPRESSIONS
	private void indexedCollectionAccess(IndexedCollectionAccess node) {
		R.rule()
			.using(node.index.attr(TYPE), node.identifier.attr(TYPE))
			.by(r -> {
				final Type indexType = r.get(0);
				final Type collectionType = r.get(1);
				
				if (collectionType instanceof ListType) {
					if (!(indexType instanceof IntType)) {
						r.error("Indexing an array using a non-Int-valued expression", node.index);
					}
				}
				else if (collectionType instanceof DictType) {
					final Type keyType = ((DictType) collectionType).keyType;
					final Type valueType = ((DictType) collectionType).valueType;
					
					if (keyType != indexType) {
						r.errorFor("Indexing a {" + keyType + ":" + valueType + "} dict using a key of type " + indexType, node.index);
					}
				}
			});
		
		R.rule(node.attr(TYPE))
			.using(node.identifier.attr(TYPE))
			.by(r -> {
				final Type type = r.get(0);
				
				if (type instanceof ListType) {
					r.set(0, ((ListType) type).elementType);
				}
				else if (type instanceof DictType) {
					r.set(0, ((DictType) type).valueType);
				}
				else {
					r.error("Trying to index a non-array expression of type " + type, node);
				}
			});
	}
	
	private void binaryExpression(BinaryExpression node) {
		R.rule(node.attr(TYPE))
			.using(node.left.attr(TYPE), node.right.attr(TYPE))
			.by(r -> {
				final Type left = r.get(0);
				final Type right = r.get(1);
				
				if (node.operator == ADD && (left instanceof StringType || right instanceof StringType)) {
					r.set(0, StringType.INSTANCE);
				}
				else if (isArithmeticOperator(node.operator)) {
					binaryArithmetic(r, node, left, right);
				}
				else if (isComparisonOperator(node.operator)) {
					binaryComparison(r, node, left, right);
				}
				else if (isLogicOperator(node.operator)) {
					binaryLogic(r, node, left, right);
				}
				else if (isEqualityOperator(node.operator)) {
					binaryEquality(r, node, left, right);
				}
			});
	}
	
	private void unaryExpression(UnaryExpression node) {
		if (node.operator == LOGICAL_COMPLEMENT) {
			R.set(node.attr(TYPE), BoolType.INSTANCE);
		}
		else if (node.operator == NEGATIVE) {
			R.set(node.attr(TYPE), IntType.INSTANCE);
		}
		
		R.rule()
			.using(node.attr(TYPE), node.operand.attr(TYPE))
			.by(r -> {
				final Type operatorType = r.get(0);
				final Type operandType = r.get(1);
				
				if (operatorType instanceof BoolType && !(operandType instanceof BoolType)) {
					r.error("Trying to use unary complement operator on type: " + operatorType, node);
				}
				else if (operatorType instanceof IntType && !(operandType instanceof IntType)) {
					r.error("Trying to use unary negative operator on type: " + operatorType, node);
				}
			});
	}
	
	private void emptyList(EmptyList node) {
		inferCollectionType(node);
	}
	
	private void emptySet(EmptySet node) {
		inferCollectionType(node);
	}
	
	private void emptyDict(EmptyDict node) {
		inferCollectionType(node);
	}
	
	private void listElements(ListElements node) {
		final Attribute[] dependencies = node.elements.stream().map(x -> x.attr(TYPE)).toArray(Attribute[]::new);
		
		R.rule(node.attr(TYPE))
			.using(dependencies)
			.by(r -> {
				final Type[] types = IntStream.range(0, dependencies.length).<Type>mapToObj(r::get).distinct().toArray(Type[]::new);
				
				int i = 0;
				Type supertype = null;
				
				for (Type type : types) {
					if (type instanceof VoidType) {
						r.errorFor("Void-valued expression in list literal", node.elements.get(i));
					}
					else if (supertype == null) {
						supertype = type;
					}
					else {
						supertype = commonSupertype(supertype, type);
						if (supertype == null) {
							r.error("Could not find common supertype in list literal", node);
							return;
						}
					}
					i++;
				}
				
				if (supertype == null) {
					r.error("Could not find common supertype in list literal: all members have void type", node);
				}
				else {
					r.set(0, new ListType(supertype));
				}
			});
	}
	
	private void setElements(SetElements node) {
		final Attribute[] dependencies = node.elements.stream().map(x -> x.attr(TYPE)).toArray(Attribute[]::new);
		
		R.rule(node.attr(TYPE))
			.using(dependencies)
			.by(r -> {
				final Type[] types = IntStream.range(0, dependencies.length).<Type>mapToObj(r::get).distinct().toArray(Type[]::new);
				
				int i = 0;
				Type supertype = null;
				
				for (Type type : types) {
					if (type instanceof VoidType) {
						r.errorFor("Void-valued expression in set literal", node.elements.get(i));
					}
					else if (supertype == null) {
						supertype = type;
					}
					else {
						supertype = commonSupertype(supertype, type);
						if (supertype == null) {
							r.error("Could not find common supertype in set literal", node);
							return;
						}
					}
					i++;
				}
				
				if (supertype == null) {
					r.error("Could not find common supertype in set literal: all members have void type", node);
				}
				else {
					r.set(0, new SetType(supertype));
				}
			});
	}
	
	private void dictElement(DictElement node) {
		R.rule(node.attr(TYPE))
			.using(node.key.attr(TYPE), node.value.attr(TYPE))
			.by(r -> {
				final Type keyType = r.get(0);
				final Type valueType = r.get(1);
				
				r.set(0, new DictType(keyType, valueType));
			});
	}
	
	private void dictElements(DictElements node) {
		final Attribute[] dependencies = node.elements.stream().map(x -> x.attr(TYPE)).toArray(Attribute[]::new);
		
		R.rule(node.attr(TYPE))
			.using(dependencies)
			.by(r -> {
				final Type[] types = IntStream.range(0, dependencies.length).<Type>mapToObj(r::get).distinct().toArray(Type[]::new);
				
				int i = 0;
				Type keySupertype = null;
				Type valueSupertype = null;
				
				for (Type type : types) {
					if (type instanceof VoidType) {
						r.errorFor("Void-valued expression in dict literal", node.elements.get(i));
					}
					else if (type instanceof DictType) {
						final DictType dictType = (DictType) type;
						final Type keyType = dictType.keyType;
						final Type valueType = dictType.valueType;
						
						if (keySupertype == null) {
							keySupertype = keyType;
						}
						else {
							keySupertype = commonSupertype(keySupertype, keyType);
							if (keySupertype == null) {
								r.error("Could not find common supertype in dict key literal", node);
								return;
							}
						}
						
						if (valueSupertype == null) {
							valueSupertype = valueType;
						}
						else {
							valueSupertype = commonSupertype(valueSupertype, valueType);
							if (valueSupertype == null) {
								r.error("Could not find common supertype in dict value literal", node);
								return;
							}
						}
					}
					i++;
				}
				
				if (keySupertype == null || valueSupertype == null) {
					r.error("Could not find common supertype in dict literal: all members have void type", node);
				}
				else {
					r.set(0, new DictType(keySupertype, valueSupertype));
				}
			});
	}
	
	private void assignment(Assignment node) {
		R.rule(node.attr(TYPE))
			.using(node.left.attr(TYPE), node.right.attr(TYPE))
			.by(r -> {
				final Type left = r.get(0);
				final Type right = r.get(1);
				
				r.set(0, r.get(0));
				
				if (node.left instanceof Identifier && R.get(node.left.attr(VAR_KIND)) == null) { // the variable has already been declared & assigned
					final Declaration decl = R.get(node.left.attr(DECL));
					
					if (decl instanceof Parameter) {
						r.errorFor("Trying to reassign a value to function parameter", node);
						return;
					}
					
					final VariableDefinition def = R.get(node.left.attr(DECL));
					
					if (def.variableKind == VariableKind.VAL) {
						r.errorFor("Trying to assign a value to already assigned single-assignment variable", node);
					}
				}
				
				if (node.left instanceof Identifier || node.left instanceof IndexedCollectionAccess) {
					if (!areCompatible(left, right)) {
						r.errorFor("Trying to assign a value to a non-compatible l-value", node);
					}
				}
				else {
					r.errorFor("Trying to assign to an non-l-value expression", node.left);
				}
			});
	}
	
	private void parameter(Parameter node) {
		if (scope == null) {
			System.out.println("Error, cannot declare parameter, scope is null");
			return;
		}
		
		scope.declare(node.identifier.value, node);
		
		R.rule(node.attr(TYPE))
			.using(node.type.attr(VALUE))
			.by(Rule::copyFirst);
	}
	
	private void parameters(Parameters node) {
		if (scope == null) {
			System.out.println("Error, cannot declare parameters, scope is null");
		}
	}
	
	private void functionCall(FunctionCall node) {
		this.inferenceContext = node;
		
		final Attribute[] dependencies = new Attribute[node.arguments.size() + 1];
		dependencies[0] = node.name.attr(TYPE);
		
		forEachIndexed(node.arguments, (i, arg) -> {
			dependencies[i + 1] = arg.attr(TYPE);
			R.set(arg.attr(INDEX), i);
		});
		
		R.rule(node.attr(TYPE))
			.using(dependencies)
			.by(r -> {
				final Type maybeFunctionType = r.get(0);
				
				if (!(maybeFunctionType instanceof FunctionType)) {
					r.error("Trying to call a non-function expression: " + node.name, node.name);
					return;
				}
				
				final FunctionType functionType = cast(maybeFunctionType);
				r.set(0, functionType.returnType);
				
				final Type[] params = functionType.paramTypes;
				final List<Expression> args = node.arguments;
				
				if (params.length != args.size()) {
					r.errorFor("Wrong number of arguments, expected " + params.length + " but got " + args.size(), node);
				}
				
				final int checkedArgs = Math.min(params.length, args.size());
				
				for (int i = 0; i < checkedArgs; ++i) {
					final Type argType = r.get(i + 1);
					final Type paramType = functionType.paramTypes[i];
					
					if (!areCompatible(argType, paramType)) {
						r.errorFor("Incompatible argument provided for argument " + i + ": expected " + paramType + " but got " + argType, node.arguments.get(i));
					}
				}
			});
	}
	//endregion
	
	//region STATEMENTS
	private void emptyStatement(EmptyStatement node) { }
	
	private void expressionStatement(ExpressionStatement node) {
		if (!(node.expression instanceof FunctionCall)) {
			System.out.println("Error, should be a function call");
		}
	}
	
	private void ifStatement(IfStatement node) {
		R.rule()
			.using(node.condition.attr(TYPE))
			.by(r -> {
				final Type type = r.get(0);
				
				if (!(type instanceof BoolType)) {
					r.error("If statement with a non-boolean condition of type: " + type, node.condition);
				}
			});
		
		final Attribute[] returnDependencies = getNodesThatCanReturn(new ArrayList<>() {{
			add(node.trueBody);
		}});
		
		R.rule(node.attr(RETURNS))
			.using(returnDependencies)
			.by(r -> r.set(0, returnDependencies.length == 1 && Arrays.stream(returnDependencies).allMatch(r::get)));
		
		final Attribute[] exitDependencies = getNodesThatCanExit(new ArrayList<>() {{
			add(node.trueBody);
		}});
		
		R.rule(node.attr(EXITS))
			.using(exitDependencies)
			.by(r -> r.set(0, exitDependencies.length == 1 && Arrays.stream(exitDependencies).allMatch(r::get)));
	}
	
	private void whileStatement(WhileStatement node) {
		R.rule()
			.using(node.condition.attr(TYPE))
			.by(r -> {
				final Type type = r.get(0);
				
				if (!(type instanceof BoolType)) {
					r.error("While statement with a non-boolean condition of type: " + type, node.condition);
				}
			});
	}
	
	private void exitStatement(ExitStatement node) {
		R.set(node.attr(EXITS), true);
		R.set(node.attr(RETURNS), false);
		
		final FunctionDefinition def = currentFunction();
		
		if (def == null) {
			return;
		}
		
		if (def instanceof FuncDefinition) {
			R.rule().by(r -> r.error("Exit statement in a func definition", node));
		}
	}
	
	private void returnStatement(ReturnStatement node) {
		R.set(node.attr(EXITS), false);
		R.set(node.attr(RETURNS), true);
		
		final FunctionDefinition def = currentFunction();
		
		if (def == null) {
			return;
		}
		
		if (def instanceof ProcDefinition) {
			R.rule().by(r -> r.error("Return statement in a proc definition", node));
			return;
		}
		
		final FuncDefinition function = (FuncDefinition) def;
		
		if (node.expression == null) {
			R.rule()
				.using(function.returnType.attr(VALUE))
				.by(r -> {
					final Type returnType = r.get(0);
					
					if (!(returnType instanceof VoidType)) {
						r.error("Return without value in a func with a return type", node);
					}
				});
		}
		else {
			R.rule()
				.using(function.returnType.attr(VALUE), node.expression.attr(TYPE))
				.by(r -> {
					final Type formal = r.get(0);
					final Type actual = r.get(1);
					
					if (formal instanceof VoidType) {
						r.error("Return with value in a Void func", node);
					}
					else if (!areCompatible(actual, formal)) {
						r.errorFor("Incompatible return type, expected " + formal + " but got " + actual, node.expression);
					}
				});
		}
	}
	
	private void block(Block node) {
		scope = new Scope(node, scope);
		R.set(node, SCOPE.name(), scope);
		
		final Attribute[] returnDependencies = getNodesThatCanReturn(node.statements);
		// If any of the nodes contained in `node` returns, then `node` returns
		R.rule(node.attr(RETURNS))
			.using(returnDependencies)
			.by(r -> r.set(0, returnDependencies.length != 0 && Arrays.stream(returnDependencies).anyMatch(r::get)));
		
		final Attribute[] exitDependencies = getNodesThatCanExit(node.statements);
		// If any of the nodes contained in `node` exits, then `node` exits
		R.rule(node.attr(EXITS))
			.using(exitDependencies)
			.by(r -> r.set(0, exitDependencies.length != 0 && Arrays.stream(exitDependencies).anyMatch(r::get)));
	}
	//endregion
	
	//region DECLARATIONS
	private void variableDefinition(VariableDefinition node) {
		if (scope == null) {
			System.out.println("Error, cannot define variable, scope is null");
			return;
		}
		
		this.inferenceContext = node;
		
		if (!(node.assignment.left instanceof Identifier)) {
			R.rule().by(r -> r.error("Trying to declare a variable with a non-compatible l-value identifier", node));
			return;
		}
		
		final Identifier identifier = (Identifier) node.assignment.left;
		
		if (scope.lookup(identifier.value) != null) {
			R.rule().by(r -> r.error("Trying to redeclare an already existing variable", node));
			return;
		}
		
		scope.declare(identifier.value, node);
		R.set(node.attr(SCOPE), scope);
		
		R.set(identifier.attr(VAR_KIND), node.variableKind);
		
		R.rule(node.attr(TYPE))
			.using(node.type.attr(VALUE))
			.by(Rule::copyFirst);
		
		R.rule()
			.using(node.type.attr(VALUE), node.assignment.right.attr(TYPE))
			.by(r -> {
				final Type expected = r.get(0);
				final Type actual = r.get(1);
				
				if (!areCompatible(actual, expected)) {
					r.error("Incompatible initializer type provided for variable `" + identifier.value + "`: expected " + expected + " but got " + actual, node.assignment.right);
				}
			});
	}
	
	private void procDefinition(ProcDefinition node) {
		if (scope == null) {
			System.out.println("Error, cannot define procedure, scope is null");
			return;
		}
		
		scope.declare(node.name.value, node);
		scope = new Scope(node, scope);
		R.set(node, SCOPE.name(), scope);
		
		final Attribute[] dependencies = new Attribute[node.parameters.count];
		forEachIndexed(node.parameters.params, (i, param) ->
			dependencies[i] = param.attr(TYPE));
		
		R.rule(node.attr(TYPE))
			.using(dependencies)
			.by(r -> {
				final Type[] paramTypes = new Type[node.parameters.count];
				
				for (int i = 0; i < paramTypes.length; ++i) {
					paramTypes[i] = r.get(i);
				}
				
				r.set(0, new FunctionType(VoidType.INSTANCE, paramTypes));
			});
	}
	
	private void funcDefinition(FuncDefinition node) {
		if (scope == null) {
			System.out.println("Error, cannot define function, scope is null");
			return;
		}
		
		scope.declare(node.name.value, node);
		scope = new Scope(node, scope);
		R.set(node, SCOPE.name(), scope);
		
		final Attribute[] dependencies = new Attribute[node.parameters.count + 1];
		dependencies[0] = node.returnType.attr(VALUE);
		forEachIndexed(node.parameters.params, (i, param) ->
			dependencies[i + 1] = param.attr(TYPE));
		
		R.rule(node.attr(TYPE))
			.using(dependencies)
			.by(r -> {
				final Type[] paramTypes = new Type[node.parameters.count];
				
				for (int i = 0; i < paramTypes.length; ++i) {
					paramTypes[i] = r.get(i + 1);
				}
				
				r.set(0, new FunctionType(r.get(0), paramTypes));
			});
		
		R.rule()
			.using(node.body.attr(RETURNS), node.returnType.attr(VALUE))
			.by(r -> {
				final boolean returns = r.get(0);
				final Type returnType = r.get(1);
				
				if (!returns && !(returnType instanceof VoidType)) {
					r.error("Missing return in function", node);
				}
			});
	}
	
	private void entryPoint(EntryPoint node) {}
	
	private void root(RootNode node) {
		scope = new RootScope(node, R);
		R.set(node, SCOPE.name(), scope);
	}
	//endregion
	
	//region UTILS
	private void popScope(RimeNode node) {
		if (scope == null) {
			System.out.println("Error, cannot pop scope, scope is null");
			return;
		}
		
		scope = scope.parent;
	}
	
	private void binaryArithmetic(Rule r, BinaryExpression node, Type left, Type right) {
		if (left instanceof IntType) {
			if (right instanceof IntType) {
				r.set(0, IntType.INSTANCE);
			}
			else {
				r.error("Trying to " + node.operator.name().toLowerCase() + " " + left + " with " + right, node);
			}
		}
		else {
			r.error("Trying to " + node.operator.name().toLowerCase() + " " + left + " with " + right, node);
		}
	}
	
	private void binaryComparison(Rule r, BinaryExpression node, Type left, Type right) {
		r.set(0, BoolType.INSTANCE);
		
		if (!(right instanceof IntType)) {
			r.errorFor("Attempting to perform arithmetic comparison on non-numeric type: " + right, node.right);
		}
		
		if (!(left instanceof IntType)) {
			r.errorFor("Attempting to perform arithmetic comparison on non-numeric type: " + left, node.left);
		}
	}
	
	private void binaryEquality(Rule r, BinaryExpression node, Type left, Type right) {
		r.set(0, BoolType.INSTANCE);
		
		if (!isComparableTo(left, right)) {
			r.errorFor("Trying to compare incomparable types " + left + " and " + right, node);
		}
	}
	
	private void binaryLogic(Rule r, BinaryExpression node, Type left, Type right) {
		r.set(0, BoolType.INSTANCE);
		
		if (!(left instanceof BoolType)) {
			r.errorFor("Attempting to perform binary logic on non-boolean type: " + left, node.left);
		}
		
		if (!(right instanceof BoolType)) {
			r.errorFor("Attempting to perform binary logic on non-boolean type: " + right, node.right);
		}
	}
	
	private void inferCollectionType(RimeNode node) {
		final RimeNode context = this.inferenceContext;
		
		if (context instanceof VariableDefinition) {
			R.rule(node.attr(TYPE))
				.using(((VariableDefinition) context).assignment.left.attr(TYPE))
				.by(Rule::copyFirst);
		}
		else if (context instanceof FunctionCall) {
			R.rule(node.attr(TYPE))
				.using(((FunctionCall) context).name.attr(TYPE), node.attr(INDEX))
				.by(r -> {
					final FunctionType functionType = r.get(0);
					r.set(0, functionType.paramTypes[(int) r.get(1)]);
				});
		}
	}
	
	private FunctionDefinition currentFunction() {
		Scope scope = this.scope;
		
		while (scope != null) {
			final RimeNode node = scope.node;
			
			if (node instanceof FunctionDefinition) {
				return (FunctionDefinition) node;
			}
			
			scope = scope.parent;
		}
		
		return null;
	}
	
	private static boolean isArithmeticOperator(BinaryOperator op) {
		return op == ADD || op == MULTIPLY || op == SUBTRACT || op == DIVIDE || op == REMAINDER;
	}
	
	private static boolean isComparisonOperator(BinaryOperator op) {
		return op == GREATER_THAN || op == GREATER_THAN_EQUAL || op == LESS_THAN || op == LESS_THAN_EQUAL;
	}
	
	private static boolean isLogicOperator(BinaryOperator op) {
		return op == LOGICAL_OR || op == LOGICAL_AND;
	}
	
	private static boolean isEqualityOperator(BinaryOperator op) {
		return op == EQUAL_TO || op == NOT_EQUAL_TO;
	}
	
	private static boolean isComparableTo(Type a, Type b) {
		if (a instanceof VoidType || b instanceof VoidType) {
			return false;
		}
		else if (a.equals(b)) {
			return true;
		}
		else {
			return a.isReference() && b.isReference();
		}
	}
	
	private static boolean areCompatible(Type a, Type b) {
		if (a instanceof VoidType || b instanceof VoidType) {
			return false;
		}
		else if (a.equals(b)) {
			return true;
		}
		else if (a instanceof AnyPrimitiveType && (b instanceof BoolType || b instanceof IntType || b instanceof StringType)) {
			return true;
		}
		else if (b instanceof AnyPrimitiveType && (a instanceof BoolType || a instanceof IntType || a instanceof StringType)) {
			return true;
		}
		else if (a instanceof ListType && b instanceof ListType) {
			return areCompatible(((ListType) a).elementType, ((ListType) b).elementType);
		}
		else if (a instanceof SetType && b instanceof SetType) {
			return areCompatible(((SetType) a).elementType, ((SetType) b).elementType);
		}
		else if (a instanceof DictType && b instanceof DictType) {
			return areCompatible(((DictType) a).keyType, ((DictType) b).keyType) &&
				areCompatible(((DictType) a).valueType, ((DictType) b).valueType);
		}
		else {
			return a instanceof NullType && b.isReference();
		}
	}
	
	private static boolean isExitContainer(RimeNode node) {
		return node instanceof Block ||
			node instanceof IfStatement ||
			node instanceof ExitStatement;
	}
	
	private static boolean isReturnContainer(RimeNode node) {
		return node instanceof Block ||
			node instanceof IfStatement ||
			node instanceof ReturnStatement;
	}
	
	private static Attribute[] getNodesThatCanExit(List<? extends RimeNode> children) {
		return children.stream()
			.filter(Objects::nonNull)
			.filter(SemanticAnalysis::isExitContainer)
			.map(child -> child.attr(EXITS))
			.toArray(Attribute[]::new);
	}
	
	private static Attribute[] getNodesThatCanReturn(List<? extends RimeNode> children) {
		return children.stream()
			.filter(Objects::nonNull)
			.filter(SemanticAnalysis::isReturnContainer)
			.map(child -> child.attr(RETURNS))
			.toArray(Attribute[]::new);
	}
	
	private static Type commonSupertype(Type a, Type b) {
		if (a instanceof VoidType || b instanceof VoidType) {
			return null;
		}
		else if (areCompatible(a, b)) {
			return b;
		}
		else if (areCompatible(b, a)) {
			return a;
		}
		else {
			return null;
		}
	}
	//endregion
}
