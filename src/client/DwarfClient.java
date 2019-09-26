package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import common.DwarfWorld;
import server.DwarfServer;


public class DwarfClient extends JPanel{

	DwarfWorld world;
	
	private int drawDistance = 5;//size of one size of box. not radius
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Digg");
		DwarfServer ds = new DwarfServer();
		ds.start();
		DwarfClient client = new DwarfClient();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(client);
		frame.pack();
		frame.setVisible(true);
		
		client.connect("localhost");
		
	}
	
	ClientPacketHandler handler;
	Thread handlerThread;
	
	public DwarfClient() {
		
		setPreferredSize(new Dimension(200,200));
	}
	
	public void connect(String addr)
	{
		if(handler != null)
		{
			handlerThread.interrupt();
		}
		handler = new ClientPacketHandler(addr);
		handlerThread = new Thread(handler);
		handlerThread.setName("ClientPacketHandler");
		handlerThread.run();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(getWidth()/2, getHeight()/2);
		g2d.scale(2,2);
		g2d.drawImage(TextureManager.instance.grass,0,0, null);
	}
	
}
