package engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.visuals.viewport.Viewport;
import engine.world.entity.Entity;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import engine.world.tile.Tile;
import game.Game;

public class World {
	
	private byte[] collisionMap; //this is a byte array that holds either 0 or 1, it is a map of collidable tiles in this world
	private int[] tiles;
	private List<Entity> entities = new ArrayList<>();
	private List<PlayerOnline> players = new ArrayList<>();
	private Map<Integer, Entity> entityLookup = new HashMap<>();
	
	private Player player;
	
	public World(Player player) {
		this.player = player;
		
		tiles = new int[(Game.WIDTH / 8) * (Game.HEIGHT / 8)];
		for(int i = 0; i < tiles.length; ++i) {
			tiles[i] = Tile.grass.id;
		}
	}
	
	public void tick() {
//		for(Entity entity : entities) {
//			entity.tick();
//		}
//		for(PlayerOnline playerOnline : players) {
//			playerOnline.tick();
//		}
		
		player.tick();
	}
	
	/**
	 * @note Render all entities, the world, etc, first
	 * @param viewport
	 */
	public void render(Viewport viewport) {
		for(int y = 0; y < Game.HEIGHT; y += 8) {
			for(int x = 0; x < Game.WIDTH; x += 8) {
				viewport.render(Tile.grass, x, y);
			}
		}
		
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
}
