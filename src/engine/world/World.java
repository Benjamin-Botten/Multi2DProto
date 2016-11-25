package engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import engine.visuals.viewport.Viewport;
import engine.world.entity.Entity;
import engine.world.entity.ItemEntity;
import engine.world.entity.LifeFlaskEntity;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import engine.world.entity.SteelArrowEntity;
import engine.world.entity.SustainFlaskEntity;
import engine.world.entity.WoodenBowEntity;
import engine.world.item.SustainFlask;
import engine.world.tile.Tile;
import game.Game;

public class World {
	public static final int MAX_ITEMS_PER_TILE = 3;
	private Map<Integer, ArrayList<ItemEntity>> items = new HashMap<>();
	
	public static final int w = 512 * 8, h = 512 * 8;
	public static final int tilesWidth = w / 8;
	public static final int tilesHeight = h / 8;
	
	private byte[] collisionMap; //this is a byte array that holds either 0 or 1, it is a map of collidable tiles in this world
	private int[] tiles;
	private int[] bgtiles; //background tiles
//	private int[][] items; //tile items
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
				
					//if(new Random().nextInt() % 11 == 2) {
					//	tiles[i + j * tilesWidth] = Tile.stone.id;
					//}
				else if(i == 15 && j == 12) {
					tiles[i + j * tilesWidth] = Tile.water.id;
					tiles[(i + 1) + j * tilesWidth] = Tile.water.id;
					tiles[(i + 1) + (j + 1) * tilesWidth] = Tile.water.id;
					tiles[(i + 2) + (j + 1) * tilesWidth] = Tile.water.id;
					tiles[(i + 2) + (j + 2) * tilesWidth] = Tile.water.id;
					tiles[(i + 2) + (j + 0) * tilesWidth] = Tile.sand.id;
					tiles[(i + 3) + (j + 1) * tilesWidth] = Tile.sand.id;
					tiles[(i + 0) + (j + 1) * tilesWidth] = Tile.sand.id;
					tiles[(i + 5) + (j + 3) * tilesWidth] = Tile.stone.id;
					tiles[(i + 6) + (j + 5) * tilesWidth] = Tile.stone.id;
				} else {
					tiles[i + j * tilesWidth] = Tile.grass.id;
				}
			}
		}
		
		
//		entities.add(new LifeFlaskEntity(32, 32));
		addEntity(new SustainFlaskEntity(40 * 2, 40 * 2));
		addEntity(new SustainFlaskEntity(48 * 1, 48 * 1));
