package rime.source.grammar;

public final class Utils {
	/**
	 * @param codePoint
	 * @return true if and only if codePoint corresponds to a letter or an underscore (_)
	 */
	public static boolean isValidIdentifierStart(int codePoint) {
		return Character.isLetter(codePoint) || codePoint == 95;
	}
	
	/**
	 * @param codePoint
	 * @return true if and only if codePoint corresponds to a letter, an underscore (_) or a digit
	 */
	public static boolean isValidIdentifierPart(int codePoint) {
		return isValidIdentifierStart(codePoint) || Character.isDigit(codePoint);
	}
}
