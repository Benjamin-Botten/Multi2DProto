package game;

import java.util.Random;

import engine.io.Input;
import engine.io.SimpleInput;
import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.entity.Player;
import game.client.GameClient;

public class Game implements Runnable {
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;
	public static final int SCALE = 2;
	public static final String TITLE = "Experiment";
	private boolean running = false;
	private int ticks = 0;
	
	private static int[] playerColors = {0xff00ff00, 0xffff0000, 0xff0000ff, 0xffffffff};
	private static String[] playerNames = {"Player", "Test", "D-e-X", "Cosmos", "Start", "End", "Other", "Black", "White"
			, "Star", "Near"};
	
	private Viewport viewport;
	private Input input;
	private Player player;
	private World world;
	private GameClient gameClient;
	
	public Game() {
		System.out.println("Parsing integer string \"0001\": " + Integer.parseInt(new String("0001".getBytes())));
		viewport = new Viewport(WIDTH, HEIGHT, SCALE, TITLE);
		input = new SimpleInput(viewport);
		player = new Player(input, assignPlayerColor(), assignPlayerName());
		world = new World(player);
		gameClient = new GameClient(player, world);
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
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(System.currentTimeMillis() - startTime >= 1000) {
				System.out.println("FPS: " + frames + ", Ticks: " + ticks);
				System.out.println("Player position (" + player.x + ", " + player.y + ")");
				frames = 0;
				ticks = 0;
				untickedTime = 0;
				startTime = System.currentTimeMillis();
				
				gameClient.sendUpdatePosition();
			} else {
				frames++;
			}
		}
	}
	
	private int x = 0, y;
	public void render() {
		viewport.clear();
		world.render(viewport);
//		x += 1;
//		viewport.renderRect(x, y);
		player.x += 0.01f;
//		player.render(viewport);
		viewport.swap();
	}
	
	public void tick() {
		ticks++;
		player.tick();
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

}
