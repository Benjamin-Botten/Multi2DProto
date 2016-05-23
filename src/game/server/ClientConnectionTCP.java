package game.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 
 * @author robot
 *
 */

public class ClientConnectionTCP implements Runnable {
	
	private Socket connection;
	private boolean connected;
	private static int connections;
	private int id;
	private PrintWriter writer;
	
	public ClientConnectionTCP(Socket connection) {
		this.connection = connection;
		
		try {
			writer = new PrintWriter(new BufferedOutputStream(connection.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(connection.isConnected()) {
			connected = true;
		}
		
		id = connections++;
	}
	
	public void start() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long startTime = System.currentTimeMillis();
		
		while(connected) {
			//getServerInput();
			if(connection.isClosed() || !connection.isConnected()) {
				connected = false;
				break;
			}
			
			try {
				String line = "";
				while((line = reader.readLine()) != null) {
					System.out.println("From Client " + id + "> " + line);
					
					if(line.equalsIgnoreCase("/quit") || line.equalsIgnoreCase("/exit") || line.equalsIgnoreCase("/stop")) {
						connection.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(System.currentTimeMillis() - startTime > 500) {
			}
		}
	}
	
	public void sendMessage(String msg) {
		writer.println(msg);
		writer.flush();
	}
}
