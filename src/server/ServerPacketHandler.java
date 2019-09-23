package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

public class ServerPacketHandler implements Runnable{
	
	
	
	@Override
	public void run() {
		System.out.println("Listener Enabled");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new ClientChannel(listener.accept()));
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class ClientChannel implements Runnable
	{
		
		private Socket socket;

		public ClientChannel(Socket sock) {
			socket = sock;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
