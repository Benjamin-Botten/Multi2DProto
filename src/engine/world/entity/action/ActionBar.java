package engine.world.entity.action;

import java.util.ArrayList;
import java.util.List;

public class ActionBar {
	
	private List<ActionButton> buttons = new ArrayList<>();
	
	/**
	 * Populate the actionbar buttons using a given spellbook
	 * @param spellbook
	 */
	public ActionBar(Spellbook spellbook) {
		List<Action> spells = spellbook.getSpells();
		for(int i = 0; i < spells.size(); ++i) {
			buttons.add(new ActionButton(i, spellbook.getSpell(i).getId(), 0));	
		}
	}
	
	/**
	 * Initializes the actionbuttons from given settings file
	 * @param filenameKeybindings
	 */
	public void init(String filenameSettings) {
		
	}
}
