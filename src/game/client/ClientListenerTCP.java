package game.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 
 * @author robot
 *
 */
public class ClientListenerTCP implements Runnable {
	
	private Socket socket;
	private BufferedReader reader;
	private boolean listening;
	
	public ClientListenerTCP(Socket socket) {
		this.socket = socket;
		
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Thread thread;
	public void start() {
		thread = new Thread(this);
		thread.start();
		listening = true;
	}
	
	@Override
	public void run() {
		try {
			String line = "";
			while(listening) {
				if(thread.isInterrupted()) {
					try {
						thread.sleep(5000);
						break;
					} catch (InterruptedException e) {
						thread.interrupt();
					}
				}
				if(!socket.isClosed()) {
					line = reader.readLine();
					if(line == null) break;
					System.out.println("Received from Server (In ClientListener thread) > " + line);
				} else {
					System.out.println("Socket closed in ClientListener!");
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void stop() {
		thread.interrupt();
	}
}
