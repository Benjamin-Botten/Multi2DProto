package engine.world.tile;

public class StoneTile extends Tile {

	public StoneTile(int id) {
		super(id);
	}
	
	public boolean solid() {
		return true;
	}
	
	public String toString() {
		return "StoneTile";
	}
	
}
