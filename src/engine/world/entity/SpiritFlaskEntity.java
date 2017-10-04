package engine.world.entity;

import engine.world.World;
import engine.world.item.Item;
import engine.world.item.SpiritFlask;
import game.Game;

public class SpiritFlaskEntity extends ItemEntity {

	public SpiritFlaskEntity(float x, float y) {
		super(x, y);
		
		item = new SpiritFlask();
		
		pickupRadius = 1024;
	}

	public void interact(World world, Entity entity) {
		if (isWithinRadius(entity, pickupRadius)) {
			entity.addItem(item);
			
			shouldRemove = true;
//			world.removeEntity(this);
		}
	}
	
	@Override
	public void interact(Entity entity) {
		entity.addItem(item);
	}
	
	public int getItemId() {
		return item.getId();
	}
	
	public String toString() {
		return item.toString() + " : dimensions " + w + ", " + h;
	}
}
