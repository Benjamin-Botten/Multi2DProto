package engine.world.entity;

import engine.visuals.viewport.Viewport;

public class Entity {
	private float x, y;
	
	public Entity() {
	}
	
	public void tick() {
	}
	
	public void render(Viewport viewport) {
		viewport.render(this);
	}
}
