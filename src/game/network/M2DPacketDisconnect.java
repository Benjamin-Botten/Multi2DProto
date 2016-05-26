package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;
import game.server.GameServer;

public class M2DPacketDisconnect extends M2DPacket {

	private PlayerOnline player;
	
	public M2DPacketDisconnect(InetAddress ip, int port, PlayerOnline player) {
		super(ip, port);
		
		this.player = player;
	}
	
	public void send(DatagramSocket socket) {
		try {
			String data = player.getUsername();
			String dataLength = GameServer.formatLength(data.length());
			String msg = (GameServer.M2DP_DATA_DISCONNECT + dataLength + data);
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

}
