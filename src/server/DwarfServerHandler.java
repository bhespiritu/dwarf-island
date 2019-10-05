package server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import common.HashGenerator;
import common.PacketID;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class DwarfServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	HashMap<InetSocketAddress, User> userData = new HashMap<InetSocketAddress, User>();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		
		ByteBufInputStream packet = new ByteBufInputStream(msg.content());
		DatagramPacket returnPacket;
		byte first = packet.readByte();
		System.out.println("SERVER: " + msg + " " + first);
		byte[] returnData = {first};
		switch(first)
		{
			case PacketID.ECHO:
				System.out.println("Client Pinged");
 				returnPacket = new DatagramPacket(Unpooled.copiedBuffer(returnData),msg.sender());
				ctx.write(returnPacket);
				break;
			case PacketID.MESSAGE:
				String message = new String(packet.readAllBytes());
				
				User u = userData.get(msg.sender());
				
				ByteBuffer data = ByteBuffer.allocate(message.getBytes().length + 1 + Integer.BYTES);
				data.put(PacketID.MESSAGE);
				data.putInt(u.hash);
				data.put(message.getBytes());
				data.rewind();
				sendToAll(ctx, data);
				break;
			case PacketID.MOVE:
				break;
			case PacketID.CONNECT:
				String name = new String(packet.readAllBytes());
				System.out.println(name);
				int hash = HashGenerator.generate();
				int newX = (int) (Math.random()*10 - 5);
				int newY = (int) (Math.random()*10 - 5);
				ByteBuffer datagram = ByteBuffer.allocate(4*4);
				datagram.put(PacketID.CONNECT);
				datagram.putInt(hash);
				datagram.putInt(newX);
				datagram.putInt(newY);
				datagram.rewind();
				returnPacket = new DatagramPacket(Unpooled.copiedBuffer(datagram),msg.sender());
				ctx.write(returnPacket);
				
				User user = new User(msg.sender());
				user.hash = hash;
				userData.put(msg.sender(), user);
				break;
		}
		packet.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

	public void sendToAll(ChannelHandlerContext ctx, ByteBuffer message)
	{
		DatagramPacket packet;
		for(User u : userData.values())
		{
			packet = new DatagramPacket(Unpooled.copiedBuffer(message),u.outSocket);
			ctx.write(packet);
		}
	}
	
}
