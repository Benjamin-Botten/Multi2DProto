package engine.world.tile;

import engine.visuals.viewport.Viewport;

public class Tile {
	
	public static final int w = 8;
	public static final int h = 8;
	
	public final int id;
	
	public static final Tile[] tiles = new Tile[1024];
	
	public static final GrassTile grass = new GrassTile(0);
	
	public Tile(int id) {
		if(tiles[id] != null) throw new IllegalArgumentException("Attempted creating tile with id (" + id + ") that already exists");
		tiles[id] = this;
		
		this.id = id;
	}
	
	public void tick() {
		
	}
	
	public void render(Viewport viewport) {
		
	}
	
	public boolean solid() {
		return false;
	}
	
	public String toString() {
		return "AbstractTile";
	}
}
