package game.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;
import game.server.GameServer;

public class M2DPacketUpdateSprite extends M2DPacket {

	public M2DPacketUpdateSprite(InetAddress ip, int port) {
		super(ip, port);
	}
	
	public void send(PlayerOnline player, DatagramSocket socket) {
		String data = player.getUsername() + "," + player.getSprite().getCurrentRowIndex() + "," + player.getSprite().getCurrentColumnIndex();
		String dataLength = GameServer.formatLength(data.length());
		String msg = (player.getUsername() + dataLength + data);
		byte[] buf = msg.getBytes();
		packet = new DatagramPacket(buf, buf.length, ip, port);
	}
	
}
