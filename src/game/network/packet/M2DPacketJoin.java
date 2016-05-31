package game.network.packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;
import game.network.parcel.M2DParcel;
import game.network.parcel.M2DParcelJoin;
import game.server.GameServer;

public class M2DPacketJoin extends M2DPacket {
	
	private M2DParcelJoin parcel;
	
	public M2DPacketJoin(int id, M2DParcelJoin parcel) {
		super(id);
		
		this.parcel = parcel;
	}
	
	public void send(DatagramSocket socket, InetAddress dst, int port, PlayerOnline player) {
		if(player == null) throw new RuntimeException("Attempted to send M2DPacketUpdatePosition with player set to 'null'");
		try {
			String data = player.getUsername();
			String dataLength = GameServer.formatLength(data.length());
			String msg = (M2DProtocol.M2DP_DATA_JOIN + dataLength + data);
			byte[] buf = msg.getBytes();
			packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("bejobo.servegame.com"), port);
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
	
	public void parse(M2DProtocol m2dp) {
		parcel.fill(m2dp);
	}
	
	public M2DParcelJoin getParcel() {
		return parcel;
	}
}
