package engine.world.item;

import engine.world.entity.Entity;
import engine.world.entity.Player;

public class Item {

	public final int id;
	public final String name;
	public final String description;
	
	public static final Item[] items = new Item[256];
	
	public static final LifeFlask lifeflask = new LifeFlask(0, "Life Flask", "Fills Life");
	public static final SustainFlask sustainflask = new SustainFlask(1, "Sustain Flask", "Sustains You");
	public static final SteelArrow steelarrow = new SteelArrow(4, "Sustain Flask", "Sustains You");
	public static final WoodenBow woodenbow = new WoodenBow(5, "Sustain Flask", "Sustains You");
	
	public Item(int id, String name, String description) {
		if(items[id] != null) throw new IllegalArgumentException("Duplicate id for different items");
		
		this.id = id;
		this.name = name;
		this.description = description;
		
		items[id] = this;
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
	
	public String toString() {
		return name;
	}
}
