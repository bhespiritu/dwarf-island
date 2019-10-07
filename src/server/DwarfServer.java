package server;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashSet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class DwarfServer implements Runnable{

	private static final int PORT = Integer.parseInt(System.getProperty("port", "7686"));
	
	ChannelGroup channels = 
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	HashSet<InetSocketAddress> clients = new HashSet<InetSocketAddress>();
	
	public static void main(String[] args) throws InterruptedException, UnknownHostException {
		DwarfServer server = new DwarfServer();
		server.run();
		
	}
	
	
	
	public DwarfServer() {
		
	}
	
	public void run()
	{
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true)
					.handler(new DwarfServerHandler(this));
			try {
				b.bind(PORT).sync().channel().closeFuture().await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			group.shutdownGracefully();
		}
	}
	
}
