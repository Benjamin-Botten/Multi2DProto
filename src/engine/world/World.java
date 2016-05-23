package engine.world;

import java.util.List;

import engine.visuals.viewport.Viewport;
import engine.world.entity.Entity;

public class World {
	
	private List<Entity> entities;
	
	public World() {
		
	}
	
	public void tick() {
	}
	
	public void render(Viewport viewport) {
		for(Entity entity : entities) {
			entity.render(viewport);
		}
	}
}
