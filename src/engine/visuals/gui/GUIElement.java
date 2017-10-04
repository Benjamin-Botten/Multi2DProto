package engine.visuals.gui;

import engine.util.Rect2D;
import engine.visuals.viewport.Viewport;
import engine.world.entity.Player;

/**
 * 
 * @author robot
 *
 */
public class GUIElement {
	
	protected boolean focused;
	protected boolean hovering;
	protected Rect2D bounds;
	
	
	/** Getters */ 
	
	
	public Rect2D getBounds() {
		return bounds;
	}
	
	public boolean hasFocus() {
		return focused;
	}
	
	public boolean isHovering() {
		return hovering;
	}
	
	/** Setters */
	
	public void setFocus(boolean focused) {
		this.focused = focused;
	}
	
	public void setHovering(boolean hovering) {
		this.hovering = hovering;
	}
}
