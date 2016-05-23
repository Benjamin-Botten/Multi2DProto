package game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ServerSocketFactory;

import engine.world.World;
import engine.world.entity.Entity;

/**
 * 
 * @author robot
 *
 */

public class ServerTCP {
	
	
	private ServerSocket serverSocket;
	public static final int PORT = 27200;
	private boolean online;
	private List<ClientConnectionTCP> connections = new ArrayList<>();
	private ServerTickerTCP ticker;
	private List<Entity> entities;
	
	public ServerTCP() {
		try {
			serverSocket = new ServerSocket(PORT);
			ticker = new ServerTickerTCP(this);
			ticker.start();
			online = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void listen() {
		System.out.println("Server Started > Listening on port " + PORT);
		while(online) {
			ClientConnectionTCP clientConnection;
			try {
				clientConnection = new ClientConnectionTCP(serverSocket.accept());
				clientConnection.start();
				System.out.println("Client Connected To Server, ID> " + connections.size() + 1);
				connections.add(clientConnection);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isClosed() {
		return serverSocket.isClosed();
	}
	
	public void sendMessage(String msg) {
		
	}
	
	public List<ClientConnectionTCP> getConnections() {
		return connections;
	}
	
	public static void main(String[] args) {
		ServerTCP server = new ServerTCP();
		server.listen();
	}
}
