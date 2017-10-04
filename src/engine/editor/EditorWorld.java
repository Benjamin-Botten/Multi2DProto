package engine.editor;

import java.util.ArrayList;
import java.util.List;

import engine.world.Level;
import engine.world.entity.Entity;
import engine.world.tile.Tile;

public class EditorWorld {
	private int w, h;
	private int[] tiles;
	private List<Entity> entities;
	private Level level;
	
	public EditorWorld() {
	}
	
	public EditorWorld(Level level, int[] tiles, int w, int h, List<Entity> entities) {
		if(tiles == null) {
			throw new IllegalArgumentException("Attempted creating editor world with null tiles");
		}
		if(entities == null) {
			throw new IllegalArgumentException("Attempted creating editor world with null entities");
		}
		if(level == null) {
			throw new IllegalArgumentException("Attempted creating editor world with null level");
		}
		
		this.level = level;
		this.tiles = tiles;
		this.w = w;
		this.h = h;
		this.entities = entities;
	}
	
	/** Getters */
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
	
	public int[] getTiles() {
		return tiles;
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public Level getLevel() {
		return level;
	}
	
	/** Setters */
	
	public void setWidth(int w) {
		this.w = w;
	}
	
	public void setHeight(int h) {
		this.h = h;
	}
	
	public void setTiles(int[] tiles) {
		if(tiles != null) {
			this.tiles = tiles;
		}
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	public void setTile(Tile tile, int xp, int yp) {
		xp >>= 4;
		yp >>= 4;
		
		if(xp < 0 || xp >= w || yp < 0 || yp >= h) return;
		
		tiles[xp + yp * w] = tile.id;
	}
	
	/** Operations */
	public void addEntity(Entity entity) {
		if(entity == null) {
			throw new IllegalArgumentException("Attempted adding null entity to editor-world");
		}
		
		entities.add(entity);
	}
}
