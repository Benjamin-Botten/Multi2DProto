package engine.world.entity;

import engine.world.World;
import engine.world.entity.Entity;
import engine.world.entity.ItemEntity;
import engine.world.item.Item;
import engine.world.item.WoodenBow;

public class WoodenBowEntity extends ItemEntity {

	public WoodenBowEntity(float x, float y) {
		super(x, y);
		
		item = new WoodenBow();
	}
	
	@Override
	public void interact(Entity entity) {
	}
	
	@Override
	public void interact(World world, Entity entity) {
		entity.addItem(item);
		
		shouldRemove = true;
//		world.removeEntity(this);
	}
	
	public int getItemId() {
		return item.getId();
	}
}
