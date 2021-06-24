package test;

import java.util.Base64;

public class Test1 {

	public static void main(String[] args) {
		String enc = Base64.getEncoder().encodeToString("audqiwyccqwbuuwb".getBytes());
		System.out.println(enc);
		String dec = new String(Base64.getDecoder().decode(enc));
		System.out.println(dec);

	}

}
