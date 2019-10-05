package server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class DwarfServer implements Runnable{

	private static final int PORT = Integer.parseInt(System.getProperty("port", "7686"));
	
	public static void main(String[] args) throws InterruptedException {
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
					.handler(new DwarfServerHandler());
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
