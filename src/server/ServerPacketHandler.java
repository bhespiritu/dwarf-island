package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.PacketRegistry;

public class ServerPacketHandler implements Runnable {

	private static final int PORT = 59001;
	public static final int BUFFERLENGTH = 256;
	private byte[] udpBuffer = new byte[BUFFERLENGTH];
	
	
	private HashMap<String, User> users = new HashMap<String,User>();
	private DatagramSocket udpSocket;

	@Override
	public void run() {
		System.out.println("Listener Enabled");
		
		ExecutorService pool = Executors.newFixedThreadPool(500);
		try (ServerSocket listener = new ServerSocket(PORT)) {
			udpSocket = new DatagramSocket(PORT);
			while (true) {
				//TCP
				pool.execute(new ClientChannel(listener.accept()));
				//UDP
				Arrays.fill(udpBuffer, (byte)0);
				DatagramPacket inputPacket = new DatagramPacket(udpBuffer,0,BUFFERLENGTH);
				udpSocket.receive(inputPacket);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printToAll(String message) {
		byte[] messageData = message.getBytes();
		byte[] outputData = new byte[messageData.length+1]; 
		outputData[0] = PacketRegistry.MESSAGE;
		System.arraycopy(messageData, 0,outputData, 1, messageData.length);

		for (User user : users.values()) {
			user.tcpStream.println(new String(outputData,0,outputData.length));
		}
	}

	private class ClientChannel implements Runnable {

		private Socket socket;
		private Scanner in;
		private PrintWriter out;
		private String name;

		public ClientChannel(Socket sock) {
			
			socket = sock;
		}

		@Override
		public void run() {
			try {
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);
				while (!Thread.currentThread().isInterrupted()) {
					int timeout = 0;
					while (!Thread.currentThread().isInterrupted() && timeout <= 1000) {
						timeout++;
						byte[] nameRequestPacket = { PacketRegistry.NAME_REQUEST };
						long startTime = System.nanoTime();
						out.println(new String(nameRequestPacket, 0, 1));
						name = in.nextLine();
						
						if (name == null) {
							return;
						}
						synchronized (users) {
							if (!name.isEmpty() && !users.containsKey(name)) {
								User user = new User();
								user.IP = socket.getInetAddress();
								user.port = socket.getPort();
								user.name = name;
								user.tcpStream = out;
								user.latency = System.nanoTime() - startTime;
								users.put(name, user);
								printToAll(name + " has joined");
								System.out.println(users.keySet());
								break;
							}
						}
					}
				}

			} catch (Exception e) {
				System.out.println(e);
			} finally {

				System.out.println(name + " is leaving");
				users.remove(name);
				printToAll(name + "has left the server.");
				try {
					socket.close();
				} catch (IOException e) {
				}
			}

		}

	}
}
