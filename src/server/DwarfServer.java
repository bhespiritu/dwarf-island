package server;

public class DwarfServer {
	ServerPacketHandler sph;
	public static void main(String[] args) {
		DwarfServer ds = new DwarfServer();
		ds.start();
	}
	
	public DwarfServer() {
		sph = new ServerPacketHandler();
	}
	
	public void start()
	{
		Thread handlerThread = new Thread(sph);
		handlerThread.setName("ServerPacketHandler");
		handlerThread.start();
	}
}
