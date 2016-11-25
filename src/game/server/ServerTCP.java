package game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ServerSocketFactory;

import engine.world.World;
import engine.world.entity.Entity;
import game.network.M2DProtocol;

/**
 * 
 * @author robot
 *
 */

public class ServerTCP {

	private ServerSocket serverSocket;
	public static final int PORT = GameServer.PORT;
	private boolean online;
	private List<ClientConnectionTCP> connections = new ArrayList<>();
	private ServerTickerTCP ticker;
	private GameServer gameServer;

	public ServerTCP(GameServer gameServer) {
		try {
			serverSocket = new ServerSocket(PORT);
			ticker = new ServerTickerTCP(this);
			ticker.start();
			online = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.gameServer = gameServer;
	}

	public void listen() {
		new Thread(new Runnable() {
			public void run() {
				System.out.println("Server Started > Listening on port " + PORT);
				while (online) {
					for(int i = 0; i < connections.size(); ++i) {
						if(!connections.get(i).isConnected()) {
							connections.remove(i);
							break;
						}
					}
					ClientConnectionTCP clientConnection;
					try {
						System.out.println("Listening for TCP connection");
						clientConnection = new ClientConnectionTCP(serverSocket.accept(), gameServer);
						clientConnection.start();
						System.out.println("Client Connected To Server, ID> " + connections.size());
						connections.add(clientConnection);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public boolean isClosed() {
		return serverSocket.isClosed();
	}

	public void sendMessage(String msg) {
		
	}
	
	public void broadcastMessage(String msg) {
		synchronized(connections) {
			for(int i = 0; i < connections.size(); ++i) {
				connections.get(i).sendMessage(msg);
			}
		}
	}

	public List<ClientConnectionTCP> getConnections() {
		return connections;
	}
}
