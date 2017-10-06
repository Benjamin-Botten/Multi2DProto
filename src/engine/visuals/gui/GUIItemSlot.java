package engine.visuals.gui;

import engine.io.Input;
import engine.util.Rect2D;
import engine.visuals.Sprite;
import engine.visuals.SpriteSheet;
import engine.visuals.viewport.Viewport;
import engine.world.item.Item;
import game.Game;

/**
 * This GUI element is a movable box within a specified bounds with a given tile (8x8 graphic)
 * @author robot
 *
 */
public class GUIItemSlot extends GUIElement {
	public static final int COLOR_TEXT_HOVER = 0xccffff00;
	public static final int COLOR_BACKGROUND_HOVER = 0x11777777;
	
	public static final int WIDTH = 8 * Game.SCALE;
	public static final int HEIGHT = 8 * Game.SCALE;
	
	
	private GUIElement parent;
	private Item item;
	private int slot;
	
	private int xOffset, yOffset;
	
	private boolean dragging;
	
	public GUIItemSlot(GUIElement parent, int slot, Item item) {
		this.parent = parent;
		this.item = item;
		this.slot = slot;
		
		int x = (slot % GUIInventory.WIDTH_ITEMS) << 4;
		int y = (slot / GUIInventory.HEIGHT_ITEMS) << 4;
		bounds = new Rect2D(parent.getBounds().x + x, parent.getBounds().y + y, WIDTH, HEIGHT);
	}
	
	public void tick(Input input) {
		//Clicked focus
		if(parent.hasFocus()) {
			int mx = input.getMouseX();
			int my = input.getMouseY();
			int mw = 1;
			int mh = 1;
			
			//Mouse left pressed
			if(input.mouseLeft()) {
				setOffset(input.getMouseXDragged() - (int) bounds.x, input.getMouseYDragged() - (int) bounds.y);
				
				if(bounds.isColliding(mx, my, mw, mh)) {
					focused = true;
					((GUIInventory) parent).setSelectionPressed(slot);
				} else {
					focused = false;
				}
				
			} else { //Mouse left released
				
				if(bounds.isColliding(mx, my, mw, mh)) {
					((GUIInventory) parent).setSelectionReleased(slot);
					focused = false;
				}
				
			}
		} else {
			focused = false;
		}
		
		if(!focused) {
			setOffset(0, 0);
		}
		
		//Hovering
		if(bounds.isColliding(input.getMouseX(), input.getMouseY(), 1, 1)) {
			hovering = true;
		} else {
			hovering = false;
		}
	}
	
	public void render(Viewport viewport) {
		if(focused) {
			viewport.renderBoundsScreenSpace((int) bounds.x, (int) bounds.y, bounds.getWidth(), bounds.getHeight());
		}
		viewport.render(item, (int) bounds.x + xOffset, (int) bounds.y + yOffset);
		
		if(hovering) {
			viewport.renderTextBackground(item.toString(), (int) bounds.x, (int) bounds.y - 16, item.toString().length() * 8, 8, COLOR_BACKGROUND_HOVER);
			viewport.renderTextScreenSpace(item.toString(), (int) bounds.x, (int) bounds.y - 16, COLOR_TEXT_HOVER);
		}
	}
	
	/** Getters */
	
	public int getSlot() {
		return slot;
	}
	
	public Item getItem() {
		return item;
	}
	
	public GUIElement getParent() {
		return parent;
	}
	
	/** Setters */
	
	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	public void setItem(Item item) {
		if(item == null) return;
		this.item = item;
	}
	
	public void setParent(GUIElement parent) {
		this.parent = parent;
	}
	
	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
