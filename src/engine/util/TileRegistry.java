package engine.util;

import engine.world.tile.Tile;

public class TileRegistry implements IDRegistry {

	public TileRegistry() {
	}

	@Override
	public String getName(int id) {
		if(id < 0 || id >= Tile.tiles.length) return "";
		if(Tile.tiles[id] == null) return "";
		
		return Tile.tiles[id].toString();
	}

	@Override
	public int getId(String name) {
		for(int i = 0; i < Tile.tiles.length; ++i) {
			if(Tile.tiles[i].toString().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}
}
