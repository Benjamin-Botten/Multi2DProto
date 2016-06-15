package engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import engine.visuals.viewport.Viewport;
import engine.world.entity.Entity;
import engine.world.entity.LifeFlaskEntity;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import engine.world.entity.SteelArrowEntity;
import engine.world.entity.SustainFlaskEntity;
import engine.world.entity.WoodenBowEntity;
import engine.world.tile.Tile;
import game.Game;

public class World {
	
	public static final int w = 512 * 8, h = 512 * 8;
	public static final int tilesWidth = w / 8;
	public static final int tilesHeight = h / 8;
	
	private byte[] collisionMap; //this is a byte array that holds either 0 or 1, it is a map of collidable tiles in this world
	private int[] tiles;
	private List<Entity> entities = new ArrayList<>();
	private List<PlayerOnline> players = new ArrayList<>();
	
	private Player player;
	
	public World(Player player) {
		this.player = player;
		
		
		tiles = new int[tilesWidth * tilesHeight];
		for(int i = 0; i < tiles.length; ++i) { //Init to -1 (Anti-tile)
			tiles[i] = -1;
		}
		
		for(int j = 0; j < tilesHeight; ++j) {
			for(int i = 0; i < tilesWidth; ++i) {
				if(tiles[i + j * tilesWidth] != -1) {
					continue;
				}
				
				if(i % 16 == 11) {
//					if(new Random().nextInt() % 6 == 2) {
						tiles[i + j * tilesWidth] = Tile.stone.id;
//					}
				}
				else if(i == 15 && j == 12) {
					tiles[i + j * tilesWidth] = Tile.water.id;
					tiles[(i + 1) + j * tilesWidth] = Tile.water.id;
					tiles[(i + 1) + (j + 1) * tilesWidth] = Tile.water.id;
					tiles[(i + 2) + (j + 1) * tilesWidth] = Tile.water.id;
					tiles[(i + 2) + (j + 2) * tilesWidth] = Tile.water.id;
					tiles[(i + 2) + (j + 0) * tilesWidth] = Tile.sand.id;
					tiles[(i + 3) + (j + 1) * tilesWidth] = Tile.sand.id;
					tiles[(i + 0) + (j + 1) * tilesWidth] = Tile.sand.id;
				} else {
					tiles[i + j * tilesWidth] = Tile.grass.id;
				}
			}
		}
		
		
//		entities.add(new LifeFlaskEntity(32, 32));
		entities.add(new LifeFlaskEntity(30 * 2, 30 * 2));
		entities.add(new SteelArrowEntity(42 * 2, 24 * 2));
		entities.add(new WoodenBowEntity(27 * 2, 16 * 2));
		entities.add(new SustainFlaskEntity(17 * 16, 12 * 16 - 6));
		entities.add(new SustainFlaskEntity(13 * 16, 15 * 16 - 6));
		for(int y = 0; y < 50; ++y) {
			for(int x = 0; x < 50; ++x) {
//				entities.add(new SustainFlaskEntity(48 + i * 9, j * 8));
			}
		}
	}
	
	public void tick() {
		for(Entity entity : entities) {
			entity.tick();
		}
//		for(PlayerOnline playerOnline : players) {
//			playerOnline.tick();
//		}
		
		player.tick();
		player.handle(this);
	}
	
	/**
	 * @note Render all entities, the world, etc, first
	 * @param viewport
	 */
	public void render(Viewport viewport) {
		viewport.render(this);
		
		for(Entity entity : entities) {
			entity.render(viewport);
		}
		for(PlayerOnline playerOnline : players) {
			playerOnline.render(viewport);
		}
		
		player.render(viewport);
	}
	
	
	public void addEntity(Entity entity) {
		if(entity == null) {
			return;
		}
		//If there's an attempt at adding a player to the world, thwart
//		if(entity instanceof Player) {
//			return;
//		}
		if(entity instanceof PlayerOnline) {
			PlayerOnline player = (PlayerOnline) entity;
			//Check to see if this online player already is logged on / in the world
			for(int i = 0; i < players.size(); ++i) {
				if(player.getUsername().equalsIgnoreCase(players.get(i).getUsername())) {
					System.out.println("Attempted adding online player to world with duplicate username");
					return;
				}
			}
			System.out.println("Added new player to world.");
			players.add((PlayerOnline) entity);
			return;
		}
		
		entities.add(entity);
	}
	
	public void updateEntity(Entity entity) {
		if(entity == null) {
			return;
		}
//		if(entity instanceof Player) {
//			return;
//		}
		if(entity instanceof PlayerOnline) {
			PlayerOnline player = (PlayerOnline) entity;
			for(int i = 0; i < players.size(); ++i) {
				if(player.getUsername().equalsIgnoreCase(players.get(i).getUsername())) {
					players.set(i, player);
					return;
				}
			}
			addEntity(player);
		}
		
		for(int i = 0; i < entities.size(); ++i) {
			Entity tmp = entities.get(i);
			if(entity.getNetId() == tmp.getNetId()) {
				entities.set(i, entity);
				break;
			}
		}
	}
	
	public void removePlayerByName(String username) {
		for(int i = 0; i < players.size(); ++i) {
			if(players.get(i).getUsername().equalsIgnoreCase(username)) {
				players.remove(i);
				return;
			}
		}
	}
	
	public PlayerOnline getPlayerByName(String name) {
		for(int i = 0; i < players.size(); ++i) {
			if(players.get(i).getUsername().equalsIgnoreCase(name)) {
				return players.get(i);
			}
		}
		return null;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Tile getTile(int x, int y) {
		//Normalize coordinates to tile space
		x /= 16;
		y /= 16;
		if(x < 0 || y < 0 || x >= tilesWidth || y >= tilesHeight) throw new IllegalArgumentException("Trying to find tile out of bounds of the world size");
		
		return Tile.tiles[tiles[x + y * tilesWidth]];
	}
	
	public void setTile(int id, int x, int y) {
		if(x < 0 || y < 0 || x >= w || y >= h) throw new IllegalArgumentException("Trying to set tile out of bounds of the world size");
		tiles[x + y * w] = id;
	}
	
	public boolean isColliding(Entity entity) {
		if(getTile((int) entity.x, (int) entity.y).solid()) {
			return true;
		}
		
		int entityTileX = (int) entity.x >> 4;
		int entityTileY = (int) entity.y >> 4;
		for(int i = 0; i < entities.size(); ++i) {
			Entity curEntity = entities.get(i);
			int curTileX = (int) curEntity.x >> 4;
			int curTileY = (int) curEntity.y >> 4;
			
			for(int j = 0; j < 2; ++j) {
				for(int k = 0; k < 2; ++k) {
					if(entityTileX + j == curTileX && entityTileY + k == curTileY) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public Entity getInteractable(Entity entity) {
		int entityTileX = (int) entity.x >> 4;
		int entityTileY = (int) entity.y >> 4;
		
		for(int i = 0; i < entities.size(); ++i) {
			Entity curEntity = entities.get(i);
			int curTileX = (int) curEntity.x >> 4;
			int curTileY = (int) curEntity.y >> 4;
			
			for(int j = 0; j < 2; ++j) {
				for(int k = 0; k < 2; ++k) {
					if(entityTileX + j * 2 == curTileX && entityTileY + k * 2 == curTileY) {
						return curEntity;
					}
				}
			}
		}
		return null;
	}

	public boolean removeEntity(Entity entity) {
		return entities.remove(entity);
	}
}
