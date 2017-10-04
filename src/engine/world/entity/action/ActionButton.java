package engine.world.entity.action;

public class ActionButton {
	private int key; //key code on the keyboard
	private long timeLastUsed;
	private int actionId;
	
	public ActionButton(int keybinding, int actionId, long timeLastUsed) {
		this.key = keybinding;
		this.actionId = actionId;
		this.timeLastUsed = timeLastUsed;
	}
}
