package engine.world.entity;

import java.util.ArrayList;
import java.util.List;

import engine.world.item.Item;

public class Inventory {
	public static final int DEFAULT_SIZE = 64;
	
	private List<Item> items = new ArrayList<>();
	private int maxSize = DEFAULT_SIZE;
	
	public Inventory(Item... items) {
		for(int i = 0; i < items.length; ++i) {
			this.items.add(items[i]);
		}
	}
	
	public void add(Item item) {
		if(items.size() >= maxSize) return;
		items.add(item);
	}
	
	public void remove(Item item) {
		items.remove(item);
	}
	
	public void remove(int id) {
		for(int i = 0; i < items.size(); ++i) {
		}
	}
	
	public Item get(int index) {
		return items.get(index);
	}
	
	public Item get(Item item) {
		return null;
	}
	
	public Item getById(int id) {
		return null;
	}
	
	public List<Item> getItems() {
		return items;
	}
	
	public int numItems() {
		return items.size();
	}
}
