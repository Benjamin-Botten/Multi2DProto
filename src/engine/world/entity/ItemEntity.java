package engine.world.entity;

import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.item.Item;
import game.Game;

public class ItemEntity extends Entity {
	
	public int w = 8 * Game.SCALE, h = 8 * Game.SCALE;
	protected Item item;
	
	public ItemEntity(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void interact(Entity entity) {
	}
	
	public void interact(World world, Entity entity) {
	}
	
	public int getItemId() {
		return item.id;
	}
	
	public Item getItem() {
		return item;
	}
	
	public void tick() {
		super.tick();
	}
	
	public void render(Viewport viewport) {
		viewport.renderItemEntity(this, ticks, 0.03125f);
		//Debugging purposes
//		viewport.renderBounds((int) x, (int) y, w, h);
	}
}
