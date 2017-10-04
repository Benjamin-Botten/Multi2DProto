package engine.visuals.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import engine.io.Input;
import engine.util.Rect2D;
import engine.visuals.SpriteSheet;
import engine.visuals.viewport.Viewport;
import engine.world.entity.Inventory;
import engine.world.entity.Player;
import engine.world.item.Item;
import game.Game;
import game.GameApplication;

public class GUIInventory extends GUIElement {
	public static final int ID_SELECTION_NONE = -1;
	public static final int ID_SELECTION_OUTSIDE_INVENTORY = -2;
	
	public static final int COLOR_BACKGROUND = 0xdd00ffff;
	public static final int WIDTH_ITEMS = 4;
	public static final int HEIGHT_ITEMS = 4;
	
	private HashMap<Integer, Item> items = new HashMap<>();
	
	private List<GUIItemSlot> inventorySlots = new ArrayList<>();
	
	private Player player;
	
	private int selectionPressed; //selected item-slot in the inventory
	private int selectionReleased;
	private int x, y;
	private final int w, h;
	
	public GUIInventory(Player player) {
		this.player = player;
		
		w = WIDTH_ITEMS * SpriteSheet.items.ew * Game.SCALE;
		h = HEIGHT_ITEMS * SpriteSheet.items.eh * Game.SCALE;
		x = Game.WIDTH - w;
		y = Game.HEIGHT - h;
		bounds = new Rect2D(x, y, w, h);
		
		init();
	}
	
	private void init() {
		
		Inventory inventory = player.getInventory();
		for(int i = 0; i < inventory.size(); ++i) {
			items.put(i, inventory.get(i));
			inventorySlots.add(new GUIItemSlot(this, i, inventory.get(i)));
		}
	}
	
	private void updateInventory() {
		
		Inventory inventory = player.getInventory();
		for(int i = 0; i < inventory.size(); ++i) {
			items.put(i, inventory.get(i));
			inventorySlots.get(i).setItem(inventory.get(i));
		}
	}
	
	public void swapSlots(int selection0, int selection1) {
		if(selection0 < 0 || selection1 < 0 || selection0 >= Inventory.DEFAULT_SIZE_UNIQUE_ITEM || selection1 >= Inventory.DEFAULT_SIZE_UNIQUE_ITEM) {
			return;
		}
		
		player.getInventory().swap(selection0, selection1);
		
		inventorySlots.get(selection0).setFocus(false);
		
		resetSelection();
	}
	
	private void resetSelection() {
		selectionPressed = ID_SELECTION_NONE;
		selectionReleased = ID_SELECTION_NONE;
	}
	
	public void tick(Input input) {
		
		//Drop item if inventory has selection on pressed but none on release
		if(selectionPressed != ID_SELECTION_NONE && selectionReleased == ID_SELECTION_OUTSIDE_INVENTORY) {
			System.out.println("Dropping item at slot > " + selectionPressed + " to slot " + selectionReleased);
			int slot = inventorySlots.get(selectionPressed).getSlot();
			player.getInventory().remove(slot);
			resetSelection();
		}
		
		//Reset inventory selections if there is a release selection but no press selection
		if(selectionReleased != ID_SELECTION_NONE && selectionPressed == ID_SELECTION_NONE) {
			resetSelection();
		}
		
		updateInventory();
		
		//Swap inventory slots if has pressed & released selection
		if(selectionPressed != ID_SELECTION_NONE && selectionReleased != ID_SELECTION_NONE) {
			swapSlots(selectionPressed, selectionReleased);
		}

		int mx = input.getMouseX();
		int my = input.getMouseY();
		int mw = 1;
		int mh = 1;
		if(input.mouseLeft()) {
			if(bounds.isColliding(mx, my, mw, mh)) {
				focused = true;
			} else {
				focused = false;
			}
		} else {
			if(!bounds.isColliding(mx, my, mw, mh)) {
				setSelectionReleased(ID_SELECTION_OUTSIDE_INVENTORY);
			}
		}
		
		//Update inventory-slots
		for(int i = 0; i < inventorySlots.size(); ++i) {
			inventorySlots.get(i).tick(input);
		}
	}
	
	public void render(Viewport viewport) {
		viewport.renderRect(x, y, w, h, COLOR_BACKGROUND);
		for(int i = 0; i < inventorySlots.size(); ++i) {
			GUIItemSlot slot = inventorySlots.get(i);
			slot.render(viewport);
		}
	}
	
	/** Getters */
	
	public int getSelectionPressed() {
		return selectionPressed;
	}
	
	public int getSelectionReleased() {
		return selectionReleased;
	}
	
	/** Setters */
	
	public void setSelectionPressed(int selectionPressed) {
		this.selectionPressed = selectionPressed;
	}
	
	public void setSelectionReleased(int selectionReleased) {
		this.selectionReleased = selectionReleased;
	}
}
