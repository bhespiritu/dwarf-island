package server;

import java.io.PrintWriter;
import java.net.InetAddress;

import common.Transform;

public class User {
	
	private static int currentID = 0;
	private int id = getNextID();
	
	public InetAddress IP;
	public int port;
	public String name;
	public PrintWriter tcpStream;
	public long latency;
	
	
	private static int getNextID()
	{
		return currentID++;
	}
	
	public int getID()
	{
		return id;
	}
	
}
