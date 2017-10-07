package game;

import engine.visuals.viewport.Camera;
import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;

public class Game {
	public static final int DEFAULT_CLIENT_TICKS = 32;
	public static final int SCALE_BITS = 1;
	public static final int SCALE = 2;
	public static final int WIDTH = 640 * 1;
	public static final int HEIGHT = 480 * 1;
	public static final int HALF_WIDTH = WIDTH / 2;
	public static final int HALF_HEIGHT = HEIGHT / 2;
	
	public static final String TITLE = "Experiment";
	public static final String HOSTNAME = "localhost";
	private int ticks = 0;
	
//	private Viewport viewport;
//	private Input input;
	private Player player;
	private World world;
	private String name;
	
	public Game(Player player) {
		this.player = player;
		world = new World(player);
		
		name = "moniker";
		player.setPlayerOnline(new PlayerOnline(name));
		
		System.out.println("Starting game, assigning player name \"" + name + "\"");
	}
	
	public void init() {
//		gameClient.sendJoin();
//		gameClient.listen();
	}
	
	/**
	 * Disonnect the game client from the server
	 */
	public void disconnect() {
//		gameClient.disconnect();
	}
	
	public void render(Viewport viewport) {
		//Render the world objects
		world.render(viewport);
	}
	
	/**
	 * Ticked updates, any change that happens with a specific timerate will happen here
	 */
	public void tick() {
		world.tick();
		
		ticks++;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Player getPlayer() {
		return player;
	}
}
