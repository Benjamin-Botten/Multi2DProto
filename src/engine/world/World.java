package engine.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import engine.util.LevelUtility;
import engine.visuals.FloaterText;
import engine.visuals.viewport.Viewport;
import engine.world.entity.AttackEntity;
import engine.world.entity.Entity;
import engine.world.entity.ItemEntity;
import engine.world.entity.LifeFlaskEntity;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import engine.world.entity.SteelArrowEntity;
import engine.world.entity.SpiritFlaskEntity;
import engine.world.entity.WoodenBowEntity;
import engine.world.entity.npc.NeoEntity;
import engine.world.item.SpiritFlask;
import engine.world.tile.LightTile;
import engine.world.tile.Tile;
import game.Game;

/**
 * Encapsulates the game-world at large. Might split into levels later
 * @author robot
 *
 */
public class World {
	public static final int ID_DAYMODE_DAY = 0;
	public static final int ID_DAYMODE_NIGHT = 1;
	public static final int ID_DAYMODE_BOTH = 2;
	
	public static final int MAX_ITEMS_PER_TILE = 3; //Bucket-size of items-in-tile
	private Map<Integer, ArrayList<ItemEntity>> items = new HashMap<>();
	private List<FloaterText> floaterTexts = new ArrayList<>();
	
	//Dimensions
	public static int w = 512 * 8, h = 512 * 8;
	public static int tilesWidth = w / 8;
	public static int tilesHeight = h / 8;
	
	//Tile data
	private byte[] collisionMap; //might be used as an auxiliary to make
	private int[] tiles;
	private int[] bgtiles; //background tiles
	private float[] tilelight;
	
	//Entities
	private List<Entity> entities = new ArrayList<>();
	private List<AttackEntity> attackEntities = new ArrayList<>();
	private List<Player> players = new ArrayList<>();
	private Player player;

	//Levels
	private Level currentLevel;
	
	//The current position in the day-night cycle in the range [0, 1]
	private int dayMode = ID_DAYMODE_BOTH;
	private static final float MAX_DAYLIGHT_PER_TICK = 2f / (32 * 60);
	private float dayPhase = 0.79f;
	private float daylightIncrement = MAX_DAYLIGHT_PER_TICK;
	private int tickTime;
	private int ticks;
	private int dayPhaseTicks;
	private final int DAY_NIGHT_CYCLE_TICKS = Game.DEFAULT_CLIENT_TICKS * 60;
	
	public World(Level level) {
		
		//Load level
		loadLevel(level);
		
		//Initialize lighting for the level
		initLightMap();
	}

	/**
	 * Constructs the world with a player already known
	 * @param player
	 */
	public World(Player player) {
		this.player = player;
		
		init();
	}
	
	/**
	 * Initialize the game world by loading in level, initializing lighting for the environment, etc...
	 */
	private void init() {
		fetchLevels();
		
		loadLevel(new Level("test2"));
		
		/** Initialize the light-map for the world here. TODO: Implement occlusion you go-getter */
		initLightMap();
	}
	
	private void fetchLevels() {
		LevelUtility.fetchAll(this);
	}

	public void loadLevel(Level level) {
		if(level == null) throw new IllegalArgumentException("Attempted setting null level");
		
		setCurrentLevel(level);
		
		LevelUtility.loadLevel(this);
		
		System.out.println("Dimension of map in tiles: " + tilesWidth + ", " + tilesHeight);
		System.out.println("Dimension of map in pixels: " + w + ", " + h);
		System.out.println("Tiles: ");
		for(int i = 0; i < tiles.length; ++i) {
			System.out.print(tiles[i]);
		}
		System.out.println();
		
		initLightMap();
		
		for(int i = 0; i < tilelight.length; ++i) {
			System.out.print(tilelight[i]);
		}
		System.out.println();
	}
	
	public void saveLevel() {
		LevelUtility.saveLevel(this);
	}

