package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;
import game.server.GameServer;

public class M2DPacketDisconnect extends M2DPacket {

	public M2DPacketDisconnect(int id) {
		super(id);
		
	}
	
	public void send(PlayerOnline player, DatagramSocket socket, InetAddress dst, int port) {
		try {
			String data = player.getUsername();
			String dataLength = GameServer.formatLength(data.length());
			String msg = (M2DProtocol.M2DP_DATA_DISCONNECT + dataLength + data);
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
