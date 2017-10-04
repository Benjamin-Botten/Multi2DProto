package game.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import engine.world.entity.AttackEntity;
import engine.world.entity.Entity;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;

public class GameServer extends Thread {
	
	public static String hostname = "localhost";
	
	//UDP segment of the server
	public static final int PORT = 27205;
	public static final int PORT_GROUP = 27206;

	private DatagramSocket socket;
	private DatagramPacket packet;
	private boolean running = false;

	public static final int MAX_TICKS = 32;
	
	private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
	private List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());
	private List<AttackEntity> attackEntities = Collections.synchronizedList(new ArrayList<AttackEntity>());
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public List<AttackEntity> getAttackEntities() {
		return attackEntities;
	}

	
	public final NetIdPool netIdPool = new NetIdPool();
	public ServerActionHandler serverActionHandler = new ServerActionHandler(this);
	
	private ServerTicker serverTicker = new ServerTicker(this);
	
	//TCP segment of the server
	private ServerTCP tcpServer;
	
	public GameServer() {
		super("Game Server");

		try {
			socket = new DatagramSocket(null);
			socket.bind(new InetSocketAddress(hostname, PORT));
			running = true;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		tcpServer = new ServerTCP(this);
	}

	public void run() {
		tcpServer.listen();
		new Thread(serverTicker).start();
		while(running) {
			
			try {
				//Construct buffer to receive UDP packet
				byte[] buf = new byte[256];
				
				//Receive packet
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				// Sender details 
				InetAddress ip = packet.getAddress();
				int port = packet.getPort();
				
//				System.out.println("Got message from client (" + ip.toString() + ": " + port + ")");
				
				//Open a new request-thread on the server to handle the packet
				new ServerRequest(this, packet).start();

				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * TODO: MOVE THIS, DOES NOT BELONG WOW HAHA
	 * @param dataLength
	 * @return
	 */
	public static String formatLength(int dataLength) {
		String result = "";
		byte[] buf = (dataLength + "").getBytes();
		int nDigits = 0;
		for(int i = 0; i < buf.length; ++i) {
			if(Character.isDigit((char) buf[i])) {
				nDigits++;
			}
		}
		
		if(nDigits < 2) {
			result = "0" + dataLength;
		} else {
			result = "" + dataLength;
		}
		return result;
	}
	
	/**
	 * TODO: Implement a safer disconnection, that is, make sure that disconnections are "handshaked" (i.o.w. all endpoints have acknowledged the event)
	 * @param name
	 */
	public void disconnectPlayerByName(String name) {
		for(int i = 0; i < players.size(); ++i) {
			if(players.get(i).getUsername().equalsIgnoreCase(name)) {
				tcpServer.broadcastMessage(M2DProtocol.M2DP_DATA_DISCONNECT + formatLength(name.length()) + name);
				players.remove(i);
				break;
			}
		}
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public DatagramSocket getSocket() {
		return socket;
	}
	
	public Player getPlayerByName(String name) {
		if(name == null) throw new IllegalArgumentException("Attempted to get player with string name = null");
		for(int i = 0; i < players.size(); ++i) {
			if(players.get(i).getUsername().equalsIgnoreCase(name)) {
				return players.get(i);
			}
		}
		return null; //Player with that username does not exist
	}

	public void addPlayer(Player player) {
		if(player == null) throw new IllegalArgumentException("Adding a null PlayerOnline object");
		System.out.println("Adding player \"" + player.getUsername() + "\" to server");
		players.add(player);
	}

	public Player getPlayerById(int targetNetId) {
		for(int i = 0; i < players.size(); ++i) {
			Player plr = players.get(i);
			if(plr.getNetId() == targetNetId) {
				return plr;
			}
		}
		return null;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public static void main(String[] args) {
		GameServer gameServer = new GameServer();
		gameServer.start();
	}
}