	private void initLightMap() {
		tilelight = new float[tilesWidth * tilesHeight];
		for(int y = 0; y < tilesHeight; ++y) {
			for(int x = 0; x < tilesWidth; ++x) {
				
				Tile tile = Tile.tiles[tiles[x + y * tilesWidth]];
				if(tile instanceof LightTile) {
					if(tilelight[x + y * tilesWidth] >= 1) continue;
					
					int lightRadius = ((LightTile) (Tile.tiles[tiles[x + y * tilesWidth]])).getRadius();
					int halfRadius = lightRadius / 2;
					for(int b = 0; b < lightRadius; ++b) {
						if(y + b - halfRadius < 0 || y + b - halfRadius >= tilesHeight) continue;
						
						for(int a = 0; a < lightRadius; ++a) {
							if(x + a - halfRadius < 0 || x + a - halfRadius >= tilesWidth) continue;
							
							int dist = ((halfRadius - a) * (halfRadius - a) + (halfRadius - b) * (halfRadius - b));
							if(dist <= lightRadius - halfRadius + 2) {
								tilelight[(x + a - halfRadius) + (y + b - halfRadius) * tilesWidth] += (1.f / (1.f + ((halfRadius - a) * (halfRadius - a) + (halfRadius - b) * (halfRadius - b))));
							}
						}
					}
				} else {
					if(tilelight[x + y * tilesWidth] <= 0)
						tilelight[x + y * tilesWidth] = 0;
				}
			}
		}
	}
	
	public void tick() {
		//Update entities and sort out the removables
		ArrayList<Entity> removables = new ArrayList<>();
		for(int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			entity.tick();
			entity.handle(this);
			if(entity.canRemove()) {
				removables.add(entities.get(i));
				System.out.println("Number of entities left: " + entities.size());
			}
		}
		//Remove all dead entities
		for(int i = 0; i < removables.size(); ++i) {
			entities.remove(removables.get(i));
		}
		
		//Update attack entities
		for(int i = 0; i < attackEntities.size(); ++i) {
			attackEntities.get(i).tick();
			attackEntities.get(i).handle(this);
		}
		
		//Update floater-texts
		for(int i = 0; i < floaterTexts.size(); ++i) {
			FloaterText floaterText = floaterTexts.get(i);
			floaterText.tick();
			if(floaterText.removable()) {
				floaterTexts.remove(floaterText);
			}
		}
		
		//Update the player
		player.tick();
		player.handle(this);
		
		//TODO: Refactor this
		ticks++;
		if(dayMode == ID_DAYMODE_BOTH) {
			if(ticks % 2 == 0) {
				tickTime++;
				if(dayPhase > 0.8f)
					daylightIncrement = -daylightIncrement;
				else if(dayPhase <= 0)
					daylightIncrement = -daylightIncrement;
			
				dayPhase += daylightIncrement;
			}
		}
		else if(dayMode == ID_DAYMODE_DAY) {
			dayPhase = 0.8f;
		}
		else if(dayMode == ID_DAYMODE_NIGHT) {
			dayPhase = 0.1f;
		}
	}
	
	/**
	 * @note Render all entities, the world, etc, first
	 * @param viewport
	 */
	public void render(Viewport viewport) {
		synchronized(viewport) {
			viewport.render(this);
		}
		
		synchronized(entities) {
			for(Entity entity : entities) {
				entity.render(viewport);
			}
		}
		
		synchronized(players) {
			for(Player player : players) {
				player.render(viewport);
			}
		}
		
		synchronized(player) {
			player.render(viewport);
		}
		
		for(AttackEntity attackEntity : attackEntities) {
			attackEntity.render(viewport);
		}
		
		for(FloaterText floaterText : floaterTexts) {
			floaterText.render(viewport);
		}
	}
	
	public void addEntity(Entity entity) {
		if(entity == null) {
			return;
		}
		if(entity instanceof Player) {
			if(player == null) {
				player = (Player) entity;
				return;
			}
			return;
		}
		if(entity instanceof ItemEntity) {
			ItemEntity item = (ItemEntity) entity;
			setTileItem((int) entity.x, (int) entity.y, item);
			setTileItem((int) entity.x + (entity.w >> 1), (int) entity.y, item);
			setTileItem((int) entity.x, (int) entity.y + (entity.h >> 1), item);
			setTileItem((int) entity.x + (entity.w >> 1), (int) entity.y + (entity.h >> 1), item);
			
			entities.add(entity);
			return;
		}
		entities.add(entity);
	}
	
