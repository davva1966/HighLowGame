package au.com.highlowgame.util;

import java.util.Random;

public class PasswordGenerator {

	public static final String DIGITS = "0123456789";
	public static final String LOCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
	public static final String UPCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String PRINTABLE_CHARACTERS = LOCASE_CHARACTERS;

	public static String getPassword(int len) {
		return generate(PRINTABLE_CHARACTERS, len);
	}

	protected static String generate(String chars, int passLength) {
		Random m_generator = new Random();
		char[] availableChars = chars.toCharArray();
		int availableCharsLeft = availableChars.length;
		StringBuffer temp = new StringBuffer(passLength);
		for (int i = 0; i < passLength; i++) {
			int pos = (int) (availableCharsLeft * m_generator.nextDouble());
			temp.append(availableChars[pos]);
			availableChars[pos] = availableChars[availableCharsLeft - 1];
			--availableCharsLeft;
		}
		return String.valueOf(temp);
	}

}