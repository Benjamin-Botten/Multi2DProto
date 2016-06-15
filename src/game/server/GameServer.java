package game.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;

public class GameServer extends Thread {

	public static final int PORT = 27205;
	public static final int PORT_GROUP = 27206;

	private DatagramSocket socket;
	private DatagramPacket packet;
	private boolean running = false;
	private List<PlayerOnline> players = new ArrayList<>();

	public GameServer() {
		super("Game Server");

		try {
			socket = new DatagramSocket(PORT);
			running = true;
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(running) {
			try {
				byte[] buf = new byte[256];
				
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				// Sender details 
				InetAddress ip = packet.getAddress();
				int port = packet.getPort();
				
				System.out.println("Got message from client (" + ip.toString() + ": " + port + ")");
				
				new ServerRequest(this, packet).start();

				try {
					Thread.sleep(4);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

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
	 * Check if any clients disconnected impromptu
	 * TODO: Remove
	 */
	public void checkLogons() {
		
	}
	
	/**
	 * TODO: Implement a safer disconnection, that is, make sure that disconnections are "handshaked" (i.o.w. all endpoints have acknowledged the event)
	 * @param name
	 */
	public void disconnectPlayerByName(String name) {
		for(int i = 0; i < players.size(); ++i) {
			PlayerOnline curPlayer = players.get(i);
			if(curPlayer.username.equalsIgnoreCase(name)) {
				players.remove(curPlayer);
			}
		}
	}
	
	public List<PlayerOnline> getPlayers() {
		return players;
	}
	
	public DatagramSocket getSocket() {
		return socket;
	}
	
	public PlayerOnline getPlayerByName(String name) {
		for(int i = 0; i < players.size(); ++i) {
			if(players.get(i).getUsername().equalsIgnoreCase(name)) {
				return players.get(i);
			}
		}
		return null; //Player with that username not exist
	}
	
	public static void main(String[] args) {
		GameServer gameServer = new GameServer();
		gameServer.start();
	}

	public void addPlayer(PlayerOnline playerOnline) {
		players.add(playerOnline);
	}
}