	public void addAttackEntity(AttackEntity attackEntity) {
		if(attackEntity == null) throw new IllegalArgumentException("Attempted to add null AttackEntity to world");
		attackEntities.add(attackEntity);
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
	
	public void removeTileItem(ItemEntity item) {
		List<ItemEntity> tileItems = getTileItems((int) item.x, (int)item.y);
		tileItems.remove(item);
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public void updateEntity(Entity entity) {
		if(entity == null) {
			return;
		}
		if(entity instanceof Player) {
			Player player = (Player) entity;
			for(int i = 0; i < players.size(); ++i) {
				if(player.getUsername().equalsIgnoreCase(players.get(i).getUsername())) {
					players.set(i, player);
					return;
				}
			}
			addEntity(player);
		}
	}
	
	public boolean removePlayerByName(String username) {
		for(int i = 0; i < players.size(); ++i) {
			if(players.get(i).getUsername().equalsIgnoreCase(username)) {
				players.remove(i);
				System.out.println("Number of entities & players in the world after removing player by name: " + entities.size() + ", " + players.size());
				return true;
			}
		}
		return false;
	}
	
	public Player getPlayerByName(String name) {
		
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
	
	public float[] getTileLight() {
		return tilelight;
	}
	
	public float getTileLight(int x, int y) {
		//Normalize coordinates to tile space
		x >>= 4;
		y >>= 4;
			
		if(x < 0 || x >= tilesWidth || y < 0 || y >= tilesHeight) return 0;//throw new IllegalArgumentException("Attempting to get tile-light outside of world-tile space");
		
		return tilelight[x + y * tilesWidth];
	}
	
	public Tile getTile(int x, int y) {
		//Normalize coordinates to tile space
		x >>= 4;
		y >>= 4;
		
		if(x < 0 || y < 0 || x >= tilesWidth || y >= tilesHeight) return Tile.air; //throw new IllegalArgumentException("Trying to find tile out of bounds of the world size @(" + x + ", " + y + ")");
		
		return Tile.tiles[tiles[x + y * tilesWidth]];
	}
	
	public void setTile(int id, int x, int y) {
		if(x < 0 || y < 0 || x >= w || y >= h) throw new IllegalArgumentException("Trying to set tile out of bounds of the world size");
		tiles[x + y * w] = id;
	}
	
	/**
	 * TODO: Clean up this mess of a stub
	 * @param entity
	 * @return true if entity is colliding, otherwise false
	 */
	public boolean isColliding(Entity entity) {
		if(entity.x < 0 || entity.x + entity.w >= w || entity.y < 0 || entity.y + entity.h >= h) {
//			System.out.println("Playing colliding with world bounds at > " + (entity.x + entity.w) + ", " + entity.y);
			return true;
		}
		
		//See if entity is colliding with a solid tile
		//Clean this code up
		//All entities may not be the same dimension, fix those cases
		int startX = (int) entity.x;
		int middleX = startX + (entity.w >> 1);
		int endX = startX + entity.w;
		int startY = (int) entity.y;
		int middleY = startY + (entity.h >> 1);
		int endY = startY + entity.h;
		if(		
				getTile((int) entity.x, (int) entity.y).solid() || 
				getTile((int) entity.x + (entity.w >> 1), (int) entity.y).solid() ||
				getTile((int) entity.x, (int) entity.y + (entity.h >> 1)).solid() ||
				getTile((int) entity.x + (entity.w >> 1), (int) entity.y + (entity.h >> 1)).solid() ||
				getTile((int) entity.x + (entity.w >> 1), (int) entity.y + entity.h).solid() ||
				getTile((int) entity.x + entity.w, (int) entity.y + (entity.h >> 1)).solid() ||
				getTile((int) entity.x + entity.w, (int) entity.y).solid() ||
				getTile((int) entity.x, (int) entity.y + entity.h).solid() ||
				getTile((int) entity.x + entity.w, (int) entity.y + entity.h).solid()
			) 
		{
			return true;
		}
		
		
//		List<ArrayList<ItemEntity>> tileItems = new ArrayList<>();
//		tileItems.add(getTileItems(startX, startY));
//		tileItems.add(getTileItems(middleX, startY));
//		tileItems.add(getTileItems(endX, startY));
//		
//		tileItems.add(getTileItems(startX, middleY));
//		tileItems.add(getTileItems(middleX, middleY));
//		tileItems.add(getTileItems(endX, middleY));
//		
//		tileItems.add(getTileItems(startX, endY));
//		tileItems.add(getTileItems(middleX, endY));
//		tileItems.add(getTileItems(endX, endY));
//		
//		for(int i = 0; i < tileItems.size(); ++i) {
//			ArrayList<ItemEntity> tmp = tileItems.get(i);
//			if(tmp == null) continue;
//			for(int j = 0; j < tmp.size(); ++j) {
//				ItemEntity ie = tmp.get(j);
//				if(ie.colliding(entity)) {
//					System.out.println("Found a colliding entity at " + ie.x + ", " + ie.y);
//					return true;
//				}
//			}
//		}
		
		for(int i = 0; i < entities.size(); ++i) {
			Entity e = entities.get(i);
			if(entity.colliding(e) && e.solid()) {
				return true;
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
	
	public void removeAttackEntity(AttackEntity attackEntity) {
		attackEntities.remove(attackEntity);
	}
	
	/**
	 * Get entity by collision
	 * @param entity
	 * @return entity the input-shape is colliding with
	 */
	public Entity getEntity(int x, int y, int w, int h) {
		for(int i = 0; i < players.size(); ++i) {
			Player plr = players.get(i);
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

	/**
	 * This can be optimized because ids are pooled, based on the value you either search entities or players, saving a small overhead
	 * An even better optimization later is to table every entity by its netId for "online entities" list
	 * @param netId
	 * @return Entity with given network ID
	 */
	public Entity getEntityByNetId(int netId) {
		if(player.getNetId() == netId) return player;
		
		for(int i = 0; i < players.size(); ++i) {
			Player plr = players.get(i);
			if(plr != null) {
				if(plr.getNetId() == netId) {
					return plr;
				}
			}
		}
		
		for(int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			if(entity != null) {
				if(entity.getNetId() == netId) {
					return entity;
				}
			}
		}
		return null;
	}
	
	public float getDayPhase() {
		return dayPhase;
	}
	
	public int getDayMode() {
		return dayMode;
	}
	
	public AttackEntity getAttackEntity(int netId) {
		return attackEntities.get(netId);
	}

	public void addFloaterText(FloaterText floaterText) {
		if(floaterText == null) throw new IllegalArgumentException("Attempting to add null FloaterText to world");
		floaterTexts.add(floaterText);
	}
	
	public Level getCurrentLevel() {
		return currentLevel;
	}
	
	public void setCurrentLevel(Level level) {
		if(level == null) {
			return;
		}
		
		currentLevel = level;
	}
	
	
	public void setTiles(int tiles[]) {
		if(tiles == null) {
			throw new IllegalArgumentException("Attempted setting tiles of world to null");
		}
		this.tiles = tiles;
	}
	
	/**
	 * Sets the width of the world in tilespace (scaled to game scale), also the amount of pixels along x
	 * @param tilesWidth (non-transformed)
	 */
	public void setWidth(int tilesWidth) {
		this.tilesWidth = tilesWidth;
		
		w = tilesWidth * Tile.w * Game.SCALE;
	}
	
	/**
	 * Sets the height of the world in tilespace (scaled to game scale), also the amount of pixels along y
	 * @param tilesHeight (non-transformed)
	 */
	public void setHeight(int tilesHeight) {
		this.tilesHeight = tilesHeight;
		
		h = tilesHeight * Tile.h * Game.SCALE;
	}

	public int getSizeEntities() {
		return entities.size();
	}

	public List<Entity> getEntities() {
		List<Entity> ret = new ArrayList<>(entities);
		return ret;
	}
}
