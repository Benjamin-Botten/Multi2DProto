package engine.world.entity;

import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.item.Item;
import game.Game;

public class ItemEntity extends Entity {
	
	protected Item item;
	protected int pickupRadius;
	
	public ItemEntity(float x, float y) {
		if(x < 0) {
			this.x = 0;
		}else {
			this.x = x;
		}
		
		if(y < 0) {
			this.y = 0;
		} else {
			this.y = y;
		}
		
		w = 8 * Game.SCALE;
		h = 8 * Game.SCALE;
	}
	
	public void interact(Entity entity) {
	}
	
	public void interact(World world, Entity entity) {
	}
	
	public int getItemId() {
		return item.getId();
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
		viewport.renderBounds(this);
	}
}
