package engine.world.entity;

import engine.world.World;
import engine.world.entity.Entity;
import engine.world.entity.ItemEntity;
import engine.world.item.Item;

public class WoodenBowEntity extends ItemEntity {

	public WoodenBowEntity(float x, float y) {
		super(x, y);
		
		item = Item.woodenbow;
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
}
