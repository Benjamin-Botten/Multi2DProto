package engine.world.entity.playerclass;

import engine.world.entity.action.Action;

public class Naturalist extends PlayerClass {

	public Naturalist(float power, float efficiency, float crit, float meleeResistance, float rangedResistance,
			float magicResistance) {
		super(power, efficiency, crit, meleeResistance, rangedResistance, magicResistance);
		
		id = PlayerClass.ID_CLASS_NATURALIST;
		
		spellbook.add(Action.weaponAttack);
	}

}
