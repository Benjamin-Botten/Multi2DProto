package game.gamestate;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import engine.io.Input;
import engine.visuals.gui.GUIElement;
import engine.visuals.gui.GUIInventory;
import engine.visuals.gui.GUIMinimap;
import engine.visuals.viewport.Viewport;
import game.Game;

public class Play extends GameState {
	
//	private List<GUIElement> gui = new ArrayList<GUIElement>();
	
	private Game game;
	private GUIInventory guiInventory;
	private GUIMinimap guiMinimap;
	
	public Play(GameState previousState, Game game) {
		super(previousState);
		
		this.game = game;
		guiInventory = new GUIInventory(game.getPlayer());
		guiMinimap = new GUIMinimap(game);
	}
	
	public void render(Viewport viewport) {
		//Clear screen
		viewport.clear();
		
		//Render shit
		viewport.setCamera(game.getPlayer());
		game.render(viewport);
		guiInventory.render(viewport);
		guiMinimap.render(viewport);
		
//		viewport.render("I WANT THAT SZECHUAN SAUCE MORTY", 320, 240, 0xffffff00, 2);
		
		//Swap V-buffers
		viewport.swap();
	}
	
	public void tick(Input input) {
		//TODO: Add focusable elements so you don't accidentally do shit underneath GUI
		guiInventory.tick(input);
		guiMinimap.tick(input);
		
		if(!guiInventory.hasFocus()) {
			game.tick();
		}
		
		if(input.keys[KeyEvent.VK_ESCAPE]) {
			game.getWorld().saveLevel();
			setNext(new Lobby(this));
		}
	}
}
