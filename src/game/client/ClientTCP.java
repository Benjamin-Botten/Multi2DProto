package game.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import game.server.ServerTCP;

/**
 * 
 * @author robot
 *
 */

public class ClientTCP {
	
	private Socket connection;
	private int port = ServerTCP.PORT;
	private String hostname = "bejobo.servegame.com";
	private BufferedReader reader;
	private PrintWriter writer;
	private ClientListenerTCP listener;
	
	public ClientTCP() {
		try {
			connection = new Socket(hostname, port);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
			listener = new ClientListenerTCP(connection);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		listener.start();
		int testTicks = 0;
		long startTime = System.currentTimeMillis();
		while(!connection.isClosed()) {
			if(testTicks > 3) {
				try {
					listener.stop(); //Stop the listener before closing the connection
					Thread.sleep(5000);
					connection.close();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(System.currentTimeMillis() - startTime > 2000) {
				writer.println("Test message from a client");
				writer.flush();
				startTime = System.currentTimeMillis();
				++testTicks;
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		ClientTCP client = new ClientTCP();
		client.start();
	}
}
