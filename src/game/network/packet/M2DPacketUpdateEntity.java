package game.network.packet;

import game.network.parcel.M2DParcelUpdateAttackEntity;
import game.network.parcel.M2DParcelUpdateEntity;

public class M2DPacketUpdateEntity extends M2DPacket {

	private M2DParcelUpdateEntity parcel;
	
	public M2DPacketUpdateEntity(int id, M2DParcelUpdateEntity parcel) {
		super(id);
		
		this.parcel = parcel;
	}
	
}
