package engine.world.entity;

import engine.world.World;
import engine.world.item.Item;
import engine.world.item.SteelArrow;

public class SteelArrowEntity extends ItemEntity {

	public SteelArrowEntity(float x, float y) {
		super(x, y);
		
		item = new SteelArrow();
	}
	
	@Override
	public void interact(Entity entity) {
	}
	
	@Override
	public void interact(World world, Entity entity) {
		entity.addItem(item);
		
		shouldRemove = true;
	}
	
	public int getItemId() {
		return item.getId();
	}
}
