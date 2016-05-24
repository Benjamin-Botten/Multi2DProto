package engine.world;

import java.util.ArrayList;
import java.util.List;

import engine.visuals.viewport.Viewport;
import engine.world.entity.Entity;
import engine.world.entity.Player;

public class World {
	
	private List<Player> players = new ArrayList<>();
	
	private Player player;
	
	public World(Player player) {
		this.player = player;
		addPlayer(player);
	}
	
	public void tick() {
	}
	
	public void render(Viewport viewport) {
		for(Player player : players) {
			player.render(viewport);
		}
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public void updatePlayer(Player player) {
		for(int i = 0; i < players.size(); ++i) {
			//If the player exists, just update positions
			if(player.name.equalsIgnoreCase(players.get(i).name)) {
				players.get(i).x = player.x;
				players.get(i).y = player.y;
				return;
			}
		}
		//If player doesn't exist, add him
		addPlayer(player);
	}
}
