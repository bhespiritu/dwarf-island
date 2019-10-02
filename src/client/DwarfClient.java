package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import common.DwarfWorld;
import common.GenericObject;
import common.PacketRegistry;
import common.PlayerObject;
import server.DwarfServer;


public class DwarfClient extends JPanel{

	DwarfWorld world;
	
	private int drawDistance = 5;//size of one side of a box centered on player. not radius
	
	public static int debugID = 0;
	private int dID;
	
	public String debugName = "testNAME";
	
	private HashMap<String, PlayerObject> players = new HashMap<String, PlayerObject>();
	
	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame("Digg");
		DwarfServer ds = new DwarfServer();
		ds.start();
		DwarfClient client = new DwarfClient();
		DwarfClient client2 = new DwarfClient();
		
		client.debugName = "User1";
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(client);
		frame.pack();
		frame.setVisible(true);
		
		client.connect("localhost");
		
		while(true)
		{
			client.time+= 0.1f;
			client.repaint();
			Thread.sleep(1000/24);
		}
	}
	
	ClientPacketHandler handler;
	Thread handlerThread;
	
	public DwarfClient() {
		setBackground(Color.CYAN);
		setPreferredSize(new Dimension(200,200));
		dID = debugID++;
		
	}
	
	
	
	public void connect(String addr)
	{
		if(handler != null)
		{
			handlerThread.interrupt();
		}
		handler = new ClientPacketHandler(addr);
		handler.testName = debugName;
		handlerThread = new Thread(handler);
		handlerThread.setName("ClientPacketHandler" + dID);
		handlerThread.start();
	}
	
	public void sendMessage(String msg)
	{
		byte[] messageData = msg.getBytes();
		byte[] outputData = new byte[messageData.length+1]; 
		outputData[0] = PacketRegistry.MESSAGE;
		System.arraycopy(messageData, 0,outputData, 1, messageData.length);
		handler.sendPacket(new String(outputData,0,outputData.length));
	}
	
	public void renderMessage(String userID, String message)
	{
		PlayerObject p = players.get(userID);
		if(p != null)
		{
			
		}
	}
	
	float time = 0;
	GenericObject testObject = new GenericObject();
	Font defaultFont = new Font("Courier New", Font.PLAIN, 5);
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setFont(defaultFont);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(getWidth()/2, getHeight()/2);
		g2d.scale(2,2);
		int offsetX = (int) (4*Math.sin(time));
		int offsetY  = (int) (4*Math.cos(time));
		
		//TODO debug visuals. don't keep
		for(int x = -drawDistance; x < drawDistance; x++)
		{
			for(int y = -drawDistance; y < drawDistance; y++)
			{
				g2d.drawImage(TextureManager.instance.dirt,(x+offsetX)*16,(y+offsetY)*16 + 10, null);
				g2d.drawImage(TextureManager.instance.grass,(x+offsetX)*16,(y+offsetY)*16, null);
			}
		}
		g2d.drawImage(TextureManager.instance.dwarf, 0, 0, null);
		Color hold = g2d.getColor();
		testObject.transform.X = (float) (Math.sin(time));
		testObject.draw(g2d, offsetX*16, offsetY*16);
		LabelObject testLabel = new LabelObject("According to all known laws of....");
		testLabel.draw(g2d, offsetX*16, offsetY*16);
		g.setColor(hold);
	}
	
}
