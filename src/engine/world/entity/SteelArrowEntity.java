package engine.world.entity;

import engine.world.item.Item;

public class SteelArrowEntity extends ItemEntity {

	public SteelArrowEntity(float x, float y) {
		super(x, y);
		
		item = Item.steelarrow;
	}
	
	@Override
	public void interact(Entity entity) {
	}
	
	public int getItemId() {
		return item.id;
	}
}