//		entities.add(new SteelArrowEntity(52 * 2, 24 * 2));
//		entities.add(new WoodenBowEntity(64 * 2, 16 * 2));
		addEntity(new SustainFlaskEntity(17 * 16, 12 * 16 - 6));
		addEntity(new SustainFlaskEntity(13 * 16, 15 * 16 - 6));
		for(int y = 0; y < 50; ++y) {
			for(int x = 0; x < 50; ++x) {
//				entities.add(new SustainFlaskEntity(48 + i * 9, j * 8));
			}
		}
	}
	
	public void tick() {
		ArrayList<Entity> removables = new ArrayList<>();
		for(int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			entity.tick();
			entity.handle(this);
			if(entity.canRemove()) {
				removables.add(entities.get(i));
			}
		}
		for(int i = 0; i < removables.size(); ++i) {
			entities.remove(removables.get(i));
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
		
		synchronized(players) {
			for(PlayerOnline playerOnline : players) {
				playerOnline.render(viewport);
			}
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
			synchronized(players) {
				for(int i = 0; i < players.size(); ++i) {
					if(player.getUsername().equalsIgnoreCase(players.get(i).getUsername())) {
						System.out.println("Attempted adding online player to world with duplicate username");
						return;
					}
				}
				System.out.println("Added new player \"" + player.getUsername() + "\" to the world.");
				players.add(player);
				return;
			}
		}
		if(entity instanceof ItemEntity) {
			ItemEntity item = (ItemEntity) entity;
			setTileItem((int) entity.x, (int) entity.y, item);
			setTileItem((int) entity.x + (entity.w >> 1), (int) entity.y, item);
			setTileItem((int) entity.x, (int) entity.y + (entity.h >> 1), item);
			setTileItem((int) entity.x + (entity.w >> 1), (int) entity.y + (entity.h >> 1), item);
//			System.out.println("World> Adding item entity at (" + entity.x + ", " + entity.y + ")");
			entities.add(entity);
			return;
		}
		entities.add(entity);
	}
	
	public void setTileItem(int x, int y, ItemEntity item) {
		if(x < 0 || y < 0 || x >= w || y >= h) throw new IllegalArgumentException("Attempted setting tile item out of bounds!");
		x = (x >> 4);
		y = (y >> 4);
		int id = x + y * tilesWidth;
		if(items.get(id) == null) {
			ArrayList<ItemEntity> list = new ArrayList<>();
			list.add(item);
			items.put(id, list);
		} else {
			items.get(x + y * tilesWidth).add(item);
		}
	}
	
	public ArrayList<ItemEntity> getTileItems(int x, int y) {
		x = (x >> 4);
		y = (y >> 4);
		int id = x + y * tilesWidth;
		return items.get(id);
	}
	
	public List<PlayerOnline> getPlayers() {
		return players;
	}
	
	public void updateEntity(Entity entity) {
		if(entity == null) {
			return;
		}
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
		
//		for(int i = 0; i < entities.size(); ++i) {
//			Entity tmp = entities.get(i);
//			if(entity.getNetId() == tmp.getNetId()) {
//				entities.set(i, entity);
//				break;
//			}
//		}
	}
	
	public boolean removePlayerByName(String username) {
		for(int i = 0; i < players.size(); ++i) {
			if(players.get(i).getUsername().equalsIgnoreCase(username)) {
				players.remove(i);
				return true;
			}
		}
		return false;
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
		if(entity.x < 0 || entity.x > w || entity.y < 0 || entity.y > h) return true;
		int startX = (int) entity.x;
		int middleX = startX + (entity.w >> 1);
		int endX = startX + entity.w;
		int startY = (int) entity.y;
		int middleY = startY + (entity.h >> 1);
		int endY = startY + entity.h;
		if(getTile((int) entity.x, (int) entity.y).solid() || 
				getTile((int) entity.x + (entity.w >> 1), (int) entity.y).solid() ||
				getTile((int) entity.x, (int) entity.y + (entity.h >> 1)).solid() ||
				getTile((int) entity.x + (entity.w >> 1), (int) entity.y + (entity.h >> 1)).solid() ||
				getTile((int) entity.x + (entity.w >> 1), (int) entity.y + entity.h).solid() ||
				getTile((int) entity.x + entity.w, (int) entity.y + (entity.h >> 1)).solid() ||
				getTile((int) entity.x + entity.w, (int) entity.y).solid() ||
				getTile((int) entity.x, (int) entity.y + entity.h).solid() ||
				getTile((int) entity.x + entity.w, (int) entity.y + entity.h).solid()) {
			return true;
		}
		
		
		List<ArrayList<ItemEntity>> tileItems = new ArrayList<>();
		tileItems.add(getTileItems(startX, startY));
		tileItems.add(getTileItems(middleX, startY));
		tileItems.add(getTileItems(endX, startY));
		
		tileItems.add(getTileItems(startX, middleY));
		tileItems.add(getTileItems(middleX, middleY));
		tileItems.add(getTileItems(endX, middleY));
		
		tileItems.add(getTileItems(startX, endY));
		tileItems.add(getTileItems(middleX, endY));
		tileItems.add(getTileItems(endX, endY));
		
		for(int i = 0; i < tileItems.size(); ++i) {
			ArrayList<ItemEntity> tmp = tileItems.get(i);
			if(tmp == null) continue;
			for(int j = 0; j < tmp.size(); ++j) {
				ItemEntity ie = tmp.get(j);
				if(ie.colliding(entity)) {
					System.out.println("Found a colliding entity at " + ie.x + ", " + ie.y);
					return true;
				}
			}
		}
		
//		int entityTileX = (int) entity.x >> 4;
//		int entityTileY = (int) entity.y >> 4;
//		for(int i = 0; i < entities.size(); ++i) {
//			Entity curEntity = entities.get(i);
//			int curTileX = (int) curEntity.x >> 4;
//			int curTileY = (int) curEntity.y >> 4;
//			
//			for(int j = 0; j < 3; ++j) {
//				for(int k = 0; k < 3; ++k) {
//					if(entityTileX + k * 1 == curTileX && entityTileY + j * 1 == curTileY) {
//						return true;
//					}
//				}
//			}
//		}
		
		return false;
	}
	
	public Entity getInteractable(Entity entity) {
		int entityTileX = (int) entity.x >> 4;
		int entityTileY = (int) entity.y >> 4;
		
		for(int i = 0; i < entities.size(); ++i) {
			Entity curEntity = entities.get(i);
			int curTileX = (int) curEntity.x >> 4;
			int curTileY = (int) curEntity.y >> 4;
			
			for(int j = 0; j < 3; ++j) {
				for(int k = 0; k < 3; ++k) {
					if(entityTileX + k * 1 == curTileX && entityTileY + j * 1 == curTileY) {
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
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public Entity getEntity(int x, int y, int w, int h) {
		for(int i = 0; i < players.size(); ++i) {
			PlayerOnline plr = players.get(i);
			if(plr != null) {
				if(plr.colliding(x, y, w, h)) {
					return plr;
				}
			}
		}
		
		for(int i = 0; i < entities.size(); ++i) {
			Entity e = entities.get(i);
			if(e != null) {
				if(e.colliding(x, y, w, h)) {
					return e;
				}
			}
		}
		return null;
	}
}
