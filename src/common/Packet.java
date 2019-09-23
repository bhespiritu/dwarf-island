package common;

import client.*;

public abstract class Packet {
	
	public static byte packetID;
	
	public abstract void clientSide(DwarfClient dc);
	
	public abstract void serverSide(DwarfClient ds);
}
