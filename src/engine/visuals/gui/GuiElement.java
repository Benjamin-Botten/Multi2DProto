package engine.visuals.gui;

import engine.visuals.viewport.Viewport;
import engine.world.entity.Player;

/**
 * 
 * @author robot
 *
 */
public class GuiElement {
	
	public final int id;
	
	public static final GuiElement[] guiElements = new GuiElement[128];
	
	public GuiElement(int id) {
		if(guiElements[id] != null) throw new IllegalArgumentException("Attempting to create gui element with duplicate id");
		
		guiElements[(this.id = id)] = this;
	}
	
	public void interact(Player player) {
	}
	
	public void render(Viewport viewport) {
	}
	
	public String toString() {
		return "Empty Gui Element";
	}
}
