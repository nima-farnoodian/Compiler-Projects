package rime.source.interpreter;

import norswap.uranium.Reactor;
import norswap.utils.exceptions.Exceptions;
import norswap.utils.exceptions.NoStackException;
import norswap.utils.visitors.ValuedVisitor;
import rime.source.ast.RimeNode;
import rime.source.ast.declarations.*;
import rime.source.ast.expressions.*;
import rime.source.ast.expressions.literals.BoolLiteral;
import rime.source.ast.expressions.literals.IntLiteral;
import rime.source.ast.expressions.literals.StringLiteral;
import rime.source.ast.statements.*;
import rime.source.parsing.RimeGrammar;
import rime.source.interpreter.exceptions.InterpreterException;
import rime.source.interpreter.exceptions.PassthroughException;
import rime.source.semantic.scope.RootScope;
import rime.source.semantic.scope.Scope;
import rime.source.semantic.types.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static norswap.utils.Util.cast;
import static norswap.utils.Vanilla.*;
import static rime.source.semantic.AttributeName.*;
import static rime.source.ast.constants.BinaryOperator.*;



public final class Interpreter {
	private final ValuedVisitor<RimeNode, Object> visitor = new ValuedVisitor<>();
	private final Reactor reactor;
	private final ArrayList<String> mainArgs = new ArrayList<>();
	private ScopeStorage rootStorage;
	private ScopeStorage storage = null;
	private RootScope rootScope;
	
	public Interpreter(Reactor reactor) {
		this.reactor = reactor;
		
		// EXPRESSIONS
		visitor.register(BoolLiteral.class, this::boolLiteral);
		visitor.register(IntLiteral.class, this::intLiteral);
		visitor.register(StringLiteral.class, this::stringLiteral);
		visitor.register(Identifier.class, this::identifier);
		visitor.register(IndexedCollectionAccess.class, this::indexedCollectionAccess);
		visitor.register(BinaryExpression.class, this::binaryExpression);
		visitor.register(UnaryExpression.class, this::unaryExpression);
		visitor.register(EmptyList.class, this::emptyList);
		visitor.register(EmptySet.class, this::emptySet);
		visitor.register(EmptyDict.class, this::emptyDict);
		visitor.register(ListElements.class, this::listElements);
		visitor.register(SetElements.class, this::setElements);
		visitor.register(DictElement.class, this::dictElement);
		visitor.register(DictElements.class, this::dictElements);
		visitor.register(Assignment.class, this::assignment);
		visitor.register(FunctionCall.class, this::functionCall);
		
		// STATEMENTS
		visitor.register(Block.class, this::block);
		visitor.register(ExpressionStatement.class, this::expressionStatement);
		visitor.register(IfStatement.class, this::ifStatement);
		visitor.register(WhileStatement.class, this::whileStatement);
		visitor.register(ExitStatement.class, this::exitStatement);
		visitor.register(ReturnStatement.class, this::returnStatement);
		
		// DECLARATIONS
		visitor.register(VariableDefinition.class, this::variableDefinition);
		visitor.register(EntryPoint.class, this::entryPoint);
		visitor.register(RootNode.class, this::rootNode);
		
		// FALLBACK
		visitor.registerFallback(node -> null);
	}
	
	public void interpret(RimeNode root, ArrayList<String> mainArgs) {
		this.mainArgs.addAll(mainArgs);
		
		try {
			run(root);
		}
		catch (PassthroughException e) {
			throw Exceptions.runtime(e.getCause());
		}
	}
	
	private Object run(RimeNode node) {
		try {
			return visitor.apply(node);
		}
		catch (InterpreterException | PassthroughException | Exit | Return e) {
			throw e;
		}
		catch (RuntimeException e) {
			throw new InterpreterException("exception while executing " + node, e);
		}
	}
	
	private <T> T get(RimeNode node) {
		return cast(run(node));
	}
	
	private static final class Return extends NoStackException {
		/**
		 * Used to implement the control flow of the return statement.
		 */
		
		public final Object value;
		
		private Return(Object value) {
			this.value = value;
		}
	}
	
	private static final class Exit extends NoStackException {
		/**
		 * Used to implement the control flow of the exit statement.
		 */
		
		private Exit() { }
	}
	
	//region EXPRESSIONS
	private Boolean boolLiteral(BoolLiteral node) {
		return node.value;
	}
	
	private Integer intLiteral(IntLiteral node) {
		return node.value;
	}
	
	private String stringLiteral(StringLiteral node) {
		return node.value;
	}
	
