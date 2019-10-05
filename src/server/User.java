package server;

import java.net.InetSocketAddress;

public class User {

	public InetSocketAddress outSocket;
	public int hash;
	
	public User(InetSocketAddress addr) {
		outSocket = addr;
	}
	
}
