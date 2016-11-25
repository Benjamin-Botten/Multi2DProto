package engine.world.entity;

import engine.world.World;
import engine.world.item.Item;
import engine.world.item.LifeFlask;
import game.Game;

public class LifeFlaskEntity extends ItemEntity {
	
	public LifeFlaskEntity(float x, float y) {
		super(x, y);
	
		
		w = 8 * Game.SCALE;
		h = 8 * Game.SCALE;
		item = Item.lifeflask;
	}
	
	@Override
	public void interact(Entity entity) {
	}
	
	@Override
	public void interact(World world, Entity entity) {
		entity.addItem(item);
		world.removeEntity(this);
	}
	
	public int getItemId() {
		return item.id;
	}
	
	public String toString() {
		return item.toString();
	}
}
