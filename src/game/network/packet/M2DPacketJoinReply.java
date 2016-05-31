package game.network.packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import game.network.M2DProtocol;
import game.server.GameServer;

public class M2DPacketJoinReply extends M2DPacket {

	public M2DPacketJoinReply(int id) {
		super(id);
	}

	/**
	 * 
	 */
	public void send(DatagramSocket socket, InetAddress dst, int port) {
		try {
			String data = M2DProtocol.M2DP_REPLY_JOIN_ACCEPT;
//			String dataLength = GameServer.formatLength(data.length());
			String msg = (data);
			byte[] buf = msg.getBytes();
			packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("bejobo.servegame.com"), port);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
