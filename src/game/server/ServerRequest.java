package game.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import engine.world.entity.action.Action;
import game.network.M2DPHandler;
import game.network.M2DProtocol;
import game.network.packet.M2DPacket;
import game.network.packet.M2DPacketDisconnect;
import game.network.packet.M2DPacketJoin;
import game.network.packet.M2DPacketUpdateAttackEntity;
import game.network.packet.M2DPacketUpdatePlayer;

public class ServerRequest extends Thread implements M2DPHandler {

	private String msg;
	private int port;
	private InetAddress ip;
	private GameServer gameServer;
	private DatagramPacket packet;

	public ServerRequest(GameServer gameServer, DatagramPacket packet) {
		this.gameServer = gameServer;
		this.packet = packet;

		msg = new String(packet.getData());
		port = packet.getPort();
		ip = packet.getAddress();
	}

	@Override
	public void run() {
		handle(msg, ip, port);
	}

	@Override
	public void handle(String msg, InetAddress ip, int port) {
		M2DProtocol m2dp = M2DProtocol.parseMessage(msg, ip, port);
		M2DPacket packet = M2DPacket.packets[m2dp.getDataId()];
		packet.parse(m2dp);

		if (packet instanceof M2DPacketUpdatePlayer) {
			// System.out.println("Got packet update player from client");
			M2DPacketUpdatePlayer updatePlayer = (M2DPacketUpdatePlayer) packet;
			String username = updatePlayer.getParcel().username;
			if (username == null)
				return;

			Player player = gameServer.getPlayerByName(username);
			if (player == null) {
				System.out.println("In update player packet, username returned NULL player");
				return;
			}
			
			int x = updatePlayer.getParcel().x;
			int y = updatePlayer.getParcel().y;
			int rowIndex = updatePlayer.getParcel().rowIndex;
			int columnIndex = updatePlayer.getParcel().columnIndex;
			int directionFacing = updatePlayer.getParcel().directionFacing;
			int currentFrame = updatePlayer.getParcel().currentFrame;
			int targetNetId = updatePlayer.getParcel().targetNetId;
			int actionId = updatePlayer.getParcel().actionId;
			int actionProgress = updatePlayer.getParcel().actionProgress;
			int netId = updatePlayer.getParcel().netId;
			int life = updatePlayer.getParcel().life;

//			if(targetNetId != 0) {
//			 System.out.println("Got update player: (" + x + ", " + y + ") with sprite ROW_INDEX " 
//			 + rowIndex + ", COLUMN_INDEX " + columnIndex + ", DIR_FACING " + directionFacing + ", CURRENT_FRAME " +
//			 currentFrame + ", TARGET_NET_ID " + targetNetId + ", ACTION_ID " + actionId + ", NET_ID " + netId + ", LIFE " + life + ", ACTION_PROGRESS " +
//			 actionProgress);
//			}
			 
			player.getPlayerOnline().setPortUDP(port);
			player.x = x;
			player.y = y;
			player.getSprite().setRowIndex(rowIndex);
			player.getSprite().setColumnIndex(columnIndex);
			player.getSprite().setDirectionFacing(directionFacing);
			player.getSprite().setCurrentFrame(currentFrame);
			player.setCurrentAction(Action.getAction(actionId));
			//player.setActionProgress(actionProgress);
			player.setTarget(gameServer.getPlayerById(targetNetId));
			player.setNetId(netId);
			
			gameServer.serverActionHandler.handle(player, player.getTarget(), player.getCurrentAction());
			
//			for(int i = 0; i < gameServer.getPlayers().size(); ++i) {
//				Player plr = gameServer.getPlayers().get(i);
//				System.out.println("Player #" + plr.getNetId() + " life > " + plr.getLife());
//			}
		}
		if(packet instanceof M2DPacketUpdateAttackEntity) {
//			System.out.println("x");
		}
//		broadcastPlayerUpdate(msg, ip, port);
	}

	public void broadcastPlayerUpdate(String msg, InetAddress ip, int port) {
		List<Player> players = gameServer.getPlayers();
		// System.out.println("Number of players online: " + players.size());
		// System.out.println("Broadcasting player update to clients");
		for (int i = 0; i < players.size(); ++i) {
//			if (players.get(i).getAddress().equals(ip) && players.get(i).getPort() == port)
//				continue;
			
			M2DPacket.updatePlayer.send(gameServer.getSocket(), ip, port, players.get(i));
		}
	}
}
