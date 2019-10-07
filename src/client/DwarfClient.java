package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import common.HashGenerator;
import common.PacketID;
import common.TextureManager;
import common.objects.DwarfObject;
import common.objects.LabelObject;
import common.objects.WorldObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SocketUtils;
import server.DwarfServer;

public class DwarfClient extends JPanel implements KeyListener{
	
	String ClientInstanceName = Double.toHexString(Math.random());
	
	public static final boolean DEBUG_LAUNCH = true;
	
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("Digscord");
		
		String ipAddress;
		
		if(DEBUG_LAUNCH)
		{
			DwarfServer testServer = new DwarfServer();
			Thread serverThread = new Thread(testServer);
			serverThread.start();
			ipAddress = "localhost";
		} else
			ipAddress=JOptionPane.showInputDialog("Enter IP Address");
		
		DwarfClient client = new DwarfClient(ipAddress);
		DwarfClient client2 = new DwarfClient(ipAddress);
		String clientName = JOptionPane.showInputDialog("Enter the name you desire");
		
		
		client2.connect(clientName+2);
		client.connect(clientName);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(client);
		frame.addKeyListener(client);
		frame.pack();
		frame.setVisible(true);
		
		JFrame frame2 = new JFrame("Digscord");
		
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.setContentPane(client2);
		frame2.pack();
		frame2.setVisible(true);
		
		for(;;)
		{
			client.repaint();
			client.time += 1/24f;
			client2.repaint();
			client2.time += 1/24f;
			Thread.sleep(1000/24);
		}
	}

	public float time = 0;
	
	static final int PORT = Integer.parseInt(System.getProperty("port", "7686"));
	
	private ConcurrentHashMap<Integer,WorldObject> objects = new ConcurrentHashMap<Integer,WorldObject>();
	
	private DwarfObject clientPlayer;
	
	private InetSocketAddress serverAddress;
	
	public int cameraX = 0, cameraY = 0;
	
	private static Dimension defaultDimension = new Dimension(320,320);
	private static Font defaultFont = new Font("Courier New", Font.PLAIN, 5);
	
	public DwarfClient(String addr) {
		serverAddress = SocketUtils.socketAddress(addr, PORT);
		setBackground(Color.cyan);
		setPreferredSize(defaultDimension);
		

	}
	
	public void setClientObject(DwarfObject dwarf)
	{
		clientPlayer = dwarf;
		lookAtObject(clientPlayer);
	}
	
	public WorldObject getObject(int id)
	{
		if(!objects.containsKey(id))System.out.println(Integer.toHexString(id) + " was requested, it doesn't exist in this instance");
		return objects.get(id);
	}
	
	
	public void spawnObject(WorldObject object, int id, boolean network)//registers a worldobject with the world
	{
		object.id = id;
		object.isNetworked = network;
		synchronized(objects)
		{
			objects.put(id, object);
		}
		System.out.println(ClientInstanceName + " OBJECT SPAWNED : " + Integer.toHexString(id) + " at " + object.posX +" " + object.posY);
		
	}
	
	
	public void spawnClientObject(WorldObject object)
	{
		spawnObject(object,HashGenerator.generate(),false);
	}
	
	public void spawnNetworkObject(WorldObject object, Integer hashID)
	{
		spawnObject(object,hashID,true);
	}
	
	public void destroyObject(Integer hashID)
	{
		objects.remove(hashID);
	}
	
	public void destroyObject(WorldObject object)
	{
		objects.remove(object.id);
	}
	
	public void lookAtObject(WorldObject object)
	{
		cameraX = -(int) (object.posX*32);
		cameraY = -(int) (object.posY*32);
	}
	
	public void displayMessage(WorldObject object, String message)
	{
		LabelObject label = new LabelObject(message);
		label.parent = object;
		spawnClientObject(label);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(defaultFont);
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(getWidth()/2 + cameraX, getHeight()/2 + cameraY);
		g2.scale(2, 2);
		
		for(int x = -5; x < 5; x++)
		{
			for(int y = -5; y < 5; y++)
			{
				Image grass = TextureManager.grass;
				Image dirt = TextureManager.dirt;
				g.drawImage(dirt, x*16, y*16 + 10, null);
				g.drawImage(grass, x*16, y*16, null);
				
			}
		}

		for(WorldObject obj : objects.values())
		{
			obj.draw(this, g2);
		}
		
		
	}
	
	DwarfClientHandler handler = new DwarfClientHandler(this);
	Channel serverChannel;
	
	public void connect(String name) throws Exception
	{
		NioEventLoopGroup group = new NioEventLoopGroup();
		
		Bootstrap b = new Bootstrap();
		
		b.group(group)
			.channel(NioDatagramChannel.class)
			.option(ChannelOption.SO_BROADCAST, true)
			.handler(handler);
		
		serverChannel = b.bind(0).sync().channel();
		
		byte[] pingData = new byte[1];
		pingData[0] = PacketID.ECHO;
		
		
		
		DatagramPacket pingPacket = new DatagramPacket(Unpooled.copiedBuffer(pingData)
				,serverAddress);
		
		serverChannel.writeAndFlush(pingPacket).await();
		
		byte[] nameBytes = name.getBytes();
		byte[] connect = new byte[1 + nameBytes.length];
		connect[0] = PacketID.CONNECT;
		System.arraycopy(nameBytes, 0, connect, 1, nameBytes.length);
		
		DatagramPacket connectPacket = new DatagramPacket(Unpooled.copiedBuffer(connect)
				,serverAddress);
		
		serverChannel.writeAndFlush(connectPacket);
	
	}

	@Override
	public void keyPressed(KeyEvent ke) {
	}

	@Override
	public void keyReleased(KeyEvent ke) {
	}

	@Override
	public void keyTyped(KeyEvent ke) {
		if(ke.getKeyChar() == 'w')
		{
			cameraY += 8;
			
		}
		if(ke.getKeyChar() == 'a')
		{
			cameraX += 8;
		}
		if(ke.getKeyChar() == 's')
		{
			cameraY -= 8;
		}
		if(ke.getKeyChar() == 'd')
		{
			cameraX -= 8;
		}
		
		if(ke.getKeyChar() == 'f')
		{
			ByteBuffer buffer = ByteBuffer.allocate("Yarr!".length() + 1);
			buffer.put(PacketID.MESSAGE);
			buffer.put("Yarr!".getBytes());
			buffer.rewind();
			DatagramPacket messagePacket = new DatagramPacket(Unpooled.copiedBuffer(buffer)
					,serverAddress);
			serverChannel.writeAndFlush(messagePacket);
		}
		
		if(clientPlayer != null)
		{
			clientPlayer.posX = -cameraX/32f;
			clientPlayer.posY = -cameraY/32f;
			
			ByteBuffer buffer = ByteBuffer.allocate(1 + Integer.BYTES + 2*Float.BYTES);
			buffer.put(PacketID.MOVE);
			buffer.putFloat(clientPlayer.posX);
			buffer.putFloat(clientPlayer.posY);
			buffer.rewind();
			DatagramPacket messagePacket = new DatagramPacket(Unpooled.copiedBuffer(buffer)
					,serverAddress);
			serverChannel.writeAndFlush(messagePacket);
		}
		
	}
	
}
