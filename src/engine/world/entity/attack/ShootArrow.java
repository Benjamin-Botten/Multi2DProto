package engine.world.entity.attack;

import engine.world.entity.Entity;

public class ShootArrow extends Attack {

	public ShootArrow(int id) {
		super(id);
	}
	
	public int getDamageOutput(Entity attacker, Entity attackee) {
		return 100;
	}
}
