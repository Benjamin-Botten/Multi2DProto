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

public class GameServer extends Thread {

	public static final int PORT = 27205;
	public static final int PORT_GROUP = 27206;

	private DatagramSocket socket;
	private DatagramSocket socketBroadcast;
	private DatagramPacket packet;
	private InetAddress group;
	private boolean running = false;
	private List<Player> players = new ArrayList<>();
	
	public GameServer() {
		super("Game Server");

		try {
			socketBroadcast = new DatagramSocket(PORT_GROUP);
			socket = new DatagramSocket(PORT);
			group = InetAddress.getByName("bejobo.servegame.com");
			running = true;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(running) {
			try {
				byte[] buf = new byte[256];
				
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				new ServerRequest(packet).start();
				// Sender details
				InetAddress ip = packet.getAddress();
				int port = packet.getPort();

				// Output message from client
				String msgClient = new String(packet.getData());
				System.out.println("Game Server: Received data from client > " + msgClient);
				
				//Construct reply
				buf = parseMessage(msgClient);
				
				// Send new packet to sender client
				packet = new DatagramPacket(buf, buf.length, ip, port);
				socket.send(packet);
				
				
				//BROADCASTING
				try {
					buf = new byte[256];
					packet = new DatagramPacket(buf, buf.length);
					socketBroadcast.receive(packet);
					System.out.println("Recv on broadcast line: " + new String(packet.getData()));
					
					buf = "BROADCAST MSG".getBytes();
					packet = new DatagramPacket(buf, buf.length, group, PORT_GROUP);
					socketBroadcast.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static final String M2DP_REPLY_JOIN_ACCEPT = "0001";
	public static final String M2DP_REPLY_JOIN_DENY = "0002";
	public static final String M2DP_REPLY_UPDATE_POSITION_ACCEPT = "0003";
	public static final String M2DP_REPLY_UPDATE_POSITION_DENY = "0004";
	
	public static final String M2DP_DATA_JOIN = "0001";
	public static final String M2DP_DATA_UPDATE_POSITION = "0002";
	public static final String M2DP_DATA_DISCONNECT = "0003";
	public static final String M2DP_DATA_REQUEST_POSITIONS = "0004";
	
	public static final int M2DP_JOIN = 1;
	public static final int M2DP_UPDATE_POSITION = 2;
	public static final int M2DP_DISCONNECT = 3;
	public static final int M2DP_REQUEST_POSITIONS = 4;
	
	public static final int M2DP_DATA_ID_POS_START = 0;
	public static final int M2DP_DATA_ID_POS_END = 4; //4 bytes
	public static final int M2DP_DATA_MSG_LENGTH_POS_START = 4;
	public static final int M2DP_DATA_MSG_LENGTH_POS_END = 6;
	
	/**
	 * 
	 * @param msg
	 * @return byte[] buffer of the data to send back to client
	 */
	private byte[] parseMessage(String msg) {
		int dataId = Integer.parseInt(msg.substring(M2DP_DATA_ID_POS_START, M2DP_DATA_ID_POS_END));
		int dataLength = Integer.parseInt(msg.substring(M2DP_DATA_MSG_LENGTH_POS_START, M2DP_DATA_MSG_LENGTH_POS_END));
		int startIndex = M2DP_DATA_MSG_LENGTH_POS_END;
		int endIndex = startIndex + dataLength;
		String data = msg.substring(startIndex, endIndex);
		
		System.out.println("Parsing packet from client, data id: " + dataId);
		System.out.println("data length: " + dataLength);
		System.out.println("data: " + data);
		
		byte[] result = "Invalid data".getBytes();
		switch(dataId) {
		case M2DP_JOIN:
			System.out.println("Player \"" + data + "\" sent join packet");
			result = M2DP_REPLY_JOIN_ACCEPT.getBytes();
			for(int i = 0; i < players.size(); ++i) {
				if(players.get(i).name.equalsIgnoreCase(data)) {
					System.out.println("Player \"" + data + "\" attempted to join while already connected");
					return M2DP_REPLY_JOIN_DENY.getBytes();
				}
			}
			players.add(new Player(null, 0, data));
			break;
			
		case M2DP_UPDATE_POSITION:
			result = M2DP_REPLY_UPDATE_POSITION_ACCEPT.getBytes();
			int tokens = 0;
			int lastTokenIndex = 0;
			byte[] bytes = new byte[data.length()];
			String name = "";
			int x = 0, y = 0;
			for(int i = 0; i < data.length(); ++i) {
				if(tokens == 0) {
					if(Character.compare(data.charAt(i), ',') == 0) {
						name = data.substring(0, i);
						lastTokenIndex = i + 1;
						tokens++;
					}
				}
				else if(tokens == 1) {
					if(Character.compare(data.charAt(i), ',') == 0) {
						x = Integer.parseInt(data.substring(lastTokenIndex, i));
						lastTokenIndex = i + 1;
						tokens++;
					}
				}
				else if(tokens == 2) {
					y = Integer.parseInt(data.substring(lastTokenIndex, dataLength));
					tokens++;
					break;
				}
			}
			System.out.println("Player " + name + " updated position to (" + x + ", " + y + ")");
			
			broadcastPlayerPositions(msg);
			break;
		}
		
		return result;
	}
	
	public void broadcastPlayerPositions(String msg) {
		for(int i = 0; i < players.size(); ++i) {
			try {
				byte[] buf = msg.getBytes();
				packet = new DatagramPacket(buf, buf.length, group, PORT_GROUP);
				
				socketBroadcast.send(packet);
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
	
	public static void main(String[] args) {
		GameServer gameServer = new GameServer();
		gameServer.start();
	}
}
