package game.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import engine.world.World;
import engine.world.entity.*;
import game.Game;
import game.network.M2DPHandler;
import game.network.M2DProtocol;
import game.network.packet.M2DPacket;
import game.network.packet.M2DPacketDisconnect;
import game.network.packet.M2DPacketDisconnectReply;
import game.network.packet.M2DPacketJoin;
import game.network.packet.M2DPacketUpdatePlayer;
import game.server.GameServer;

/**
 * The game client for game-updates that don't require reliable connection
 * ClientTCP.java is intended for connection items such as gamechat, etc.
 * 
 * @author robot
 *
 */
public class GameClient implements M2DPHandler {
	private DatagramSocket socket;
	private DatagramPacket packet;
	private DatagramPacket packetBroadcast;
	private PlayerOnline player;
	private World world;

	private InetAddress ip; // ip_dst
	private String hostname;
	
	//TCP Client
	ClientTCP tcpClient;

	public GameClient(String hostname, PlayerOnline player, World world) {
		if(player == null) {
			throw new IllegalArgumentException("Attempted to create game client with a null player");
		}
		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName("localhost");
			tcpClient = new ClientTCP(this);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		this.player = player;
		this.world = world;
	}

	public boolean sendJoin() {
		if (player != null && !player.isConnected()) {
			
			//Waiting for the player/gameclient to connect/join
			tcpClient.sendJoin();
			tcpClient.listen();
			System.out.println("Successfully established connection to game server");
			
//			try {
//				M2DPacket.join.send(socket, InetAddress.getByName("localhost"), GameServer.PORT, player);
//				while(!player.isConnected()) {
//					M2DPacket.join.recv(socket);
//					
//					if (M2DPacket.join.getReply().contains(M2DProtocol.M2DP_REPLY_JOIN_ACCEPT)) {
//						System.out.println("Login attempt: " + M2DPacket.join.getReply());
//						DatagramPacket packet = M2DPacket.join.getPacket();
//						player.setAddress(packet.getAddress());
//						player.setPort(packet.getPort());
//						player.setConnected(true);
//					} else {
//						System.out.println("Login attempt: " + M2DPacket.join.getReply());
//					}
//				}
//			} catch (UnknownHostException e) {
//				e.printStackTrace();
//			}
		}
		return false;
	}

	public void disconnect() {
		tcpClient.sendDisconnect();
		tcpClient.listen();
	}
	
	// TODO: Implement disconnect
	private void sendDisconnect() {
		tcpClient.sendDisconnect();
		while(player.isConnected()) {
		}
	}
	
	public void sendUpdatePlayer() {
		if (player != null) {
			M2DPacket.updatePlayer.send(socket, ip, GameServer.PORT, player);
		}
	}
	
	@Override
	public void handle(String msg, InetAddress ip, int port) {
		M2DProtocol m2dp = M2DProtocol.parseMessage(msg, ip, port);
		M2DPacket packet = M2DPacket.packets[m2dp.getDataId()];
		packet.parse(m2dp);
		
		if(packet instanceof M2DPacketUpdatePlayer) {
			M2DPacketUpdatePlayer updatePlayer = (M2DPacketUpdatePlayer) packet;
			String username = updatePlayer.getParcel().username;
			if(username.equalsIgnoreCase(player.getUsername())) {
				return;
			}
			int x = updatePlayer.getParcel().x;
			int y = updatePlayer.getParcel().y;
			int rowIndex = updatePlayer.getParcel().rowIndex;
			int columnIndex = updatePlayer.getParcel().columnIndex;
			int directionMovement = updatePlayer.getParcel().directionMovement;
			int currentFrame = updatePlayer.getParcel().currentFrame;
			System.out.println("Players online on clientside: " + world.getPlayers().size());
			PlayerOnline player = world.getPlayerByName(username);
			if(player != null) {
				player.x = x;
				player.y = y;
				player.getSprite().setRowIndex(rowIndex);
				player.getSprite().setColumnIndex(columnIndex);
				player.getSprite().setDirectionMovement(directionMovement);
				player.getSprite().setCurrentFrame(currentFrame);
			} else {
				PlayerOnline newPlayer = new PlayerOnline(username, ip, port);
				newPlayer.x = x;
				newPlayer.y = y;
				newPlayer.getSprite().setRowIndex(rowIndex);
				newPlayer.getSprite().setColumnIndex(columnIndex);
				newPlayer.getSprite().setDirectionMovement(directionMovement);
				newPlayer.getSprite().setCurrentFrame(currentFrame);
				world.addEntity(newPlayer);
			}
		}
	}
	
	public synchronized void listen() {
		if (!player.isConnected())
			throw new RuntimeException("Player attempting to listen while not connected");
		
		tcpClient.start();
		
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						byte[] buf = new byte[256];
						packetBroadcast = new DatagramPacket(buf, buf.length);
						socket.receive(packetBroadcast);
 
						String msg = new String(packetBroadcast.getData());

						handle(msg, packetBroadcast.getAddress(), packetBroadcast.getPort());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void updatePlayer(Player srcPlayer) {
		player.x = srcPlayer.x;
		player.y = srcPlayer.y;
		player.setSprite(srcPlayer.getSprite());
	}

	public PlayerOnline getPlayer() {
		return player;
	}

	public void removePlayerByName(String name) {
		synchronized(world) {
			world.removePlayerByName(name);
		}
	}
}