package game.gamestate;

import engine.visuals.viewport.Camera;

public class GameSettings {

	public static final String TITLE_GAME = "Game of the Century";
	
	/** Viewport Settings */
	private final int viewportWidth;
	private final int viewportHeight;
	private final int viewportScale;
	private final Camera viewportCamera;
	
	private static final String filenameSettings = "/resrc/game.ini";
	
	public GameSettings() {
		viewportWidth = 640;
		viewportHeight = 480;
		viewportScale = 2;
		viewportCamera = new Camera();
	}
	
	public void save() {
	}
	
	public void load() {
	}
	
	
	public int getViewportWidth() {
		return viewportWidth;
	}
	
	public int getViewportHeight() {
		return viewportHeight;
	}
	
	public int getViewportScale() {
		return viewportScale;
	}
	
	public Camera getViewportCamera() {
		return viewportCamera;
	}
}
