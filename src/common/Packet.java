package common;

import java.net.DatagramPacket;

import client.*;

public abstract class Packet {
	
	public static byte packetID;
	
	public abstract void clientSide(DwarfClient dc);
	
	public abstract void serverSide(DwarfClient ds);
	
	public abstract DatagramPacket build();
}
