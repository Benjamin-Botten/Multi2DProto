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
import game.network.M2DPacketJoin;
import game.network.M2DPacketUpdateSprite;
import game.server.GameServer;

/**
 * The game client for game-updates that don't require reliable connection
 * ClientTCP.java is intended for connection items such as gamechat, etc.
 * 
 * @author robot
 *
 */
public class GameClient {
	private DatagramSocket socket;
	private DatagramPacket packet;
	private DatagramPacket packetBroadcast;
	private PlayerOnline player;
	private World world;

	private InetAddress ip; // ip_dst

	public GameClient(PlayerOnline player, World world) {
		if(player == null) {
			throw new IllegalArgumentException("Attempted to create game client with a null player");
		}
		try {
			socket = new DatagramSocket();
			ip = InetAddress.getByName("bejobo.servegame.com");
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
			try {
				M2DPacketJoin join = new M2DPacketJoin(InetAddress.getByName("bejobo.servegame.com"), GameServer.PORT,
						player);
				join.send(socket);
				join.recv(socket);
				DatagramPacket packet = join.getPacket();
				player.setAddress(packet.getAddress());
				player.setPort(packet.getPort());
				if (join.getReply().contains(GameServer.M2DP_REPLY_JOIN_ACCEPT)) {
					System.out.println("Login attempt: " + join.getReply());
					player.setConnected(true);
				} else {
					System.out.println("Login attempt: " + join.getReply());
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void sendUpdatePosition() {
		if (player != null) {
			if(player.isConnected()) {
			try {
				player.x = world.getPlayer().x;
				player.y = world.getPlayer().y;
				String strPosition = player.getUsername() + "," + (int) player.x + "," + (int) player.y;
				String formattedStrLength = GameServer.formatLength(strPosition.length());
				byte[] buf = (GameServer.M2DP_DATA_UPDATE_POSITION + formattedStrLength + strPosition).getBytes();
				packet = new DatagramPacket(buf, buf.length, ip, GameServer.PORT);
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}
	}

	public void sendUpdateSprite() {
		if (player != null) {
			if(player.isConnected()) {
			try {
				player.setSprite(world.getPlayer().getSprite());
				String strSprite = player.getUsername() + "," + player.getSprite().getCurrentRowIndex() + "," + player.getSprite().getCurrentColumnIndex() + 
						"," + player.getSprite().getDirectionMovement() + "," + player.getSprite().getCurrentFrame();
				String formattedStrLength = GameServer.formatLength(strSprite.length());
				byte[] buf = (GameServer.M2DP_DATA_UPDATE_SPRITE + formattedStrLength + strSprite).getBytes();
				packet = new DatagramPacket(buf, buf.length, ip, GameServer.PORT);
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
//			player.setSprite(world.getPlayer().getSprite());
//			M2DPacketUpdateSprite updateSprite = new M2DPacketUpdateSprite(ip, GameServer.PORT);
//			updateSprite.send(player, socket);
		}
	}

	public synchronized void listen() {
		if (!player.isConnected())
			throw new IllegalArgumentException("Player attempting to listen while not connected");
		
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						System.out.println("Listening for game-update packet!");
						byte[] buf = new byte[256];
						packetBroadcast = new DatagramPacket(buf, buf.length);
						socket.receive(packetBroadcast);

						// if(packetBroadcast.getPort() ==
						// GameServer.PORT_GROUP) {
						// System.out.println("Recv from broadcast port on
						// gameserver");
						// }
						// System.out.println("Recv from: " +
						// packetBroadcast.getAddress().toString() + ": " +
						// packetBroadcast.getPort());

						String msg = new String(packetBroadcast.getData());
						System.out.println("RECEIVED A BROADCAST PACKET WITH MESSAGE: " + msg);

						int dataId = Integer.parseInt(
								msg.substring(GameServer.M2DP_DATA_ID_POS_START, GameServer.M2DP_DATA_ID_POS_END));
						int dataLength = Integer.parseInt(msg.substring(GameServer.M2DP_DATA_MSG_LENGTH_POS_START,
								GameServer.M2DP_DATA_MSG_LENGTH_POS_END));
						int startIndex = GameServer.M2DP_DATA_MSG_LENGTH_POS_END;
						int endIndex = startIndex + dataLength;
						String data = msg.substring(startIndex, endIndex);
						switch (dataId) {
						case GameServer.M2DP_UPDATE_SPRITE:
							int tokens = 0;
							int lastTokenIndex = 0;
							String name = "";
							int rowIndex = 0, columnIndex = 0;
							int directionMovement = 0, currentFrame = 0;
							for (int i = 0; i < data.length(); ++i) {
								if (tokens == 0) {
									if (Character.compare(data.charAt(i), ',') == 0) {
										name = data.substring(0, i);
										lastTokenIndex = i + 1;
										tokens++;
									}
								} else if (tokens == 1) {
									if (Character.compare(data.charAt(i), ',') == 0) {
										rowIndex = Integer.parseInt(data.substring(lastTokenIndex, i));
										lastTokenIndex = i + 1;
										tokens++;
									}
								} else if (tokens == 2) {
									if (Character.compare(data.charAt(i), ',') == 0) {
										columnIndex = Integer.parseInt(data.substring(lastTokenIndex, i));
										lastTokenIndex = i + 1;
										tokens++;
									}
								} else if (tokens == 3) {
									if (Character.compare(data.charAt(i), ',') == 0) {
										directionMovement = Integer.parseInt(data.substring(lastTokenIndex, i));
										lastTokenIndex = i + 1;
										tokens++;
									}
								} else if (tokens == 4) {
									currentFrame = Integer.parseInt(data.substring(lastTokenIndex, dataLength));
									tokens++;
									break;
								}
							}
							player = world.getPlayerOnlineByName(name);
							if(player != null) {
								player.getSprite().setRowIndex(rowIndex);
								player.getSprite().setColumnIndex(columnIndex);
								player.getSprite().setDirectionMovement(directionMovement);
								player.getSprite().setCurrentFrame(currentFrame);
							} else {
								world.addEntity(new PlayerOnline(name, packetBroadcast.getAddress(), packetBroadcast.getPort()));
							}
							break;
							
						case GameServer.M2DP_UPDATE_POSITION:
							tokens = 0;
							lastTokenIndex = 0;
							name = "";
							int x = 0, y = 0;
							for (int i = 0; i < data.length(); ++i) {
								if (tokens == 0) {
									if (Character.compare(data.charAt(i), ',') == 0) {
										name = data.substring(0, i);
										lastTokenIndex = i + 1;
										tokens++;
									}
								} else if (tokens == 1) {
									if (Character.compare(data.charAt(i), ',') == 0) {
										x = Integer.parseInt(data.substring(lastTokenIndex, i));
										lastTokenIndex = i + 1;
										tokens++;
									}
								} else if (tokens == 2) {
									y = Integer.parseInt(data.substring(lastTokenIndex, dataLength));
									tokens++;
									break;
								}
							}
							PlayerOnline player = world.getPlayerOnlineByName(name);
							if(player != null) {
								player.x = x;
								player.y = y;
							} else {
								world.addEntity(new PlayerOnline(name, packetBroadcast.getAddress(), packetBroadcast.getPort()));
							}
							
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	// TODO: Implement disconnect
	public void sendDisconnect() {
	}
}