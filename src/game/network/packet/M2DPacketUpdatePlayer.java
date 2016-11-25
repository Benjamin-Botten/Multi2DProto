package game.network.packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;
import game.network.parcel.M2DParcelUpdatePlayer;
import game.server.GameServer;

public class M2DPacketUpdatePlayer extends M2DPacket {

	private M2DParcelUpdatePlayer parcel;
	
	public M2DPacketUpdatePlayer(int id, M2DParcelUpdatePlayer parcel) {
		super(id);
		
		this.parcel = parcel;
	}
	
	public void send(DatagramSocket socket, InetAddress ip, int port, PlayerOnline player) {
		if(player == null) throw new IllegalArgumentException("Attempted sending null player update");
		try {
			String data = player.getUsername() + "," + (int) player.x + "," + (int) player.y + "," + player.getSprite().getRowIndex() + "," + player.getSprite().getColumnIndex() + 
					"," + player.getSprite().getDirectionMovement() + "," + player.getSprite().getCurrentFrame();
			String dataLength = GameServer.formatLength(data.length());
			String msg = (M2DProtocol.M2DP_DATA_UPDATE_PLAYER + dataLength + data);
			byte[] buf = msg.getBytes();
			packet = new DatagramPacket(buf, buf.length, ip, port);
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parse(M2DProtocol m2dp) {
		parcel.fill(m2dp);
	}
	
	public M2DParcelUpdatePlayer getParcel() {
		return parcel;
	}
}
