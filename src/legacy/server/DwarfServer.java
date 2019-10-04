package legacy.server;

public class DwarfServer {
	ServerPacketHandler sph;
	Thread handlerThread;
	public static void main(String[] args) {
		DwarfServer ds = new DwarfServer();
		ds.start();
	}
	
	public DwarfServer() {
		sph = new ServerPacketHandler();
	}
	
	public void start()
	{
		if(handlerThread != null) handlerThread.interrupt();
		handlerThread = new Thread(sph);
		handlerThread.setName("ServerPacketHandler");
		handlerThread.start();
	}
}
