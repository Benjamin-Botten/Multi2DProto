package game.gamestate;

import engine.io.Input;
import engine.visuals.gui.GUIButton;
import engine.visuals.viewport.Viewport;
import engine.world.entity.Player;
import game.GameApplication;
import game.Game;

public class Lobby extends GameState {

	GUIButton buttonPlay = new GUIButton("play", GameApplication.gameSettings.getViewportWidth() / 2, GameApplication.gameSettings.getViewportHeight() / 2, 32, 8, 0xff00ff00, 0xff333333);
	GUIButton buttonLogout = new GUIButton("logout", GameApplication.gameSettings.getViewportWidth() - 48, GameApplication.gameSettings.getViewportHeight() - 16, "logout".length() * 8, 8, 0xffff0000, 0xff333333);
	GUIButton buttonExit = new GUIButton("exit", 32, GameApplication.gameSettings.getViewportHeight() - 16, 32, 8, 0xffffffff, 0xff333333);
	
	public Lobby(GameState previousState) {
		super(previousState);
	}
	
	public void render(Viewport viewport) {
		viewport.setCamera(0, 0);
		viewport.clear();
		
		viewport.render("lobby", 320, 8, 0xffaf6f00);
		buttonPlay.render(viewport);
		buttonLogout.render(viewport);
		buttonExit.render(viewport);
		viewport.swap();
	}
	
	public void tick(Input input) {
		buttonPlay.tick(input);
		if(buttonPlay.pressed()) {
			setNext(new Play(this, new Game(new Player(input))));
		}
	}
}
