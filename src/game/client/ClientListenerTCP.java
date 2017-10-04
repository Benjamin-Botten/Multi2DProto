package game.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;

public class ClientListenerTCP {
	
	private Socket socket;
	private BufferedReader reader;
	private boolean listening;
	private GameClient gameClient;
	
	public ClientListenerTCP(Socket socket, GameClient gameClient) {
		this.socket = socket;
		this.gameClient = gameClient;
		
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		listening = true;
		run();
	}
	
	public void run() {
		try {
			String line = "";
			while(listening) {
				System.out.println("Listening for server reply");
				if(!socket.isClosed()) {
					line = reader.readLine();
					if(line == null) break;
					System.out.println("Received from Server (In ClientListener thread) > " + line);
					
					if(line.contains(M2DProtocol.M2DP_REPLY_JOIN_ACCEPT)) {
						int netId = Integer.parseInt(line.substring(5, line.length()));
						System.out.println("Client> Received join accept reply from Server with Net ID '" + netId + "'");
						gameClient.getPlayer().setConnected(true);
						gameClient.getPlayer().setNetId(netId);
						break;
					}
					if(line.contains(M2DProtocol.M2DP_REPLY_DISCONNECT_ACCEPT)) {
						System.out.println("Client> Received disconnect accept reply from Server");
						gameClient.getPlayer().setNetId(0);
						gameClient.getPlayer().setConnected(false);
						break;
					}
				} else {
					System.out.println("Socket closed in ClientListener!");
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
