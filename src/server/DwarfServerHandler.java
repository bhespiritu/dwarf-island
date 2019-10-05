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
		byte[] returnData = {first};
		User user = userData.get(msg.sender());
		ByteBuffer datagram;
		switch(first)
		{
			case PacketID.ECHO:
 				returnPacket = new DatagramPacket(Unpooled.copiedBuffer(returnData),msg.sender());
				ctx.write(returnPacket);
				break;
			case PacketID.MESSAGE:
				String message = new String(packet.readAllBytes());
				
				
				
				ByteBuffer data = ByteBuffer.allocate(message.getBytes().length + 1 + Integer.BYTES);
				data.put(PacketID.MESSAGE);
				data.putInt(user.hash);
				data.put(message.getBytes());
				data.rewind();
				sendToAll(ctx, data);
				break;
			case PacketID.MOVE:
				float changeX = packet.readFloat();
				float changeY = packet.readFloat();
				
				datagram = ByteBuffer.allocate(3*4 + 1);
				datagram.put(PacketID.MOVE);
				datagram.putInt(user.hash);
				datagram.putFloat(changeX);
				datagram.putFloat(changeY);
				datagram.rewind();
				
				sendToAllExcept(ctx, datagram, msg.sender());
				
				break;
			case PacketID.CONNECT:
				String name = new String(packet.readAllBytes());
				System.out.println(name);
				int hash = HashGenerator.generate();
				int newX = (int) (Math.random()*10 - 5);
				int newY = (int) (Math.random()*10 - 5);
				datagram = ByteBuffer.allocate(1 + 3*Integer.BYTES + name.getBytes().length);
				datagram.put(PacketID.CONNECT);
				datagram.putInt(hash);
				datagram.putInt(newX);
				datagram.putInt(newY);
				datagram.put(name.getBytes());
				datagram.rewind();
				returnPacket = new DatagramPacket(Unpooled.copiedBuffer(datagram),msg.sender());
				ctx.write(returnPacket);
				
				User newUser = new User(msg.sender());
				newUser.hash = hash;
				userData.put(msg.sender(), newUser);
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
	
	public void sendToAllExcept(ChannelHandlerContext ctx, ByteBuffer message, InetSocketAddress other)
	{
		DatagramPacket packet;
		for(User u : userData.values())
		{
			if(u.outSocket.equals(other)) continue;
			packet = new DatagramPacket(Unpooled.copiedBuffer(message),u.outSocket);
			ctx.write(packet);
		}
	}
	
}
