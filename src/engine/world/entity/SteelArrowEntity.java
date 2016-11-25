package engine.world.entity;

import engine.world.World;
import engine.world.item.Item;

public class SteelArrowEntity extends ItemEntity {

	public SteelArrowEntity(float x, float y) {
		super(x, y);
		
		item = Item.steelarrow;
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
