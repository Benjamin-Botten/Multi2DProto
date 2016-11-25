package engine.world.entity.attack;

import engine.world.entity.Entity;

public class Attack {
	
	
	public static final Attack[] attacks = new Attack[64];
	
	public static final ShootArrow shootarrow = new ShootArrow(0);
	
	public final int id;
	
	public Attack(int id) {
		if(attacks[id] != null) throw new IllegalArgumentException("Attempted creating tile with id \"" + id + "\" that already exists");
	
		attacks[(this.id = id)] = this;
	}
	
	/**
	 * Calculates the damage output a given attacker will produce on an attackee using a type of attack
	 * @param attacker
	 * @param attackee
	 * @return
	 */
	public int getDamageOutput(Entity attacker, Entity attackee) {
		return 0;
	}
}