	private Object identifier(Identifier node) {
		final Scope scope = reactor.get(node.attr(SCOPE));
		final Declaration decl = reactor.get(node.attr(DECL));
		
		if (node.value.equals(RimeGrammar.MAIN_ARGS)) {
			return mainArgs;
		}
		
		if (decl instanceof VariableDefinition || decl instanceof Parameter) {
			return (scope == rootScope) ? rootStorage.get(scope, node.value) : storage.get(scope, node.value);
		}
		
		return decl;
	}
	
	private Object indexedCollectionAccess(IndexedCollectionAccess node) {
		final Type collectionType = reactor.get(node.identifier.attr(TYPE));
		
		if (collectionType instanceof ListType) {
			final ArrayList<?> list = getList(node.identifier);
			
			try {
				return list.get(getAndCheckListIndex(node.index));
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new PassthroughException(e);
			}
		}
		else if (collectionType instanceof DictType) {
			final Map<?, ?> dict = getHashMap(node.identifier);
			
			try {
				return dict.get(get(node.index));
			}
			catch (NullPointerException e) {
				throw new PassthroughException(e);
			}
		}
		else {
			throw new IllegalArgumentException("Node should be either a list or a dict");
		}
	}
	
	private Object binaryExpression(BinaryExpression node) {
		final Type leftType = reactor.get(node.left.attr(TYPE));
		final Type rightType = reactor.get(node.right.attr(TYPE));
		
		if (leftType instanceof BoolType && rightType instanceof BoolType) {
			final boolean leftBool = (Boolean) extractUntilIsType(node.left, Boolean.class);
			final boolean rightBool = (Boolean) extractUntilIsType(node.right, Boolean.class);
			
			switch (node.operator) {
				case LOGICAL_AND:
					return leftBool && rightBool;
				case LOGICAL_OR:
					return leftBool || rightBool;
				case EQUAL_TO:
					return leftBool == rightBool;
				case NOT_EQUAL_TO:
					return leftBool != rightBool;
			}
		}
		
		if (node.operator == ADD && (leftType instanceof StringType || rightType instanceof StringType)) {
			return convertToString(extractUntilIsNotRimeNode(node.left)) + extractUntilIsNotRimeNode(get(node.right));
		}
		
		if (leftType instanceof IntType && rightType instanceof IntType) {
			final int leftInt = (Integer) extractUntilIsType(node.left, Integer.class);
			final int rightInt = (Integer) extractUntilIsType(node.right, Integer.class);
			
			return switch (node.operator) {
				case EQUAL_TO -> leftInt == rightInt;
				case NOT_EQUAL_TO -> leftInt != rightInt;
				default -> numericalOperation(node, leftInt, rightInt);
			};
		}
		
		return switch (node.operator) {
			case EQUAL_TO -> leftType.isPrimitive() ? get(node.left).equals(get(node.right)) : get(node.left) == get(node.right);
			case NOT_EQUAL_TO -> leftType.isPrimitive() ? !get(node.left).equals(get(node.right)) : get(node.left) != get(node.right);
			default -> throw new Error("Should not reach here");
		};
	}
	
	private Object unaryExpression(UnaryExpression node) {
		return switch (node.operator) {
			case NEGATIVE -> -(Integer) extractUntilIsType(node.operand, Integer.class);
			case LOGICAL_COMPLEMENT -> !(Boolean) extractUntilIsType(node.operand, Boolean.class);
		};
	}
	
	private ArrayList<Object> emptyList(EmptyList node) {
		return new ArrayList<>();
	}
	
	private HashSet<Object> emptySet(EmptySet node) {
		return new HashSet<>();
	}
	
	private HashMap<Object, Object> emptyDict(EmptyDict node) {
		return new HashMap<>();
	}
	
	private ArrayList<Object> listElements(ListElements node) {
		return new ArrayList<>(Arrays.asList(map(node.elements, new Object[0], visitor)));
	}
	
	private HashSet<Object> setElements(SetElements node) {
		return (HashSet<Object>) Arrays.stream(map(node.elements, new Object[0], visitor)).collect(Collectors.toSet());
	}
	
	private Entry<Object, Object> dictElement(DictElement node) {
		final Object key = extractUntilIsNotRimeNode(node.key);
		final Object value = extractUntilIsNotRimeNode(node.value);
		
		return new AbstractMap.SimpleEntry<>(key, value);
	}
	
