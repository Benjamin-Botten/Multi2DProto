package engine.world.tile;

import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.entity.Entity;

public class Tile {
	
	public static final int w = 8;
	public static final int h = 8;
	
	public final int id;
	
	public static final Tile[] tiles = new Tile[256];
	
	public static final AirTile air = new AirTile(0);
	public static final GrassTile grass = new GrassTile(1);
	public static final StoneTile stone = new StoneTile(2);
	public static final SandTile sand = new SandTile(3);
	public static final WaterTile water = new WaterTile(4);
	public static final TorchTile torch = new TorchTile(5, 0.8f, 80);
	
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
	
	public void interact(World world, Entity entity) {
	}
	
	public String toString() {
		return "AbstractTile";
	}
}
