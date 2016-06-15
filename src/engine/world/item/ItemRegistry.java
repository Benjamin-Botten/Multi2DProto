package engine.world.item;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {
	
	private static final Map<String, Item> registry = new HashMap<>();
	
	private static final ItemRegistry lifeflask = new ItemRegistry(Item.lifeflask);
	private static final ItemRegistry sustainflask = new ItemRegistry(Item.sustainflask);
	
	public ItemRegistry(Item item) {
		registry.put(item.toString(), item);
	}
	
	public Item getItem(String name) {
		return registry.get(name);
	}
	
	public Item get(int id) {
		if(id > Item.items.length || id < 0) {
			throw new IllegalArgumentException("Stop it! Attempting to get item out of bounds");
		}
		return Item.items[id];
	}
	
}
