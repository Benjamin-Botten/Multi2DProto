package engine.world.item;

import engine.world.entity.Entity;

public class LifeFlask extends ConsumableItem {

	private final int lifeAddition = 10; //Percentage
	
	public LifeFlask() {
		id = ID_ITEM_FLASK_LIFE;
		name = "Life Flask";
	}
	
	@Override
	public void use(Entity entity) {
	}
	
	@Override
	public void useOn(Entity entity) {
	}
}
