package rime.source.parsing;

public final class Utils {
	public static boolean isValidIdentifierStart(int codePoint) {
		// returns true if and only if codePoint corresponds to a letter or an underscore
		return Character.isLetter(codePoint) || codePoint == 95;
	}
	
	public static boolean isValidIdentifierPart(int codePoint) {
		// returns true if and only if codePoint corresponds to a letter, an underscore (_) or a digit
		return isValidIdentifierStart(codePoint) || Character.isDigit(codePoint);
	}
}
