package rime.source.semantic;

import norswap.uranium.Attribute;
import norswap.uranium.Reactor;
import norswap.uranium.Rule;
import norswap.uranium.SemanticError;
import norswap.utils.visitors.ReflectiveFieldWalker;
import norswap.utils.visitors.Walker;
import rime.source.ast.RimeNode;
import rime.source.ast.constants.BinaryOperator;
import rime.source.ast.declarations.*;
import rime.source.ast.expressions.*;
import rime.source.ast.expressions.literals.BoolLiteral;
import rime.source.ast.expressions.literals.IntLiteral;
import rime.source.ast.expressions.literals.StringLiteral;
import rime.source.ast.statements.*;
import rime.source.ast.types.DictTypeNode;
import rime.source.ast.types.ListTypeNode;
import rime.source.ast.types.PrimitiveType;
import rime.source.ast.types.SetTypeNode;
import rime.source.semantic.scope.DeclarationContext;
import rime.source.semantic.scope.RootScope;
import rime.source.semantic.scope.Scope;
import rime.source.semantic.types.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static norswap.utils.Util.cast;
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
		walker.register(Parameters.class, PRE_VISIT, node -> {});
		walker.register(FunctionCall.class, PRE_VISIT, analysis::functionCall);
		
		// STATEMENTS
		walker.register(Block.class, PRE_VISIT, analysis::block);
		walker.register(IfStatement.class, PRE_VISIT, analysis::ifStatement);
		walker.register(ElseStatement.class, PRE_VISIT, analysis::elseStatement);
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
		final Scope scope = this.scope;
		final DeclarationContext contextOrNull = (scope != null) ? scope.lookup(node.value) : null;
		
		if (contextOrNull != null) {
			R.set(node.attr(DECL.name()), contextOrNull.declaration);
			R.set(node.attr(SCOPE.name()), contextOrNull.scope);
			
			R.rule(node.attr(TYPE.name()))
				.using(contextOrNull.declaration.attr(TYPE.name()))
				.by(Rule::copyFirst);
			
			return;
		}
		
		R.rule(node.attr(DECL.name()), node.attr(SCOPE.name()))
			.by(r -> {
				final DeclarationContext context = (scope != null) ? scope.lookup(node.value) : null;
				final Declaration decl = (context == null) ? null : context.declaration;
				
				if (context == null) {
					r.errorFor("Could not resolve: " + node.value, node, node.attr(DECL.name()), node.attr(SCOPE.name()), node.attr(TYPE.name()));
				}
				else {
					r.set(node.attr(SCOPE.name()), context.scope);
					r.set(node.attr(DECL.name()), decl);
					
					if (decl instanceof VariableDefinition) {
						r.errorFor("Variable used before declaration: " + node.value, node, node.attr(TYPE.name()));
					}
					else {
						R.rule(node.attr(TYPE.name()))
							.using(decl.attr(TYPE.name()))
							.by(Rule::copyFirst);
					}
				}
			});
	}
	//endregion
	
	//region LITERALS
	private void boolLiteral(BoolLiteral node) {
		R.set(node.attr(TYPE.name()), BoolType.INSTANCE);
	}
	
	private void intLiteral(IntLiteral node) {
		R.set(node.attr(TYPE.name()), IntType.INSTANCE);
	}
	
	private void stringLiteral(StringLiteral node) {
		R.set(node.attr(TYPE.name()), StringType.INSTANCE);
	}
	//endregion
	
	//region TYPES
	private void primitiveType(PrimitiveType node) {
		final Scope scope = this.scope;
		
		R.rule()
			.by(r -> {
				final String typeName = node.type.name().toLowerCase();
				final DeclarationContext contextOrNull = (scope != null) ? scope.lookup(typeName) : null;
				final Declaration decl = (contextOrNull == null) ? null : contextOrNull.declaration;
				
				if (contextOrNull == null) {
					r.errorFor("Could not resolve: " + node.type.name().toLowerCase(), node, node.attr(VALUE.name()));
				}
				else {
					R.rule(node.attr(VALUE.name()))
						.using(decl.attr("declared"))
						.by(Rule::copyFirst);
				}
			});
	}
	
	private void listType(ListTypeNode node) {
		R.rule(node.attr(VALUE.name()))
			.using(node.type.attr(VALUE.name()))
			.by(r -> r.set(0, new ListType(r.get(0))));
	}
	
	private void setType(SetTypeNode node) {
		R.rule(node.attr(VALUE.name()))
			.using(node.type.attr(VALUE.name()))
			.by(r -> r.set(0, new SetType(r.get(0))));
	}
	
	private void dictType(DictTypeNode node) {
		R.rule(node.attr(VALUE.name()))
			.using(node.keyType.attr(VALUE.name()), node.valueType.attr(VALUE.name()))
			.by(r -> r.set(0, new DictType(r.get(0), r.get(1))));
	}
	//endregion
	
	//region EXPRESSIONS
	private void indexedCollectionAccess(IndexedCollectionAccess node) {
		R.rule()
			.using(node.identifier.attr(TYPE.name()))
			.by(r -> {
				final Type operandType = r.get(0);
				
				if (!(operandType instanceof ListType || operandType instanceof DictType)) {
					r.error("Trying to index a non-indexed-collection expression of type " + operandType, node);
				}
			});
		
		R.rule()
			.using(node.identifier.attr(TYPE.name()), node.index.attr(TYPE.name()))
			.by(r -> {
				final Type operandType = r.get(0);
				final Type indexType = r.get(1);
				
				if (operandType instanceof ListType && !(indexType instanceof IntType)) {
					r.error("Indexing a list using a non-int-valued expression", node.index);
				}
				else if (operandType instanceof DictType) {
					if (!(indexType instanceof BoolType || indexType instanceof IntType || indexType instanceof StringType)) {
						r.error("Indexing a dict using a non-primitive-valued expression", node.index);
					}
				}
				else {
					r.error("Trying to index a non-indexed-collection expression of type " + operandType, node);
				}
			});
	}
	
	private void binaryExpression(BinaryExpression node) {
		R.rule(node.attr(TYPE.name()))
			.using(node.left.attr(TYPE.name()), node.right.attr(TYPE.name()))
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
	
	private void unaryExpression(UnaryExpression node) {
		if (node.operator == LOGICAL_COMPLEMENT) {
			R.set(node.attr(TYPE.name()), BoolType.INSTANCE);
			
			R.rule()
				.using(node.operand.attr(TYPE.name()))
				.by(r -> {
					final Type operandType = r.get(0);
					
					if (!(operandType instanceof BoolType)) {
						r.error("Trying to complement type: " + operandType, node);
					}
				});
		}
		else if (node.operator == NEGATIVE) {
			R.set(node.attr(TYPE.name()), IntType.INSTANCE);
			
			R.rule()
				.using(node.operand.attr(TYPE.name()))
				.by(r -> {
					final Type operandType = r.get(0);
					
					if (!(operandType instanceof IntType)) {
						r.error("Trying to negate type: " + operandType, node);
					}
				});
		}
	}
	
	private void emptyList(EmptyList node) {
		final Type type = getPrimitiveSemanticTypeFromASTType(node.type);
		R.set(node.attr(TYPE.name()), new ListType(type));
	}
	
	private void emptySet(EmptySet node) {
		final Type type = getPrimitiveSemanticTypeFromASTType(node.type);
		R.set(node.attr(TYPE.name()), new SetType(type));
	}
	
	private void emptyDict(EmptyDict node) {
		final Type keyType = getPrimitiveSemanticTypeFromASTType(node.keyType);
		final Type valueType = getPrimitiveSemanticTypeFromASTType(node.valueType);
		R.set(node.attr(TYPE.name()), new DictType(keyType, valueType));
	}
	
	private void listElements(ListElements node) {
		if (node.elements.size() == 0) {
			inferCollectionType(node);
			return;
		}
		
		Attribute[] dependencies = node.elements.stream().map(it -> it.attr(TYPE.name())).toArray(Attribute[]::new);
		
		R.rule(node.attr(TYPE.name()))
			.using(dependencies)
			.by(r -> {
				final Type[] types = IntStream.range(0, dependencies.length).<Type>mapToObj(r::get).distinct().toArray(Type[]::new);
				Type supertype = null;
				int i = 0;
				
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
		if (node.elements.size() == 0) {
			inferCollectionType(node);
			return;
		}
		
		Attribute[] contextOrNull = node.elements.stream().map(it -> it.attr(TYPE.name())).toArray(Attribute[]::new);
		
		R.rule(node.attr(TYPE.name()))
			.using(contextOrNull)
			.by(r -> {
				final Type[] types = IntStream.range(0, contextOrNull.length).<Type>mapToObj(r::get).distinct().toArray(Type[]::new);
				Type supertype = null;
				int i = 0;
				
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
		// TODO
	}
	
	private void dictElements(DictElements node) {
		// TODO
	}
	
	private void assignment(Assignment node) {
		R.rule(node.attr(TYPE.name()))
			.using(node.left.attr(TYPE.name()), node.right.attr(TYPE.name()))
			.by(r -> {
				final Type left = r.get(0);
				final Type right = r.get(1);
				
				r.set(0, r.get(1));
				
				if (node.left instanceof Identifier || node.left instanceof IndexedCollectionAccess) {
					if (!areCompatible(right, left)) {
						r.errorFor("Trying to assign a value to a non-compatible l-value", node);
					}
				}
				else {
					r.errorFor("Trying to assign to an non-l-value expression", node.left);
				}
			});
	}
	
	private void parameter(Parameter node) {
		final ParameterDecl declNode = new ParameterDecl(node);
		
		scope.declare(node.identifier.value, declNode);
		
		R.rule(node.attr(TYPE.name()))
			.using(node.type.attr(VALUE.name()))
			.by(Rule::copyFirst);
	}
	
	private void functionCall(FunctionCall node) {
		this.inferenceContext = node;
		
		final Attribute[] dependencies = new Attribute[node.arguments.size() + 1];
		dependencies[0] = node.name.attr(TYPE.name());
		
		for (int i = 0; i < node.arguments.size(); i++) {
			final RimeNode arg = node.arguments.get(i);
			dependencies[i + 1] = arg.attr(TYPE.name());
			R.set(arg.attr(INDEX.name()), i);
		}
		
		R.rule(node.attr(TYPE.name()))
			.using(dependencies)
			.by(r -> {
				final Type functionTypeOrNull = r.get(0);
				
				if (!(functionTypeOrNull instanceof FunctionType)) {
					r.error("trying to call a non-function expression: " + node.name, node.name);
					return;
				}
				
				final FunctionType functionType = cast(functionTypeOrNull);
				final Type[] params = functionType.paramTypes;
				final List<RimeNode> args = node.arguments;
				
				r.set(0, functionType.returnType);
				
				if (params.length != args.size()) {
					r.errorFor("Wrong number of arguments, expected " + params.length + " but got " + args.size(), node);
				}
				
				final int checkedArgs = Math.min(params.length, args.size());
				
				for (int i = 0; i < checkedArgs; ++i) {
					final Type argType = r.get(i + 1);
					final Type paramType = functionType.paramTypes[i];
					
					if (!areCompatible(argType, paramType)) {
						r.errorFor("incompatible argument provided for argument " + i + ": expected " + paramType + " but got " + argType, node.arguments.get(i));
					}
				}
			});
	}
	//endregion
	
	//region STATEMENTS
	private void ifStatement(IfStatement node) {
		R.rule()
			.using(node.condition.attr(TYPE.name()))
			.by(r -> {
				final Type type = r.get(0);
				
				if (!(type instanceof BoolType)) {
					r.error("If statement with a non-boolean condition of type: " + type, node.condition);
				}
			});
		
		final Attribute[] returnsDependencies = getReturnsDependencies(node.body.statements);
		
		R.rule(node.attr(RETURNS.name()))
			.using(returnsDependencies)
			.by(r -> r.set(0, Arrays.stream(returnsDependencies).allMatch(r::get)));
		
		final Attribute[] exitsDependencies = getExitsDependencies(node.body.statements);
		
		R.rule(node.attr(EXITS.name()))
			.using(exitsDependencies)
			.by(r -> r.set(0, Arrays.stream(exitsDependencies).allMatch(r::get)));
	}
	
	private void elseStatement(ElseStatement node) {
		final Attribute[] returnsDependencies = getReturnsDependencies(node.body.statements);
		
		R.rule(node.attr(RETURNS.name()))
			.using(returnsDependencies)
			.by(r -> r.set(0, Arrays.stream(returnsDependencies).allMatch(r::get)));
		
		final Attribute[] exitsDependencies = getExitsDependencies(node.body.statements);
		
		R.rule(node.attr(EXITS.name()))
			.using(exitsDependencies)
			.by(r -> r.set(0, Arrays.stream(exitsDependencies).allMatch(r::get)));
	}
	
	private void whileStatement(WhileStatement node) {
		R.rule()
			.using(node.condition.attr(TYPE.name()))
			.by(r -> {
				final Type type = r.get(0);
				
				if (!(type instanceof BoolType)) {
					r.error("While statement with a non-boolean condition of type: " + type, node.condition);
				}
			});
	}
	
	private void exitStatement(ExitStatement node) {
		R.set(node.attr(EXITS.name()), true);
		R.set(node.attr(RETURNS.name()), false);
		
		final FunctionDefinition function = currentFunction();
		
		if (function == null) {
			return;
		}
		
		if (function instanceof FuncDefinition) {
			R.error(new SemanticError("Func definition contains an exit statement", null, node));
		}
	}
	
	private void returnStatement(ReturnStatement node) {
		R.set(node.attr(EXITS.name()), false);
		R.set(node.attr(RETURNS.name()), true);
		
		final FunctionDefinition function = currentFunction();
		
		if (function == null) {
			return;
		}
		
		if (function instanceof ProcDefinition) {
			R.error(new SemanticError("Proc definition contains a return statement", null, node));
		}
		
		final FuncDefinition func;
		
		if (function instanceof FuncDefinition) {
			func = (FuncDefinition) function;
		}
		else {
			return;
		}
		
		if (node.expression == null) {
			R.rule()
				.using(func.returnType.attr(VALUE.name()))
				.by(r -> {
					final Type returnType = r.get(0);
					
					if (!(returnType instanceof VoidType)) {
						r.error("Return without value in a function with a return type", node);
					}
				});
		}
		else {
			R.rule()
				.using(func.returnType.attr(VALUE.name()), node.expression.attr(TYPE.name()))
				.by(r -> {
					final Type formal = r.get(0);
					final Type actual = r.get(1);
					
					if (formal instanceof VoidType) {
						r.error("Return with value in a Void function", node);
					}
					else if (!areCompatible(actual, formal)) {
						r.errorFor("Incompatible return type, expected " + formal + " but got " + actual, node.expression);
					}
				});
		}
	}
	
	private void block(Block node) {
		scope = new Scope(node, scope);
		R.set(node.attr(SCOPE.name()), scope);
		
		final Attribute[] returnsDependencies = getReturnsDependencies(node.statements);
		
		R.rule(node.attr(RETURNS.name()))
			.using(returnsDependencies)
			.by(r -> r.set(0, returnsDependencies.length != 0 && Arrays.stream(returnsDependencies).allMatch(r::get)));
		
		final Attribute[] exitsDependencies = getExitsDependencies(node.statements);
		
		R.rule(node.attr(EXITS.name()))
			.using(exitsDependencies)
			.by(r -> r.set(0, exitsDependencies.length != 0 && Arrays.stream(exitsDependencies).allMatch(r::get)));
	}
	//endregion
	
	//region DECLARATIONS
	private void variableDefinition(VariableDefinition node) {
		if (scope == null) {
			return;
		}
		
		this.inferenceContext = node;
		final String identifier = ((Identifier) node.assignment.left).value;
		
		scope.declare(identifier, node);
		R.set(node.attr(SCOPE.name()), scope);
		
		R.rule(node.attr(TYPE.name()))
			.using(node.type.attr(VALUE.name()))
			.by(Rule::copyFirst);
		
		R.rule()
			.using(node.type.attr(VALUE.name()), node.assignment.right.attr(TYPE.name()))
			.by(r -> {
				final Type expected = r.get(0);
				final Type actual = r.get(1);
				
				if (!areCompatible(actual, expected)) {
					r.error("Incompatible initializer type provided for variable " + identifier + " : expected " + expected + " but got " + actual, node.assignment.right);
				}
			});
	}
	
	private void procDefinition(ProcDefinition node) {
		if (scope == null) {
			return;
		}
		
		scope.declare(node.name.value, node);
		scope = new Scope(node, scope);
		R.set(node.attr(SCOPE.name()), scope);
		
		final Attribute[] dependencies = new Attribute[node.parameters.count + 1];
		
		for (int i = 0; i < node.parameters.count; i++) {
			dependencies[i] = node.parameters.params.get(i).attr(TYPE.name());
		}
		
		R.rule(node.attr(TYPE.name()))
			.using(dependencies)
			.by(r -> {
				final Type[] paramTypes = new Type[node.parameters.count];
				
				for (int i = 0; i < paramTypes.length; i++) {
					paramTypes[i] = r.get(i);
				}
				
				r.set(0, new FunctionType(VoidType.INSTANCE, paramTypes));
			});
		
		R.rule()
			.using(node.body.attr(RETURNS.name()))
			.by(r -> {
				final boolean returns = r.get(0);
				
				if (returns) {
					r.error("Return statement in proc definition", node);
				}
			});
	}
	
	private void funcDefinition(FuncDefinition node) {
		if (scope == null) {
			return;
		}
		
		scope.declare(node.name.value, node);
		scope = new Scope(node, scope);
		R.set(node.attr(SCOPE.name()), scope);
		
		final Attribute[] dependencies = new Attribute[node.parameters.count + 1];
		dependencies[0] = node.returnType.attr(VALUE.name());
		
		for (int i = 0; i < node.parameters.count; i++) {
			dependencies[i + 1] = node.parameters.params.get(i).attr(TYPE.name());
		}
		
		R.rule(node.attr(TYPE.name()))
			.using(dependencies)
			.by(r -> {
				final Type[] paramTypes = new Type[node.parameters.count];
				
				for (int i = 0; i < paramTypes.length; ++i) {
					paramTypes[i] = r.get(i + 1);
				}
				
				r.set(0, new FunctionType(r.get(0), paramTypes));
			});
		
		R.rule()
			.using(node.body.attr(RETURNS.name()), node.returnType.attr(VALUE.name()))
			.by(r -> {
				final boolean returns = r.get(0);
				final Type returnType = r.get(1);
				
				if (!returns && !(returnType instanceof VoidType)) {
					r.error("Missing return statement in func definition", node);
				}
			});
		
		R.rule()
			.using(node.body.attr(EXITS.name()))
			.by(r -> {
				final boolean exits = r.get(0);
				
				if (exits) {
					r.error("Exit statement in func definition", node);
				}
			});
	}
	
	private void entryPoint(EntryPoint node) {
		scope = new Scope(node, scope);
		R.set(node.attr(SCOPE.name()), scope);
	}
	
	private void root(RootNode node) {
		scope = new RootScope(node, R);
		R.set(node.attr(SCOPE.name()), scope);
		
		for (FunctionDefinition functionDefinition : node.preMainDefinitions) {
			if (functionDefinition instanceof ProcDefinition) {
				procDefinition((ProcDefinition) functionDefinition);
			}
			else if (functionDefinition instanceof FuncDefinition) {
				funcDefinition((FuncDefinition) functionDefinition);
			}
		}
		
		for (FunctionDefinition functionDefinition : node.postMainDefinitions) {
			if (functionDefinition instanceof ProcDefinition) {
				procDefinition((ProcDefinition) functionDefinition);
			}
			else if (functionDefinition instanceof FuncDefinition) {
				funcDefinition((FuncDefinition) functionDefinition);
			}
		}
	}
	//endregion
	
	//region UTILS
	private void popScope(RimeNode node) {
		if (scope != null) {
			scope = scope.parent;
		}
	}
	
	private boolean isArithmeticOperator(BinaryOperator op) {
		return op == ADD || op == MULTIPLY || op == SUBTRACT || op == DIVIDE || op == REMAINDER;
	}
	
	private boolean isComparisonOperator(BinaryOperator op) {
		return op == GREATER_THAN || op == GREATER_THAN_EQUAL || op == LESS_THAN || op == LESS_THAN_EQUAL;
	}
	
	private boolean isLogicOperator(BinaryOperator op) {
		return op == LOGICAL_OR || op == LOGICAL_AND;
	}
	
	private boolean isEqualityOperator(BinaryOperator op) {
		return op == EQUAL_TO || op == NOT_EQUAL_TO;
	}
	
	private static boolean isComparableTo(Type a, Type b) {
		if (a instanceof VoidType || b instanceof VoidType) {
			return false;
		}
		
		return a.equals(b) || (a.isReference() && b.isReference());
	}
	
	private static boolean areCompatible(Type a, Type b) {
		if (a instanceof VoidType || b instanceof VoidType) {
			return false;
		}
		else if (a.equals(b)) {
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
		
		return a instanceof NullType && b.isReference();
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
	
	private boolean isExitContainer(RimeNode node) {
		return node instanceof Block
			|| node instanceof IfStatement
			|| node instanceof ElseStatement
			|| node instanceof ExitStatement;
	}
	
	private boolean isReturnContainer(RimeNode node) {
		return node instanceof Block ||
			node instanceof IfStatement ||
			node instanceof ElseStatement ||
			node instanceof ReturnStatement;
	}
	
	private Attribute[] getExitsDependencies(List<? extends RimeNode> children) {
		return children.stream()
			.filter(Objects::nonNull)
			.filter(this::isExitContainer)
			.map(child -> child.attr(EXITS.name()))
			.toArray(Attribute[]::new);
	}
	
	private Attribute[] getReturnsDependencies(List<? extends RimeNode> children) {
		return children.stream()
			.filter(Objects::nonNull)
			.filter(this::isReturnContainer)
			.map(child -> child.attr(RETURNS.name()))
			.toArray(Attribute[]::new);
	}
	
	private Type getPrimitiveSemanticTypeFromASTType(PrimitiveType type) {
		return switch (type.type) {
			case ANY -> new SetType(AnyPrimitiveType.INSTANCE);
			case BOOL -> new SetType(BoolType.INSTANCE);
			case INT -> new SetType(IntType.INSTANCE);
			case STRING -> new SetType(StringType.INSTANCE);
			case TYPE -> new SetType(TypeType.INSTANCE);
			case VOID -> new SetType(VoidType.INSTANCE);
		};
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
	
	private void inferCollectionType(RimeNode node) {
		final RimeNode context = this.inferenceContext;
		
		if (context instanceof VariableDefinition) {
			R.rule(node.attr(TYPE.name()))
				.using(context.attr(TYPE.name()))
				.by(Rule::copyFirst);
		}
		else if (context instanceof FunctionCall) {
			R.rule(node.attr(TYPE.name()))
				.using(((FunctionCall) context).name.attr(TYPE.name()), node.attr(INDEX.name()))
				.by(r -> {
					final FunctionType functionType = r.get(0);
					r.set(0, functionType.paramTypes[(int) r.get(1)]);
				});
		}
	}
	//endregion
}
