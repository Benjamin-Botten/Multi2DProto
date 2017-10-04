package game.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;
import game.server.GameServer;
import game.server.ServerTCP;

/**
 * 
 * @author robot
 *
 */

public class ClientTCP {
	
	private Socket connection;
	private int port = ServerTCP.PORT;
	private String hostname = "localhost";
	private BufferedReader reader;
	private PrintWriter writer;
	private ClientListenerThreadTCP threadedListener;
	private ClientListenerTCP listener;
	private GameClient gameClient;
	
	public ClientTCP(GameClient gameClient) {
		this.gameClient = gameClient;
		try {
			connection = new Socket(hostname, port);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
			listener = new ClientListenerTCP(connection, gameClient);
			threadedListener = new ClientListenerThreadTCP(connection, gameClient);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendJoin() {
		if(writer == null) {
			System.out.println("Attempting to send join without a connection (null-writer)");
			return;
		}
		String data = gameClient.getPlayer().getUsername();
		String dataLength = GameServer.formatLength(data.length());
		writer.println(M2DProtocol.M2DP_DATA_JOIN + dataLength + data);
		writer.flush();
		
		System.out.println("Sending join from TCP client");
	}
	
	public void sendDisconnect() {
		String data = gameClient.getPlayer().getUsername();
		String dataLength = GameServer.formatLength(data.length());
		writer.println(M2DProtocol.M2DP_DATA_DISCONNECT + dataLength + data);
		writer.flush();
	}
	
	public void listen() {
		listener.start();
	}
	
	public void start() {
		threadedListener.start();
//		int testTicks = 0;
//		long startTime = System.currentTimeMillis();
//		while(!connection.isClosed()) {
//			if(testTicks > 3) {
//				try {
//					listener.stop(); //Stop the listener before closing the connection
//					Thread.sleep(5000);
//					connection.close();
//				} catch (IOException | InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			
//			try {
//				Thread.sleep(2);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
}
