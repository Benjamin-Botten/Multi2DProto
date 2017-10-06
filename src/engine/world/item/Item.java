package engine.world.item;

import engine.world.entity.Entity;
import engine.world.entity.Player;

public class Item {
	
	//Flasks, Potions & Other Consumables
	public static final int ID_ITEM_NONE = 0;
	public static final int ID_ITEM_FLASK_LIFE = 1;
	public static final int ID_ITEM_FLASK_NOA = 2;
	public static final int ID_ITEM_FLASK_SPIRIT = 3;
	public static final int ID_ITEM_FLASK_POWER = 4;
	
	//Arrows & Bows
	public static final int ID_ITEM_ARROW_STEEL = 5;
	public static final int ID_ITEM_BOW_WOOD = 6;
	
	protected int id;
	protected String name;
	protected String description;
	
	public Item() {
		id = ID_ITEM_NONE;
		name = "";
		description = "";
	}
	
	/**
	 * If this item can be used with its owner and *do something to the owner*, this method will be filled in
	 */
	public void use(Entity entity) {
	}
	
	/**
	 * Use this item with a target entity (if that target is yourself, call {@code public void use()}.
	 * @param entity
	 */
	public void useOn(Entity entity) {
	}
	
	/**
	 * @return String containing the name of the item (case-intact)
	 */
	public String toString() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
}
