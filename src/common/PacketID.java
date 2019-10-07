package common;

public final class PacketID {
	public static final byte ECHO = 0;
	public static final byte MESSAGE = 1;
	public static final byte MOVE = 2;
	public static final byte CONNECT = 3;
	public static final byte SPAWN = 4;
	public static final byte DELETE = 5;
	
	public static String getPacketName(byte id)
	{
		switch(id)
		{
		case 0: return "ECHO";
		case 1: return "MESSAGE";
		case 2: return "MOVE";
		case 3: return "CONNECT";
		case 4: return "SPAWN";
		case 5: return "DELETE";
		}
		return "INVALID PACKET NAME";
	}
}
