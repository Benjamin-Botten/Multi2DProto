package engine.world.entity;

import engine.world.item.Item;

public class SustainFlaskEntity extends ItemEntity {

	public SustainFlaskEntity(float x, float y) {
		super(x, y);
		
		item = Item.sustainflask;
	}

	@Override
	public void interact(Entity entity) {
	}
	
	public int getItemId() {
		return item.id;
	}
}
