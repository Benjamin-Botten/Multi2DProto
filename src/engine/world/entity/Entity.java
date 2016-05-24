package engine.world.entity;

import engine.visuals.Sprite;
import engine.visuals.viewport.Viewport;

public class Entity {
	public float x, y;
	public int color;
	protected Sprite sprite;
	
	public Entity() {
	}
	
	public void tick() {
	}
	
	public void render(Viewport viewport) {
		viewport.render(this);
	}
}
