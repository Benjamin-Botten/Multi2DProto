package engine.world.entity.playerclass;

import engine.world.entity.action.Action;

public class Wizard extends PlayerClass {

	public Wizard(float power, float efficiency, float crit, float meleeResistance, float rangedResistance,
			float magicResistance) {
		super(power, efficiency, crit, meleeResistance, rangedResistance, magicResistance);
		
		id = PlayerClass.ID_CLASS_WIZARD;
		
		spellbook.add(Action.weaponAttack);
	}

}
