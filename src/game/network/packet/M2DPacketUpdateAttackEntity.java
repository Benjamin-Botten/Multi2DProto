package game.network.packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.AttackEntity;
import engine.world.entity.FrostbiteEntity;
import engine.world.entity.Player;
import game.network.M2DProtocol;
import game.network.parcel.M2DParcelUpdateAttackEntity;
import game.server.GameServer;

public class M2DPacketUpdateAttackEntity extends M2DPacket {

	private M2DParcelUpdateAttackEntity parcel;
	
	public M2DPacketUpdateAttackEntity(int id, M2DParcelUpdateAttackEntity parcel) {
		super(id);
		
		this.parcel = parcel;
	}
	
	public void send(DatagramSocket socket, InetAddress ip, int port, AttackEntity attackEntity) {
		if(attackEntity == null) throw new IllegalArgumentException("Attempted sending null AttackEntity update!");
		try {
			String data = attackEntity.id +
					"," +
					(int) attackEntity.x +
					"," +
					(int) attackEntity.y +
					"," +
					attackEntity.reachedTarget +
					"," +
					attackEntity.getNetId() +
					"," +
					attackEntity.getOwnerNetId() +
					"," +
					attackEntity.getTargetNetId();
			
			System.out.println("In M2DPacketUpdateAttackEntity.send, ATTACK_ENTITY_ID > " + attackEntity.id + ", NET_ID " + attackEntity.getNetId());
			
			String dataLength = GameServer.formatLength(data.length());
			String msg = (M2DProtocol.M2DP_DATA_UPDATE_ATTACK_ENTITY + dataLength + data);
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
	
	public M2DParcelUpdateAttackEntity getParcel() {
		return parcel;
	}
	
}
