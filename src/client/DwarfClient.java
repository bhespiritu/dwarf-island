package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JFrame;
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
	
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("Digscord");
		DwarfClient client = new DwarfClient();
		DwarfObject testPlayer = new DwarfObject();
		client.spawnClientObject(testPlayer);
		
		client.displayMessage(testPlayer, "testMessage");
		client.setClientObject(testPlayer);
		
		DwarfServer server = new DwarfServer();
		
		Thread testThread = new Thread(server);
		testThread.start();
		
		client.connect();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(client);
		frame.addKeyListener(client);
		frame.pack();
		frame.setVisible(true);
		
		for(;;)
		{
			client.repaint();
			client.time += 1/24f;
			Thread.sleep(1000/24);
		}
	}

	public float time = 0;
	
	static final int PORT = Integer.parseInt(System.getProperty("port", "7686"));
	
	private HashMap<String,WorldObject> objects = new HashMap<String,WorldObject>();
	
	private DwarfObject clientPlayer;
	
	public int cameraX = 0, cameraY = 0;
	
	private static Dimension defaultDimension = new Dimension(320,320);
	private static Font defaultFont = new Font("Courier New", Font.PLAIN, 5);
	
	public DwarfClient() {
		setBackground(Color.cyan);
		setPreferredSize(defaultDimension);
		

	}
	
	public void setClientObject(DwarfObject dwarf)
	{
		clientPlayer = dwarf;
		lookAtObject(clientPlayer);
	}
	
	public WorldObject getObject(String hash)
	{
		return objects.get(hash);
	}
	
	public void spawnObject(WorldObject object, String hashID, boolean network)//registers a worldobject with the world
	{
		object.id = hashID;
		object.isNetworked = network;
		objects.put(hashID, object);
	}
	
	
	public void spawnClientObject(WorldObject object)
	{
		spawnObject(object,HashGenerator.generate(),false);
	}
	
	public void spawnNetworkObject(WorldObject object, String hashID)
	{
		spawnObject(object,hashID,true);
	}
	
	public void destroyObject(String hashID)
	{
		objects.remove(hashID);
	}
	
	public void destroyObject(WorldObject object)
	{
		objects.remove(object.id);
	}
	
	public void lookAtObject(WorldObject object)
	{
		cameraX = (int) (object.posX*16);
		cameraY = (int) (object.posY*16);
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
	
	public void connect() throws Exception
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
				,SocketUtils.socketAddress("localhost", PORT));
		
		serverChannel.writeAndFlush(pingPacket).sync();
		
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
		
		if(clientPlayer != null)
		{
			clientPlayer.posX = -cameraX/32f;
			clientPlayer.posY = -cameraY/32f;
		}
		
	}
	
}
