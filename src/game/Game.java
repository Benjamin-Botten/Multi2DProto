package game;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import com.sun.glass.events.KeyEvent;

import engine.io.Input;
import engine.io.SimpleInput;
import engine.visuals.viewport.Camera;
import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import game.client.GameClient;

public class Game implements Runnable {
	public static final int DEFAULT_CLIENT_TICKS = 32;
	public static final int SCALE_BITS = 1;
	public static final int SCALE = 2;
	public static final int WIDTH = 640 * 1;
	public static final int HEIGHT = 480 * 1;
	public static final String TITLE = "Experiment";
	public static final String HOSTNAME = "localhost";
	private boolean running = false;
	private int ticks = 0;
	
	private static int[] playerColors = {0xff00ff00, 0xffff0000, 0xff0000ff, 0xffffffff};
	private static String[] playerNames = {"Player", "Test", "D-e-X", "Cosmos", "Start", "End", "Other", "Black", "White"
			, "Star", "Near", "Google", "Test2", "Test3", "Test4", "Exp", "Logic"};
	
	private Viewport viewport;
	private Input input;
	private Player player;
	private Camera camera;
	private World world;
	private GameClient gameClient;
	private String name;
	
	public Game() {
		camera = new Camera();
		viewport = new Viewport(WIDTH, HEIGHT, SCALE, TITLE, camera);
		input = new SimpleInput(viewport);
		player = new Player(input, camera);
		world = new World(player);
		
		name = assignPlayerName();
		System.out.println("Starting game, assigning player name \"" + name + "\"");
		viewport.setTitle(name);
		viewport.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				System.out.println("Exited client, disconnecting player");
				disconnect();
			}
		});
		gameClient = new GameClient(HOSTNAME, new PlayerOnline(name), world);
	}
	
	public static int assignPlayerColor() {
		Random random = new Random();
		int randomIndex = random.nextInt(playerColors.length - 1);
		return playerColors[randomIndex];
	}
	
	public static String assignPlayerName() {
		Random random = new Random();
		int randomIndex = random.nextInt(playerNames.length - 1);
		return playerNames[randomIndex];
	}
	
	public void init() {
		gameClient.sendJoin();
		gameClient.listen();
	}
	
	public void start() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		init();
		running = true;
		
		long startTime = System.currentTimeMillis();
		long startTimeTicks = System.currentTimeMillis();
		int frames = 0;
		final double msPerTick = 1000.d / 32;
		double untickedTime = 0;
		while(running) {
			long dtimeTicks = System.currentTimeMillis() - startTimeTicks;
			if(dtimeTicks >= msPerTick) {
				untickedTime += (dtimeTicks - msPerTick) / 1000.d;
//				while(untickedTime > 0) {
//					tick();
//					untickedTime -= msPerTick / 1000.d;
//				}
				//System.out.println("Unticked time: " + untickedTime);
				startTimeTicks = System.currentTimeMillis();
				tick(); //Game logic update
			}
			
			render(); //Draw the visuals
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(System.currentTimeMillis() - startTime >= 1000) {
				//System.out.println("FPS: " + frames + ", Ticks: " + ticks);
				//System.out.println("Player position (" + player.x + ", " + player.y + ")");
				viewport.setTitle("Player: \"" + name + "\", FPS: " + frames + ", Ticks: " + ticks);
				frames = 0;
				ticks = 0;
				untickedTime = 0;
				startTime = System.currentTimeMillis();
			} else {
				frames++;
			}
		}
		
		disconnect();
	}
	
	public void disconnect() {
		gameClient.disconnect();
	}
	
	public void render() {
		viewport.clear();
		world.render(viewport);
//		viewport.render("TEST", 0, 0);
		viewport.swap();
	}
	
	
	public static final int HALF_WIDTH = WIDTH / 2;
	public static final int HALF_HEIGHT = HEIGHT / 2;
	public void tick() {
		world.tick();
		gameClient.updatePlayer(player);
		gameClient.sendUpdatePlayer();
		
		if(input.keys[KeyEvent.VK_ESCAPE]) {
			quit();
		}
		
		ticks++;
	}
	
	private void quit() {
		running = false;
	}

	public Viewport getViewport() {
		return viewport;
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
}
