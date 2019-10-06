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
		
		ByteBufInputStream packet = new ByteBufInputStream(msg.content());
		byte first = packet.readByte();
		float newX, newY;
		int id;
		WorldObject obj;
		DwarfObject newDwarf;
		switch(first)
		{
			case PacketID.ECHO:
				System.out.println("Server Pinged");
				break;
			case PacketID.MESSAGE:
				id = packet.readInt();
				obj = clientContext.getObject(id);
				byte[] message = packet.readAllBytes();
				clientContext.displayMessage(obj, new String(message));
				break;
			case PacketID.MOVE:
				id = packet.readInt();
				newX = packet.readFloat();
				newY = packet.readFloat();
				obj = clientContext.getObject(id);
				obj.posX = newX;
				obj.posY = newY;
				break;
			case PacketID.CONNECT:
				id = packet.readInt();
				newX = packet.readInt();
				newY = packet.readInt();
				String name = new String(packet.readAllBytes());
				newDwarf = new DwarfObject();
				newDwarf.id = id;
				newDwarf.posX = newX;
				newDwarf.posY = newY;
				newDwarf.name = name;
				clientContext.setClientObject(newDwarf);
				clientContext.spawnNetworkObject(newDwarf, id);
				break;
			case PacketID.SPAWN:
				id = packet.readInt();
				int type = packet.readInt();
				newX = packet.readInt();
				newY = packet.readInt();
				if(type == 1)
				{
					newDwarf = new DwarfObject();
					newDwarf.id = id;
					newDwarf.posX = newX;
					newDwarf.posY = newY;
					newDwarf.deserializeData(packet.readAllBytes());
					clientContext.spawnNetworkObject(newDwarf, id);
				}
				break;
			case PacketID.DELETE:
				id = packet.readInt();
				clientContext.destroyObject(id);
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
