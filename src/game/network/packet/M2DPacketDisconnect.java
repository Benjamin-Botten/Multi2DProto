package game.network.packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;
import game.network.parcel.M2DParcelDisconnect;
import game.server.GameServer;

public class M2DPacketDisconnect extends M2DPacket {

	private M2DParcelDisconnect parcel;
	
	public M2DPacketDisconnect(int id, M2DParcelDisconnect parcel) {
		super(id);
		
		this.parcel = parcel;
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
	
	public M2DParcelDisconnect getParcel() {
		return parcel;
	}

}
