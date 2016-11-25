package game.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;

/**
 * 
 * @author robot
 *
 */
public class ClientListenerThreadTCP implements Runnable {
	
	private Socket socket;
	private BufferedReader reader;
	private boolean listening;
	private GameClient gameClient;
	
	public ClientListenerThreadTCP(Socket socket, GameClient gameClient) {
		this.socket = socket;
		this.gameClient = gameClient;
		
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
					
					if(line.contains(M2DProtocol.M2DP_DATA_DISCONNECT)) {
						System.out.println("Client> Received disconnect from a player");
						M2DProtocol m2dp = M2DProtocol.parseTCP(line, socket.getInetAddress(), socket.getPort());
						if(m2dp.getData().equalsIgnoreCase(gameClient.getPlayer().getUsername())) {
							continue;
						} else {
							gameClient.removePlayerByName(m2dp.getData());
						}
					}
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
