package engine.visuals.gui;

import engine.io.Input;
import engine.util.Rect2D;
import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.tile.Tile;
import game.Game;

public class GUIMinimap extends GUIElement {
	
	public static final int SCALE_PIXEL = 4;
	
	private Game game;
	
	private final int w, h;
	private final int x, y;
	
	public GUIMinimap(Game game) {
		this.game = game;
		
		w = game.getWorld().tilesWidth;
		h = game.getWorld().tilesWidth;
		x = Game.WIDTH - (w * SCALE_PIXEL);
		y = 0;
		
		bounds = new Rect2D(x, y, w, h);
	}
	
	public void render(Viewport viewport) {
		viewport.renderMinimap(game.getWorld(), game.getPlayer(), x, y, w, h, SCALE_PIXEL);
	}
	
	public void tick(Input input) {
		
	}
}
