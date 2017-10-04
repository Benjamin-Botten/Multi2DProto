package game.network.parcel;

import game.network.M2DProtocol;

public class M2DParcelUpdateAttackEntity extends M2DParcel {
	
	//Its id
	public int id;
	
	//The attack entity's position
	public int x, y;
	
	//Reached target yet? x = 0 false or x != 0 true
	public int reachedTarget;
	
	//Its Net ID
	public int netId;
	
	//The source entity of the attack entity on the network
	public int ownerNetId;
	
	//The target entity to affect
	public int targetNetId;
	
	public void fill(M2DProtocol m2dp) {
		String data = m2dp.getData();
//		System.out.println("IN PARCEL: filling parcel with " + data);
		int tokens = 0;
		int lastTokenIndex = 0;
		for (int i = 0; i < data.length(); ++i) {
			if (tokens == 0) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					id = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 1) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					x = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			}  else if (tokens == 2) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					y = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 3) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					reachedTarget = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 4) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					netId = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 5) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					ownerNetId = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 6) {
				targetNetId = Integer.parseInt(data.substring(lastTokenIndex, data.length()));
				tokens++;
				break;
			}
			
		}
	}
}
