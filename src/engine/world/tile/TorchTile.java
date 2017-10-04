package engine.world.tile;

public class TorchTile extends LightTile {

	
	public TorchTile(int id, float luminosity, int radius) {
		super(id, luminosity, radius);
	}
	
	public String toString() {
		return "TorchTile";
	}
}
