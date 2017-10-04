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
import engine.world.entity.action.Action;
import game.Game;
import game.network.M2DPHandler;
import game.network.M2DProtocol;
import game.network.packet.M2DPacket;
import game.network.packet.M2DPacketDisconnect;
import game.network.packet.M2DPacketDisconnectReply;
import game.network.packet.M2DPacketJoin;
import game.network.packet.M2DPacketUpdateAttackEntity;
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
	
	private Player player;
	private World world;

	private InetAddress ip; // ip_dst
	private String hostname;
	
	//TCP Client
	ClientTCP tcpClient;

	public GameClient(String hostname, Player player, World world) {
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
//				System.out.println("synchronizing player that sent packet");
				synchronizePlayerState(updatePlayer);
//				world.getPlayer().setLife(updatePlayer.getParcel().life);
				return;
			}
			
			int x = updatePlayer.getParcel().x;
			int y = updatePlayer.getParcel().y;
			int rowIndex = updatePlayer.getParcel().rowIndex;
			int columnIndex = updatePlayer.getParcel().columnIndex;
			int directionFacing = updatePlayer.getParcel().directionFacing;
			int currentFrame = updatePlayer.getParcel().currentFrame;
			int actionId = updatePlayer.getParcel().actionId;
			int actionProgress = updatePlayer.getParcel().actionProgress;
			int targetNetId = updatePlayer.getParcel().targetNetId;
			int netId = updatePlayer.getParcel().netId;
			int life = updatePlayer.getParcel().life;
			
//			 System.out.println("Got update player: (" + x + ", " + y + ") with sprite ROW_INDEX " 
//					 + rowIndex + ", COLUMN_INDEX " + columnIndex + ", DIR_FACING " + directionFacing + ", CURRENT_FRAME " +
//					 currentFrame + ", TARGET_NET_ID " + targetNetId + ", ACTION_ID " + actionId + ", NET_ID " + netId + ", LIFE " + life + ", ACTION_PROGRESS " +
//					 actionProgress);
			
//			System.out.println("Players online on clientside: " + world.getPlayers().size());
			Player player = world.getPlayerByName(username);
			if(player != null) {
				player.x = x;
				player.y = y;
				player.getSprite().setRowIndex(rowIndex);
				player.getSprite().setColumnIndex(columnIndex);
				player.getSprite().setDirectionFacing(directionFacing);
				player.getSprite().setCurrentFrame(currentFrame);
//				if(player.getCurrentAction() == Action.frostbite && Action.getAction(actionId) == Action.noAction) {
//					synchronized(world.attacks) {
//						world.attacks.add(new FrostbiteEntity(player, player.getTarget(), 0, (int) player.x, (int) player.y));
//					}
//				}
				if(player.isCasting()) {
					player.setCurrentAction(Action.getAction(actionId));
				}
				player.setTarget(world.getEntityByNetId(targetNetId));
				player.setLife(life);
			} else {
				Player newPlayer = new Player(username, ip, port);
				newPlayer.x = x;
				newPlayer.y = y;
				newPlayer.getSprite().setRowIndex(rowIndex);
				newPlayer.getSprite().setColumnIndex(columnIndex);
				newPlayer.getSprite().setDirectionMovement(directionFacing);
				newPlayer.getSprite().setCurrentFrame(currentFrame);
				newPlayer.setCurrentAction(Action.getAction(actionId));
				newPlayer.setTarget(world.getEntityByNetId(targetNetId));
				newPlayer.setNetId(netId);
				newPlayer.setLife(life);
				System.out.println("Adding new player to the world with NET ID > " + newPlayer.getNetId());
				world.addEntity(newPlayer);
			}
		}
		if(packet instanceof M2DPacketUpdateAttackEntity) {
			M2DPacketUpdateAttackEntity updateAttackEntity = (M2DPacketUpdateAttackEntity) packet;
			
			int id = updateAttackEntity.getParcel().id;
			int x = updateAttackEntity.getParcel().x;
			int y = updateAttackEntity.getParcel().y;
			int reachedTarget = updateAttackEntity.getParcel().reachedTarget;
			int netId = updateAttackEntity.getParcel().netId;
			int ownerNetId = updateAttackEntity.getParcel().ownerNetId;
			int targetNetId = updateAttackEntity.getParcel().targetNetId;
			
			Entity owner = world.getEntityByNetId(ownerNetId);
			Entity target = world.getEntityByNetId(targetNetId);
			
//			System.out.println("Client received an AttackEntity update > ID " + id + ", X " + x + ", Y " + y + ", NET_ID " + netId + ", OWNER_NET_ID" + ownerNetId + ", TARGET_NET_ID " + targetNetId);
			
			//AttackEntity attackEntity = new AttackEntity(owner, target, id, x, y);
			
			AttackEntity attackEntity = world.getAttackEntity(netId);
			if(attackEntity != null) {
				if(attackEntity.reachedTarget != 0) {
//					world.removeAttackEntity(netId);
				} else {
					attackEntity.x = x;
					attackEntity.y = y;
				}
			} else {
				System.out.println("Adding attack entity to the client world");
				world.addAttackEntity(AttackEntityRegistry.create(id, owner, target, netId, x, y));
			}
		}
	}
	
	private void synchronizePlayerState(M2DPacketUpdatePlayer updatePlayer) {
		if(updatePlayer.getParcel().life != player.getLife()) {
			System.out.println("Synchronizing player life-state from " + player.getLife() + ", to " + updatePlayer.getParcel().life);
			player.setLife(updatePlayer.getParcel().life);
		}
		
//		if(player.getActionQueue().peek() != Action.noAction) {
//		}
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

//						System.out.println("Received a packet on client");
						
						handle(msg, packetBroadcast.getAddress(), packetBroadcast.getPort());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void updatePlayer(Player srcPlayer) {
//		if(srcPlayer == player) {
//			return;
			//throw new RuntimeException("Updating gameclient player with instance of itself!!");
//		}
//		player.x = srcPlayer.x;
//		player.y = srcPlayer.y;
//		player.setSprite(srcPlayer.getSprite());
//		if(srcPlayer.getTarget() != null) {
//			player.setTarget(srcPlayer.getTarget());
//			System.out.println("Updating game client player with target > " + player.getTarget().getNetId());
//		}
//		player.setActionQueue(srcPlayer.getActionQueue());
		//player.setNetId(srcPlayer.getNetId());
//		player.setLife(srcPlayer.getLife());
//		player.setActionQueue(srcPlayer.getActionQueue());
//		player.setActionProgress(0);
	}

	public Player getPlayer() {
		return player;
	}

	public void removePlayerByName(String name) {
		synchronized(world) {
			world.removePlayerByName(name);
		}
	}
}