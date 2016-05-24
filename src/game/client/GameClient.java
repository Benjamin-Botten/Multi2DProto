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
import game.server.GameServer;

/**
 * The game client for game-updates that don't require reliable connection
 * ClientTCP.java is intended for connection items such as gamechat, etc.
 * @author robot
 *
 */
public class GameClient {
	private DatagramSocket socketBroadcast;
	private DatagramSocket socket;
	private DatagramPacket packet;
	private DatagramPacket packetBroadcast;
	private Player player;
	private World world;
	
	private InetAddress ip;
	private InetAddress group;
	
	public GameClient(Player player, World world) {
		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName("bejobo.servegame.com");
			group = InetAddress.getByName("bejobo.servegame.com");
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.player = player;
		this.world = world;
	}
	
	public boolean sendJoin() {
		if(player != null) {
			try {
				String formattedStrLength = GameServer.formatLength(player.name.length());
				byte[] buf = (GameServer.M2DP_DATA_JOIN + formattedStrLength + player.name).getBytes();
				
				packet = new DatagramPacket(buf, buf.length, ip, GameServer.PORT);
				socket.send(packet);
				
				buf = new byte[256];
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				String reply = new String(buf);
				if(reply.contains(GameServer.M2DP_REPLY_JOIN_ACCEPT)) {
					player.setConnected(true);
					System.out.println("Got join accept from server");
					return true;
				} else {
					System.out.println("Attempted to join server, reply: " + reply);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void sendUpdatePosition() {
		if(player.isConnected()) {
			try {
				String strPosition = player.name + "," + (int) player.x + "," + (int) player.y;
				String formattedStrLength = GameServer.formatLength(strPosition.length());
				byte[] buf = (GameServer.M2DP_DATA_UPDATE_POSITION + formattedStrLength + strPosition).getBytes();
				packet = new DatagramPacket(buf, buf.length, ip, GameServer.PORT);
				socket.send(packet);
				
//				buf = new byte[256];
//				packet = new DatagramPacket(buf, buf.length);
//				socket.receive(packet);
//				
//				String reply = new String(packet.getData());
//				if(reply.contains(GameServer.M2DP_REPLY_UPDATE_POSITION_ACCEPT)) {
//					System.out.println("Got update position accept from server");
//				} else {
//					System.out.println("Attempted to update position, got reply: " + reply);
//				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void listen() {
		if(!player.isConnected()) throw new IllegalArgumentException("Player attempting to listen while not connected");
		new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						System.out.println("Listening for broadcast packet!");
						byte[] buf = new byte[256];
						packetBroadcast = new DatagramPacket(buf, buf.length);
						socket.receive(packetBroadcast);
						
//						if(packetBroadcast.getPort() == GameServer.PORT_GROUP) {
//							System.out.println("Recv from broadcast port on gameserver");
//						}
						System.out.println("Recv from: " + packetBroadcast.getAddress().toString() + ": " + packetBroadcast.getPort());
						
						String msg = new String(packetBroadcast.getData());
						System.out.println("RECEIVED A BROADCAST PACKET WITH MESSAGE: " + msg);
						
						int dataId = Integer.parseInt(msg.substring(GameServer.M2DP_DATA_ID_POS_START, GameServer.M2DP_DATA_ID_POS_END));
						int dataLength = Integer.parseInt(msg.substring(GameServer.M2DP_DATA_MSG_LENGTH_POS_START, GameServer.M2DP_DATA_MSG_LENGTH_POS_END));
						int startIndex = GameServer.M2DP_DATA_MSG_LENGTH_POS_END;
						int endIndex = startIndex + dataLength;
						String data = msg.substring(startIndex, endIndex);
						
						switch(dataId) {
						case GameServer.M2DP_UPDATE_POSITION:
							int tokens = 0;
							int lastTokenIndex = 0;
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
							//if(player.name.equalsIgnoreCase(name)) break;
							Player player = new Player(null, 0xffffffff, name);
							player.x = x;
							player.y = y;
							world.updatePlayer(player);
							break;
						}
						
						System.out.println("In listen thread, message from server: " + msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public void listenUpdatePositions() {
		try {
			System.out.println("Listening for broadcast packet!");
			byte[] buf = new byte[256];
			packetBroadcast = new DatagramPacket(buf, buf.length);
			socket.receive(packetBroadcast);
			
//			if(packetBroadcast.getPort() == GameServer.PORT_GROUP) {
//				System.out.println("Recv from broadcast port on gameserver");
//			}
			System.out.println("Recv from: " + packetBroadcast.getAddress().toString() + ": " + packetBroadcast.getPort());
			
			if(packetBroadcast.getPort() != GameServer.PORT_GROUP) return;
			String msg = new String(packetBroadcast.getData());
			
			System.out.println("RECEIVED A BROADCAST PACKET WITH MESSAGE: " + msg);
			
			
			int dataId = Integer.parseInt(msg.substring(GameServer.M2DP_DATA_ID_POS_START, GameServer.M2DP_DATA_ID_POS_END));
			int dataLength = Integer.parseInt(msg.substring(GameServer.M2DP_DATA_MSG_LENGTH_POS_START, GameServer.M2DP_DATA_MSG_LENGTH_POS_END));
			int startIndex = GameServer.M2DP_DATA_MSG_LENGTH_POS_END;
			int endIndex = startIndex + dataLength;
			String data = msg.substring(startIndex, endIndex);
			
			switch(dataId) {
			case GameServer.M2DP_UPDATE_POSITION:
				int tokens = 0;
				int lastTokenIndex = 0;
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
				if(player.name.equalsIgnoreCase(name)) break;
				world.updatePlayer(new Player(null, Game.assignPlayerColor(), name));
				break;
			}
			
			System.out.println("In listen thread, message from server: " + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		new GameClient(null).start();
//	}
}
