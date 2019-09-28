package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import common.PacketRegistry;

public class ClientPacketHandler implements Runnable{
	
	String serverAddress;
	Scanner in;
	PrintWriter out;
	
	public String testName = "TestName";
	
	public ClientPacketHandler(String addr) {
		serverAddress = addr;
		testName += (int)(Math.random()*1000);
	}
	
	
	public void run()
	{
		try {
            Socket socket = new Socket(serverAddress, 59001);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println(Thread.getAllStackTraces().keySet());
            while (in.hasNextLine() && !Thread.currentThread().isInterrupted()) {
                String line = in.nextLine();
                byte[] data = line.getBytes();
                byte type = data[0];
                byte[] content = new byte[data.length-1];
                System.arraycopy(data, 1, content, 0, content.length);
                if (type == PacketRegistry.NAME_REQUEST) {
                    out.println(testName);
                    
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
