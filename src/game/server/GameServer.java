package game.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class GameServer extends Thread {
	
	public static final int PORT = 27200;
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	private BufferedReader reader;
	
	public GameServer() {
		super("Game Server");
		
		try {
			socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		byte[] buf = new byte[256];
		
		packet = new DatagramPacket(buf, buf.length);
		try {
			socket.receive(packet);
			
			//Sender details
			InetAddress ip = packet.getAddress();
			int port = packet.getPort();
			
			//Output message from client
			System.out.println("Game Server: Received data from client > " + new String(packet.getData()));
			
			//Construct outgoing message
			buf = new String("Got a message from you, sending this one back").getBytes();
			
			//Send new packet to sender client
			packet = new DatagramPacket(buf, buf.length, ip, port);
			socket.send(packet);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		GameServer gameServer = new GameServer();
		gameServer.start();
	}
}
