package server;

import common.HashGenerator;
import common.PacketID;
import common.objects.DwarfObject;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class DwarfServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		System.out.println("SERVER: " + msg);
		ByteBufInputStream packet = new ByteBufInputStream(msg.content());
		DatagramPacket returnPacket;
		byte first = packet.readByte();
		switch(first)
		{
			case PacketID.ECHO:
				System.out.println("Client Pinged");
				byte[] returnData = {first};
 				returnPacket = new DatagramPacket(Unpooled.copiedBuffer(returnData),msg.sender());
				ctx.write(returnPacket);
				break;
			case PacketID.MESSAGE:
				break;
			case PacketID.MOVE:
				break;
			case PacketID.CONNECT:
				break;
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

}
