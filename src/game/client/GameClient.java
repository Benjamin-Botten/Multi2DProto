package game.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import engine.world.entity.*;
import game.server.GameServer;

/**
 * The game client for game-updates that don't require reliable connection
 * ClientTCP.java is intended for connection items such as gamechat, etc.
 * @author robot
 *
 */
public class GameClient extends Thread {
	private DatagramSocket socket;
	private DatagramPacket packet;
	
	public GameClient(Player player) {
		super("Game Client");
		
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		byte[] buf = new byte[256];
		try {
			InetAddress ip = InetAddress.getByName("bejobo.servegame.com");
			
			buf = "Hello there. - Client 2016".getBytes();
			packet = new DatagramPacket(buf, buf.length, ip, GameServer.PORT);
			socket.send(packet);
			
			buf = new byte[256];
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			System.out.println("Game Client: Received from server > " + packet.getData().toString()); //+ new String(packet.getData()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new GameClient(null).start();
	}
}
