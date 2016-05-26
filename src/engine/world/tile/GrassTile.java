package engine.world.tile;

public class GrassTile extends Tile {

	public GrassTile(int id) {
		super(id);
	}
	
	public boolean solid() {
		return false;
	}
	
	public String toString() {
		return "GrassTile";
	}
}
