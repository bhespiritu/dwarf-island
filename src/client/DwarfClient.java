package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import common.PacketRegistry;
import server.DwarfServer;


public class DwarfClient {

	public static void main(String[] args) {
		DwarfServer ds = new DwarfServer();
		ds.start();
		DwarfClient client = new DwarfClient("localhost");
		client.run();
		
	}
	
	String serverAddress;
	Scanner in;
	PrintWriter out;
	
	public DwarfClient(String addr) {
		serverAddress = addr;
	}
	
	public void run()
	{
		try {
            Socket socket = new Socket(serverAddress, 59001);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println(Thread.getAllStackTraces().keySet());
            while (in.hasNextLine()) {
                String line = in.nextLine();
                byte[] data = line.getBytes();
                byte type = data[0];
                byte[] content = new byte[data.length-1];
                System.arraycopy(data, 1, content, 0, content.length);
                if (type == PacketRegistry.NAME_REQUEST) {
                    out.println("TestName");
                }else if (type == PacketRegistry.MESSAGE) {
                    System.out.println(new String(content));
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally
		{
			
		}
	}
	
}
