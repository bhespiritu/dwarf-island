package server;

import java.io.PrintWriter;
import java.net.Socket;

public class User {
	
	private static int currentID = 0;
	private int id = getNextID();
	public String name;
	public PrintWriter stream;
	public Socket socket;
	
	
	private static int getNextID()
	{
		return currentID++;
	}
	
	public int getID()
	{
		return id;
	}
	
}
