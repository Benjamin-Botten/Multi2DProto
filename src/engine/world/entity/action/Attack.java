package engine.world.entity.action;

import engine.world.entity.Entity;

public class Attack extends Action {

	public Attack(int id, String name, String description, long castTime, long cooldownTime) {
		super(id, name, description, castTime, cooldownTime);
	}
	
}
