package client;

import common.HashGenerator;
import common.PacketID;
import common.objects.DwarfObject;
import common.objects.WorldObject;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class DwarfClientHandler extends SimpleChannelInboundHandler<DatagramPacket>{

	DwarfClient clientContext;
	
	public DwarfClientHandler(DwarfClient context) {
		clientContext = context;
	}
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		System.out.println("CLIENT: " + msg);
		ByteBufInputStream packet = new ByteBufInputStream(msg.content());
		byte first = packet.readByte();
		byte[] hash;
		int newX, newY;
		String id;
		WorldObject obj;
		switch(first)
		{
			case PacketID.ECHO:
				System.out.println("Server Pinged");
				break;
			case PacketID.MESSAGE:
				hash = new byte[4];
				packet.read(hash);
				id = HashGenerator.byteToHex(hash);
				obj = clientContext.getObject(id);
				byte[] message = packet.readAllBytes();
				clientContext.displayMessage(obj, new String(message));
				break;
			case PacketID.MOVE:
				hash = new byte[4];
				packet.read(hash);
				id = HashGenerator.byteToHex(hash);
				newX = packet.readInt();
				newY = packet.readInt();
				obj = clientContext.getObject(id);
				obj.posX = newX;
				obj.posY = newY;
				break;
			case PacketID.CONNECT:
				hash = new byte[4];
				packet.read(hash);
				id = HashGenerator.byteToHex(hash);
				newX = packet.readInt();
				newY = packet.readInt();
				DwarfObject newDwarf = new DwarfObject();
				newDwarf.id = id;
				newDwarf.posX = newX;
				newDwarf.posY = newY;
				clientContext.setClientObject(newDwarf);
				clientContext.spawnNetworkObject(newDwarf, id);
				break;
		}
		packet.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
}
