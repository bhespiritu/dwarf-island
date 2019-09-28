package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import common.DwarfWorld;
import server.DwarfServer;


public class DwarfClient extends JPanel{

	DwarfWorld world;
	
	private int drawDistance = 5;//size of one side of a box centered on player. not radius
	
	public static int debugID = 0;
	private int dID;
	
	public String debugName = "testNAME";
	
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
	
	float time = 0;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
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
	}
	
}
