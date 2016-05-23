package game;

import engine.io.Input;
import engine.io.SimpleInput;
import engine.visuals.viewport.Viewport;

public class Game implements Runnable {
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;
	public static final int SCALE = 2;
	public static final String TITLE = "Experiment";
	private boolean running = false;
	
	private Viewport viewport;
	private Input input;
	
	public Game() {
		viewport = new Viewport(WIDTH, HEIGHT, SCALE, TITLE);
		input = new SimpleInput(viewport);
	}
	
	public void init() {
	}
	
	public void start() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		init();
		running = true;
		
		while(running) {
			tick(); //Game logic update
			render(); //Draw the visuals
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void render() {
		viewport.clear();
		viewport.renderRect();
		viewport.swap();
	}
	
	public void tick() {
		
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

}
