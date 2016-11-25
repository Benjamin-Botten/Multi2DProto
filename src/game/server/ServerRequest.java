package game.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import engine.world.entity.PlayerOnline;
import game.network.M2DPHandler;
import game.network.M2DProtocol;
import game.network.packet.M2DPacket;
import game.network.packet.M2DPacketDisconnect;
import game.network.packet.M2DPacketJoin;
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

			PlayerOnline player = gameServer.getPlayerByName(username);
			if (player == null) {
				return;
			}
			int x = updatePlayer.getParcel().x;
			int y = updatePlayer.getParcel().y;
			int rowIndex = updatePlayer.getParcel().rowIndex;
			int columnIndex = updatePlayer.getParcel().columnIndex;
			int directionMovement = updatePlayer.getParcel().directionMovement;
			int currentFrame = updatePlayer.getParcel().currentFrame;

			// System.out.println("Got update player: (" + x + ", " + y + ")
			// with sprite RI " + rowIndex + ", CI "
			// + columnIndex + ", DIRMOV " + directionMovement + ", CURFRAME " +
			// currentFrame);

			player.x = x;
			player.y = y;
			player.getSprite().setRowIndex(rowIndex);
			player.getSprite().setColumnIndex(columnIndex);
			player.getSprite().setDirectionMovement(directionMovement);
			player.getSprite().setCurrentFrame(currentFrame);
			// PlayerOnline newPlayer = new PlayerOnline(username, ip, port);
			// newPlayer.x = x;
			// newPlayer.y = y;
			// newPlayer.getSprite().setRowIndex(rowIndex);
			// newPlayer.getSprite().setColumnIndex(columnIndex);
			// newPlayer.getSprite().setDirectionMovement(directionMovement);
			// newPlayer.getSprite().setCurrentFrame(currentFrame);
			// newPlayer.setConnected(true);
			// gameServer.addPlayer(newPlayer);
		}
		broadcastPlayerUpdate(msg, ip, port);
	}

	public void broadcastPlayerUpdate(String msg, InetAddress ip, int port) {
		List<PlayerOnline> players = gameServer.getPlayers();
		// System.out.println("Number of players online: " + players.size());
		// System.out.println("Broadcasting player update to clients");
		for (int i = 0; i < players.size(); ++i) {
			if (players.get(i).getAddress().equals(ip) && players.get(i).getPort() == port)
				continue;
			// byte[] buf = msg.getBytes();
			// packet = new DatagramPacket(buf, buf.length,
			// players.get(i).getAddress(), players.get(i).getPort());
			// System.out.println("Sending packet: \"" + msg + "\", " +
			// players.get(i).getAddress().toString() + ", " +
			// players.get(i).getPort());
			// gameServer.getSocket().send(packet);
			M2DPacket.updatePlayer.send(gameServer.getSocket(), ip, port, players.get(i));
		}
	}
}
