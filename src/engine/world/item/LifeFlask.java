package engine.world.item;

import engine.world.entity.Entity;

public class LifeFlask extends Item {

	private final int lifeAddition = 10;
	
	public LifeFlask(int id, String name, String description) {
		super(id, name, description);
	}

	@Override
	public void use(Entity entity) {
	}
	
	@Override
	public void useOn(Entity entity) {
		
	}
}
