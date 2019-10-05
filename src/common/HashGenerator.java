package common;

import java.nio.ByteBuffer;
import java.util.Random;

public final class HashGenerator {

	private static Random prng = new Random();
	
	public static int generate()
	{
		byte[] hash = new byte[4];
		prng.nextBytes(hash);
		return ByteBuffer.wrap(hash).getInt();
	}
	
	public static String byteToHex(int data)
	{
		return Integer.toHexString(data);
	}
	
}
