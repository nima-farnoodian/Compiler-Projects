package rime.source.interpreter.exceptions;

/**
 * This class represents an exception that is intentionally thrown by
 * the interpreter to implement various control flow statements.
 */
public final class PassthroughException extends RuntimeException {
	public PassthroughException(Throwable cause) {
		super(cause);
	}
}
