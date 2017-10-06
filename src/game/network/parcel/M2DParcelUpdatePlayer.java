package game.network.parcel;

import game.network.M2DProtocol;

public class M2DParcelUpdatePlayer extends M2DParcel {
	
	//Username
	public String username;
	
	//Positional data
	public int x, y;
	
	//Sprite data
	public int rowIndex, columnIndex;
	public int directionMovement, directionFacing, currentFrame;
	
	//Action-data (such as attacks, interaction animations, etc)
	public int actionId;
	public int actionProgress;
	
	//Target entity net-ID
	public int targetNetId;
	
	//Player's Network ID
	public int netId;
	
	//Entity attributes
	public int life;
	
	public void fill(M2DProtocol m2dp) {
		String data = m2dp.getData();
//		System.out.println("IN PARCEL: filling parcel with " + data);
		int tokens = 0;
		int lastTokenIndex = 0;
		for (int i = 0; i < data.length(); ++i) {
			if (tokens == 0) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					username = data.substring(0, i);
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 1) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					x = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 2) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					y = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 3) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					rowIndex = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 4) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					columnIndex = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 5) {
				if (Character.compare(data.charAt(i), ',') == 0) {
					directionFacing = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 6) {
				if(Character.compare(data.charAt(i), ',') == 0) {
					currentFrame = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 7) {
				if(Character.compare(data.charAt(i), ',') == 0) {
					actionId = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 8) {
				if(Character.compare(data.charAt(i), ',') == 0) {
					actionProgress = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 9) {
				if(Character.compare(data.charAt(i), ',') == 0) {
					targetNetId = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			}  else if (tokens == 10) {
				if(Character.compare(data.charAt(i), ',') == 0) {
					netId = Integer.parseInt(data.substring(lastTokenIndex, i));
					lastTokenIndex = i + 1;
					tokens++;
				}
			} else if (tokens == 11) {
				life = Integer.parseInt(data.substring(lastTokenIndex, data.length()));
				tokens++;
				break;
			}
			
		}
	}
}