	private HashMap<Object, Object> dictElements(DictElements node) {
		final Entry<?, ?>[] entries = (Entry<?, ?>[]) map(node.elements, new Entry<?, ?>[0], visitor);
		final HashMap<Object, Object> map = new HashMap<>();
		
		for (Entry<?, ?> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		
		return map;
	}
	
	private Object assignment(Assignment node) {
		if (node.left instanceof Identifier) {
			final Scope scope = reactor.get(node.left.attr(SCOPE));
			final String name = ((Identifier) node.left).value;
			final Object val = get(node.right);
			
			storage.set(scope, name, val);
			
			return val;
		}
		else if (node.left instanceof IndexedCollectionAccess) {
			final Type collectionType = reactor.get(((IndexedCollectionAccess) node.left).identifier.attr(TYPE));
			final IndexedCollectionAccess access = (IndexedCollectionAccess) node.left;
			final Scope scope = reactor.get(access.identifier.attr(SCOPE));
			final String name = access.identifier.value;
			
			if (collectionType instanceof ListType) {
				final int index = getAndCheckListIndex(access.index);
				
				if (storage.get(scope, name) instanceof ListElements) {
					final ListElements elements = (ListElements) storage.get(scope, name);
					
					try {
						final Expression val = (Expression) extractWhileIsRimeNode(node.right);
						elements.elements.set(index, val);
						return val;
					}
					catch (ArrayIndexOutOfBoundsException e) {
						throw new PassthroughException(e);
					}
				}
				else if (storage.get(scope, name) instanceof ArrayList) {
					final ArrayList<Object> elements = (ArrayList<Object>) storage.get(scope, name);
					
					try {
						final Object val = extractUntilIsNotRimeNode(node.right);
						elements.set(index, val);
						return val;
					}
					catch (ArrayIndexOutOfBoundsException e) {
						throw new PassthroughException(e);
					}
				}
				else {
					throw new Error("Should not reach here");
				}
				
				
			}
			else if (collectionType instanceof DictType) {
				final Expression key = access.index;
				final Expression value = (Expression) extractWhileIsRimeNode(node.right);
				final DictElements elements;
				
				if (storage.get(scope, name) instanceof EmptyDict) {
					storage.set(scope, name, new DictElements(new ArrayList<>() {{
						add(new DictElement(key, value));
					}}));
				}
				else {
					elements = (DictElements) storage.get(scope, name);
					
					final Optional<DictElement> element = elements.elements.stream().filter(x -> x.key == key).reduce((x, y) -> y);
					
					if (element.isEmpty()) {
						elements.elements.add(new DictElement(key, value));
					}
					else {
						final int index = elements.elements.indexOf(element.orElseThrow());
						elements.elements.set(index, new DictElement(key, value));
					}
				}
				
				return value;
			}
			else {
				throw new Error("Should not reach here");
			}
		}
		
		throw new Error("Should not reach here");
	}
	
	private Object functionCall(FunctionCall node) {
		final Declaration decl = get(node.name);
		final Object[] args = new Object[node.arguments.size()];
		
		for (int i = 0; i < args.length; i++) {
			final RimeNode n = node.arguments.get(i);
			
			if (n instanceof Identifier && ((Identifier) n).value.equals(RimeGrammar.MAIN_ARGS)) {
				args[i] = mainArgs;
			}
			else {
				args[i] = extractUntilIsNotRimeNode(node.arguments.get(i));
			}
		}
		
		if (decl == null) {
			throw new PassthroughException(new NullPointerException("Calling a null function"));
		}
		
		if (decl instanceof PredefinedFunction) {
			return builtin(((PredefinedFunction) decl).name, args);
		}
		
		final ScopeStorage oldStorage = storage;
		final Scope scope = reactor.get(decl.attr(SCOPE));
		
		storage = new ScopeStorage(scope, storage);
		
		if (decl instanceof ProcDefinition) {
			final ProcDefinition funDecl = (ProcDefinition) decl;
			coIterate(args, funDecl.parameters.params, (arg, param) -> storage.set(scope, param.identifier.value, arg));
			
			try {
				get(funDecl.body);
			}
			catch (Exit e) {
				return null;
			}
			finally {
				storage = oldStorage;
			}
		}
		else if (decl instanceof FuncDefinition) {
			final FuncDefinition funDecl = (FuncDefinition) decl;
			coIterate(args, funDecl.parameters.params, (arg, param) -> storage.set(scope, param.identifier.value, arg));
			
			try {
				get(funDecl.body);
			}
			catch (Return r) {
				return r.value;
			}
			finally {
				storage = oldStorage;
			}
		}
		
		return null;
	}
	//endregion
	
	//region STATEMENTS
	private Void expressionStatement(ExpressionStatement node) {
		if (node.expression instanceof FunctionCall) {
			functionCall((FunctionCall) node.expression);
		}
		
		return null;
	}
	
	private Void block(Block node) {
		final Scope scope = reactor.get(node.attr(SCOPE));
		storage = new ScopeStorage(scope, storage);
		node.statements.forEach(this::run);
		storage = storage.parent;
		
		return null;
	}
	
	private Void ifStatement(IfStatement node) {
		if ((get(node.condition) instanceof BoolLiteral && ((BoolLiteral) get(node.condition)).value) ||
			(get(node.condition) instanceof Boolean && (Boolean) get(node.condition))) {
			get(node.trueBody);
		}
		else if (node.falseBody != null) {
			get(node.falseBody);
		}
		
		return null;
	}
	
	private Void whileStatement(WhileStatement node) {
		while ((get(node.condition) instanceof BoolLiteral && ((BoolLiteral) get(node.condition)).value) ||
			(get(node.condition) instanceof Boolean && (Boolean) get(node.condition))) {
			get(node.body);
		}
		
		return null;
	}
	
	private Void exitStatement(ExitStatement node) {
		throw new Exit();
	}
	
	private Void returnStatement(ReturnStatement node) {
		throw new Return(node.expression == null ? null : get(node.expression));
	}
	//endregion
	
	//region DECLARATIONS
	private Void variableDefinition(VariableDefinition node) {
		final Scope scope = reactor.get(node.attr(SCOPE));
		storage.set(scope, ((Identifier) node.assignment.left).value, node.assignment.right);
		return null;
	}
	
	private Void entryPoint(EntryPoint node) {
		return null;
	}
	
	private Void rootNode(RootNode node) {
		rootScope = reactor.get(node.attr(SCOPE));
		storage = rootStorage = new ScopeStorage(rootScope, null);
		storage.initRoot(rootScope);
		storage.set(rootScope, RimeGrammar.MAIN_ARGS, mainArgs);
		reactor.set(rootScope.mainArgs.attr(VALUE), mainArgs);
		
		try {
			block(node.entryPoint.definition.body);
		}
		catch (Exit e) {
			return null;
		}
		finally {
			storage = null;
		}
		
		return null;
	}
	//endregion
	
	//region UTILS
	private Object extractWhileIsRimeNode(Object node) {
		if (!(node instanceof RimeNode)) {
			throw new RuntimeException("Node is not a RimeNode");
		}
		
		Object old = node;
		Object current = get((RimeNode) old);
		while (current instanceof RimeNode) {
			old = current;
			current = get((RimeNode) old);
		}
		return old;
	}
	
	private Object extractUntilIsNotRimeNode(Object node) {
		if (!(node instanceof RimeNode)) {
			return node;
		}
		
		Object o = get((RimeNode) node);
		while (o instanceof RimeNode) {
			o = get((RimeNode) o);
		}
		return o;
	}
	
	private Object extractUntilIsType(Object node, Class<?> type) {
		if (!(node instanceof RimeNode)) {
			return node;
		}
		
		Object o = get((RimeNode) node);
		while (!type.isInstance(o)) {
			o = get((RimeNode) o);
		}
		return o;
	}
	
	private Object builtin(String name, Object[] args) {
		return switch (name) {
			case "print" -> _print(args);
			case "parseInt" -> _parseInt(args);
			case "length" -> _length(args);
			case "append" -> _append(args);
			case "add" -> _add(args);
			case "contains" -> _contains(args);
			default -> throw new Error("Should not reach here");
		};
	}
	
	private HashMap<?, ?> getHashMap(Expression node) {
		final Object object = get(node);
		
		if (object == null) {
			throw new PassthroughException(new NullPointerException("indexing null dict"));
		}
		
		if (object instanceof HashMap) {
			return (HashMap<?, ?>) object;
		}
		else {
			return (HashMap<?, ?>) extractUntilIsType(object, HashMap.class);
		}
	}
	
	private ArrayList<?> getList(Expression node) {
		if (node instanceof Identifier && ((Identifier) node).value.equals(RimeGrammar.MAIN_ARGS)) {
			return mainArgs;
		}
		
		final Object object = get(node);
		
		if (object == null) {
			throw new PassthroughException(new NullPointerException("indexing null list"));
		}
		
		if (object instanceof ArrayList) {
			return (ArrayList<?>) object;
		}
		else {
			return (ArrayList<?>) extractUntilIsType(object, ArrayList.class);
		}
	}
	
	private int getAndCheckListIndex(Expression node) {
		final int index = (Integer) extractUntilIsType(node, Integer.class);
		
		if (index < 0) {
			throw new ArrayIndexOutOfBoundsException("Negative index: " + index);
		}
		
		if (index >= Integer.MAX_VALUE - 1) {
			throw new ArrayIndexOutOfBoundsException("Index exceeds max array index (2ˆ31 - 1): " + index);
		}
		
		return index;
	}
	
	public static String convertToString(Object arg) {
		if (arg == null) {
			return "null";
		}
		else if (arg instanceof Object[]) {
			return Arrays.deepToString((Object[]) arg);
		}
		else if (arg instanceof ProcDefinition) {
			return ((ProcDefinition) arg).name.value;
		}
		else if (arg instanceof FuncDefinition) {
			return ((FuncDefinition) arg).name.value;
		}
		else if (arg instanceof BoolLiteral) {
			return ((BoolLiteral) arg).value.toString();
		}
		else if (arg instanceof IntLiteral) {
			return ((IntLiteral) arg).value.toString();
		}
		else if (arg instanceof StringLiteral) {
			return ((StringLiteral) arg).value;
		}
		else {
			return arg.toString();
		}
	}
	
	private static Object numericalOperation(BinaryExpression node, Integer left, Integer right) {
		final int _left = left;
		final int _right = right;
		
		switch (node.operator) {
			case MULTIPLY:
				return _left * _right;
			case DIVIDE:
				if (_right != 0) {
					return _left / _right;
				}
				else {
					throw new ArithmeticException("Division by zero");
				}
			case REMAINDER:
				return _left % _right;
			case ADD:
				return _left + _right;
			case SUBTRACT:
				return _left - _right;
			case GREATER_THAN:
				return _left > _right;
			case LESS_THAN:
				return _left < _right;
			case GREATER_THAN_EQUAL:
				return _left >= _right;
			case LESS_THAN_EQUAL:
				return _left <= _right;
			case EQUAL_TO:
				return _left == _right;
			case NOT_EQUAL_TO:
				return _left != _right;
			default:
				throw new Error("Should not reach here");
		}
	}
	//endregion
	
	//region BUILTIN FUNCTIONS
	public Void _print(Object[] args) {
		final String out = convertToString(args[0]);
		System.out.println(out);
		return null;
	}
	
	public Integer _parseInt(Object[] args) {
		return Integer.parseInt(convertToString(args[0]));
	}
	
	public int _length(Object[] args) {
		if (args[0] instanceof EmptyList) {
			return 0;
		}
		else if (args[0] instanceof ListElements) {
			return listElements((ListElements) args[0]).size();
		}
		else if (args[0] instanceof ArrayList) {
			return ((ArrayList<String>) args[0]).size();
		}
		else {
			throw new IllegalArgumentException("Error, argument should be a list");
		}
	}
	
	public ArrayList<?> _append(Object[] args) {
		final ArrayList<Object> list;
		
		if (args[0] instanceof EmptyList) {
			list = new ArrayList<>();
		}
		else if (args[0] instanceof ListElements) {
			list = listElements((ListElements) args[0]);
		}
		else if (args[0] instanceof ArrayList) {
			list = new ArrayList<>((Collection<?>) args[0]);
		}
		else {
			throw new IllegalArgumentException("Error, first argument should be a list");
		}
		
		list.add(extractUntilIsNotRimeNode(args[1]));
		return list;
	}
	
	public Object _add(Object[] args) {
		final Class<?> type = args[1].getClass();
		final HashSet<Object> set;
		
		if (args[0] instanceof EmptySet) {
			set = new HashSet<>();
		}
		else if (args[0] instanceof SetElements) {
			set = setElements((SetElements) args[0]);
		}
		else if (args[0] instanceof HashSet<?>) {
			set = new HashSet<>((Collection<?>) args[0]);
		}
		else {
			throw new IllegalArgumentException("Error, first argument should be a set");
		}
		
		set.add(extractUntilIsType(args[1], type));
		return set;
	}
	
	public Object _contains(Object[] args) {
		final Class<?> type = args[1].getClass();
		final HashSet<?> set;
		
		if (args[0] instanceof EmptySet) {
			return false;
		}
		else if (args[0] instanceof SetElements) {
			set = setElements((SetElements) args[0]);
			
			if (set.isEmpty()) {
				return false;
			}
		}
		else if (args[0] instanceof HashSet) {
			set = (HashSet<?>) args[0];
			
			if (set.isEmpty()) {
				return false;
			}
		}
		else if (args[0] instanceof FunctionCall) {
			set = (HashSet<?>) extractUntilIsNotRimeNode(args[0]);
		}
		else {
			throw new IllegalArgumentException("Error, first argument should be a set");
		}
		
		return set.contains(extractUntilIsType(args[1], type));
	}
	//endregion
}
