package client;

import javax.swing.JPanel;

import server.DwarfServer;


public class DwarfClient extends JPanel{

	
	public static void main(String[] args) {
		DwarfServer ds = new DwarfServer();
		ds.start();
		DwarfClient client = new DwarfClient();
		client.connect("localhost");
		
	}
	
	ClientPacketHandler handler;
	Thread handlerThread;
	
	public DwarfClient() {
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
	
}
