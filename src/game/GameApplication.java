package game;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import engine.io.Input;
import engine.io.SimpleInput;
import engine.visuals.viewport.Viewport;
import game.Game;
import game.gamestate.GameSettings;
import game.gamestate.GameState;
import game.gamestate.Lobby;

public class GameApplication implements Runnable {
	
	public static final GameSettings gameSettings = new GameSettings();
	
	private GameState gameState;
	private Viewport viewport;
	private Input input;
	
	private JFrame frame = new JFrame();
	
	private boolean running = false;
	
	private int ticks;
	
	public void init() {
		viewport = new Viewport
				(
				gameSettings.getViewportWidth(), 
				gameSettings.getViewportHeight(),
				gameSettings.getViewportScale(),
				GameSettings.TITLE_GAME, 
				gameSettings.getViewportCamera(),
				frame
				);
		
		
		viewport.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				
			}
		});
		
		input = new SimpleInput(viewport);
		
		gameState = new Lobby(null);
	}
	
	public void start() {
		new Thread(this).start();
	}
	
	public void render() {
		gameState.render(viewport);
	}

	public void tick() {
		gameState.tick(input);
		
		ticks++;
		
		
		gameState = gameState.update();
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
			//Time between ticks (delta value)
			long dtimeTicks = System.currentTimeMillis() - startTimeTicks;
			
			//If the difference in time between last two ticks is larger than constant tickrate, 
			//add the missed ticks to untickedtime and synch
			//TODO: Tick the unprocessed ticks
			if(dtimeTicks >= msPerTick) {
				//Game logic update
				tick();
				
//				untickedTime += (dtimeTicks - msPerTick) / 1000.d;
//				while(untickedTime > 0) {
//					tick();
//					untickedTime -= msPerTick / 1000.d;
//				}
//				System.out.println("Unticked time: " + untickedTime);
				startTimeTicks = System.currentTimeMillis();
			}
			
			//Draw everything
			render();
			
			//Sleep the thread a little so you don't freak out the CPU
//			try {
//				Thread.sleep(2);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			//Do second-based operations here
			if(System.currentTimeMillis() - startTime >= 1000) {
				//System.out.println("FPS: " + frames + ", Ticks: " + ticks);
				//System.out.println("Player position (" + player.x + ", " + player.y + ")");
				viewport.setTitle("FPS: " + frames + ", Ticks: " + ticks);
				frames = 0;
				ticks = 0;
				untickedTime = 0;
				startTime = System.currentTimeMillis();
			} else {
				frames++;
			}
		}
		
		//Disconnect the player from the world when the game stops running
//		disconnect();
	}
	
	public static void main(String[] args) {
		GameApplication gameApplication = new GameApplication();
		gameApplication.start();
	}
}
