package engine.world.entity;

import engine.world.World;
import engine.world.item.Item;
import engine.world.item.LifeFlask;
import game.Game;

public class LifeFlaskEntity extends ItemEntity {

	public LifeFlaskEntity(float x, float y) {
		super(x, y);
		
		item = new LifeFlask();
		pickupRadius = 1024;
	}
	
	@Override
	public void tick() {
		if(dead()) {
			shouldRemove = true;
		}
	}

	@Override
	public void interact(Entity entity) {
	}

	@Override
	public void interact(World world, Entity entity) {
		if (isWithinRadius(entity, pickupRadius)) {
			entity.addItem(item);

			world.removeEntity(this);
		}
	}

	public int getItemId() {
		return item.getId();
	}

	public String toString() {
		return item.toString();
	}
}
