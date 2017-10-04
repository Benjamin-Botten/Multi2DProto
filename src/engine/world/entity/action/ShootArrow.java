package engine.world.entity.action;

import engine.world.entity.Entity;

public class ShootArrow extends Attack {

	public ShootArrow(int id, String name, String description, long castTime, long cooldownTime) {
		super(id, name, description, castTime, cooldownTime);
	}
	
	public String toString() {
		return name;
	}
}
