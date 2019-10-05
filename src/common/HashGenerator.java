package common;

import java.util.Random;

public final class HashGenerator {

	private static Random prng = new Random();
	
	public static String generate()
	{
		byte[] hash = new byte[4];
		prng.nextBytes(hash);
		return byteToHex(hash);
	}
	
	public static String byteToHex(byte[] data)
	{
		String out = "";
		for (byte b : data) {
            out += (String.format("%02x", b));
		}
		return out;
	}
	
}
