package engine.world.entity.action;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author robot
 *
 *	Class encapsulation for entity-spellbooks
 */
public class Spellbook {
	private List<Action> spells = new ArrayList<Action>();
	
	public Spellbook add(Action action) {
		spells.add(action);
		return this;
	}
	
	public List<Action> getSpells() {
		return spells;
	}
	
	public Action getSpell(int index) {
		return spells.get(index);
	}
}
