package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;
import game.server.GameServer;

public class M2DPacketJoin extends M2DPacket {
	
	private PlayerOnline player;
	
	public M2DPacketJoin(int id) {
		super(id);
	}
	
	public void send(DatagramSocket socket, InetAddress dst, int port) {
		if(player == null) throw new RuntimeException("Attempted to send M2DPacketUpdatePosition with player set to 'null'");
		try {
			String data = player.getUsername();
			String dataLength = GameServer.formatLength(data.length());
			String msg = (M2DProtocol.M2DP_DATA_JOIN + dataLength + data);
			byte[] buf = msg.getBytes();
			packet = new DatagramPacket(buf, buf.length, ip, port);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void recv(DatagramSocket socket) {
		try {
			byte[] buf = new byte[256];
			packet = new DatagramPacket(buf, buf.length);
			
			socket.receive(packet);
			reply = new String(packet.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setPlayer(PlayerOnline player) {
		this.player = player;
	}
}
