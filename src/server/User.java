package server;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;

public class User {
	
	public int hash;
	public Channel channel;
	public InetSocketAddress address;
	
	public User() {
		
	}
	
}
