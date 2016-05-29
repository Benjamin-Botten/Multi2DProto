package game.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;
import game.server.GameServer;

public class M2DPacketUpdateSprite extends M2DPacket {

	public M2DPacketUpdateSprite(int id) {
		super(id);
	}
	
	public void send(PlayerOnline player, DatagramSocket socket) {
		String data = player.getUsername() + "," + player.getSprite().getCurrentRowIndex() + "," + player.getSprite().getCurrentColumnIndex() + 
				"," + player.getSprite().getDirectionMovement() + "," + player.getSprite().getCurrentFrame();
		String dataLength = GameServer.formatLength(data.length());
		String msg = (M2DProtocol.M2DP_DATA_UPDATE_SPRITE + dataLength + data);
		byte[] buf = msg.getBytes();
		packet = new DatagramPacket(buf, buf.length, ip, port);
	}
	
}
