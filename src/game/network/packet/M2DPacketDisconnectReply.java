package game.network.packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;
import game.network.parcel.M2DParcel;
import game.network.parcel.M2DParcelDisconnect;
import game.server.GameServer;

public class M2DPacketDisconnectReply extends M2DPacket {

	private M2DParcelDisconnect parcel;
	
	public M2DPacketDisconnectReply(int id, M2DParcelDisconnect parcelDisconnect) {
		super(id);
		
		parcel = parcelDisconnect;
	}

	public void send(PlayerOnline player, DatagramSocket socket, InetAddress dst, int port) {
		try {
			String data = player.getUsername();
			String dataLength = GameServer.formatLength(data.length());
			String msg = (M2DProtocol.M2DP_REPLY_DISCONNECT_ACCEPT + dataLength + data);
			byte[] buf = msg.getBytes();
			packet = new DatagramPacket(buf, buf.length, dst, port);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void recv(DatagramSocket socket) {
		
	}
	
	public void parse(M2DProtocol m2dp) {
		parcel.fill(m2dp);
	}
	
	public M2DParcelDisconnect getParcel() {
		return parcel;
	}
}
