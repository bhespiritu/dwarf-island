package server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import common.HashGenerator;
import common.PacketID;
import common.objects.DwarfObject;
import common.objects.WorldObject;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.DatagramPacket;

public class DwarfServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	
	ChannelGroup channels;
	
	User associatedUser;
	
	public DwarfServerHandler(ChannelGroup channels) {
		this.channels = channels;
		associatedUser = new User();
		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		
		ByteBufInputStream packet = new ByteBufInputStream(msg.content());
		DatagramPacket returnPacket;
		byte first = packet.readByte();
		byte[] returnData = {first};
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
				data.putInt(associatedUser.hash);
				data.put(message.getBytes());
				data.rewind();
				sendToAll(ctx, data);
				break;
			case PacketID.MOVE:
				float changeX = packet.readFloat();
				float changeY = packet.readFloat();
				
				datagram = ByteBuffer.allocate(3*4 + 1);
				datagram.put(PacketID.MOVE);
				datagram.putInt(associatedUser.hash);
				datagram.putFloat(changeX);
				datagram.putFloat(changeY);
				datagram.rewind();
				
				sendToAllOther(ctx, datagram);
				
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
				
				associatedUser.hash = hash;
				
				DwarfObject newDwarf = new DwarfObject();
				newDwarf.id = hash;
				newDwarf.posX = newX;
				newDwarf.posY = newY;
				newDwarf.name = name;
				
				spawnPlayerObject(ctx, newDwarf);
				
				break;
		}
		packet.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
		
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    channels.add(ctx.channel());
	    associatedUser.channel = ctx.channel();
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		ByteBuffer buffer = ByteBuffer.allocate(1 + Integer.BYTES);
		buffer.put(PacketID.DELETE);
		buffer.putInt(associatedUser.hash);
		sendToAllOther(ctx, buffer);
		
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

	public void sendToAll(ChannelHandlerContext ctx, ByteBuffer message)
	{
		DatagramPacket packet;
		for(Channel c : channels)
		{
			packet = new DatagramPacket(Unpooled.copiedBuffer(message),(InetSocketAddress) c.remoteAddress());
			c.writeAndFlush(packet);
		}
	}
	
	public void sendToAllOther(ChannelHandlerContext ctx, ByteBuffer message)
	{
		DatagramPacket packet;
		for(Channel c : channels)
		{
			if(ctx.channel().equals(c))continue;
			packet = new DatagramPacket(Unpooled.copiedBuffer(message),(InetSocketAddress) c.remoteAddress());
			c.writeAndFlush(packet);
		}
	}
	
	public void spawnObject(ChannelHandlerContext ctx, WorldObject object)
	{
		byte[] data = object.serializeData();
		ByteBuffer buffer = ByteBuffer.allocate(data.length + Integer.BYTES + 2*Float.BYTES + 1);
		buffer.put(PacketID.SPAWN);
		buffer.putInt(object.id);
		buffer.putFloat(object.posX);
		buffer.putFloat(object.posY);
		buffer.put(data);
		sendToAll(ctx, buffer);
	}
	
	public void spawnPlayerObject(ChannelHandlerContext ctx, WorldObject object)
	{
		byte[] data = object.serializeData();
		ByteBuffer buffer = ByteBuffer.allocate(data.length + Integer.BYTES + 2*Float.BYTES + 1);
		buffer.put(PacketID.SPAWN);
		buffer.putInt(object.id);
		buffer.putFloat(object.posX);
		buffer.putFloat(object.posY);
		buffer.put(data);
		sendToAllOther(ctx, buffer);
	}
	
}
