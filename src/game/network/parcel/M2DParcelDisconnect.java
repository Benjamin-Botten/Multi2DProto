package game.network.parcel;

import game.network.M2DProtocol;

public class M2DParcelDisconnect extends M2DParcel {
	
	public String username;
	
	public void fill(M2DProtocol m2dp) {
		username = m2dp.getData();
	}
}
