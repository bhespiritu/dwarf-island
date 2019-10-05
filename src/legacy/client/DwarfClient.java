package legacy.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import common.TextureManager;
import legacy.common.DwarfWorld;
import legacy.common.GenericObject;
import legacy.common.PacketRegistry;
import legacy.server.DwarfServer;


public class DwarfClient extends JPanel implements KeyListener{

	DwarfWorld world;
	
	private int drawDistance = 5;//size of one side of a box centered on player. not radius
	
	public static int debugID = 0;
	private int dID;
	
	public String debugName = "testNAME";
	
	private HashMap<String, PlayerObject> players = new HashMap<String, PlayerObject>();
	private HashSet<WorldObject> worldObjects = new HashSet<WorldObject>();
	
	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame("Digg");
		DwarfServer ds = new DwarfServer();
		ds.start();
		DwarfClient client = new DwarfClient();
		
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
			LabelObject label = new LabelObject(message);
			label.parent = p;
			worldObjects.add(label);
		}
	}
	
	float time = 0;
	GenericObject testObject = new GenericObject();
	Font defaultFont = new Font("Courier New", Font.PLAIN, 5);
	LabelObject testLabel = new LabelObject("According to all known laws of....");
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setFont(defaultFont);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(getWidth()/2, getHeight()/2);
		g2d.scale(2,2);
		int offsetX = (int) (4*Math.sin(time))*0 + 2;
		int offsetY  = (int) (4*Math.cos(time))*0 + 2;
		
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
		

		g.setColor(hold);
	}



	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
