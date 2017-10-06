package engine.world.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import engine.world.item.ConsumableItem;
import engine.world.item.Item;

public class Inventory {
	public static final int INVENTORY_FULL = -1;
	public static final int DEFAULT_SIZE_UNIQUE_ITEM = 16;
	public static final int DEFAULT_SIZE_ITEM_STACK = 128;
	
	private List<Item> items = Arrays.asList(new Item[DEFAULT_SIZE_UNIQUE_ITEM]); //new ArrayList<Item>(DEFAULT_SIZE_UNIQUE_ITEM);
	
	public Inventory() {
		for(int i = 0; i < DEFAULT_SIZE_UNIQUE_ITEM; ++i) {
			items.set(i, new Item());
		}
	}
	
//	private int maxSize = DEFAULT_SIZE_UNIQUE_ITEM;
	
	public Inventory(Item... items) {
		for(int i = 0; i < items.length; ++i) {
			add(items[i]);
		}
	}
	
	
	public void add(Item item) {
		if(item == null) return;
		
		Item consumable = searchConsumable(item);
		if(consumable != null) {
			((ConsumableItem) consumable).add();
		} else {
			int slot = getVacantSlot();
			if(slot != INVENTORY_FULL) {
				items.set(slot, item);
			}
		}
	}
	
	public boolean hasConsumable(Item item) {
		if(item instanceof ConsumableItem) {
			Item consumable = searchConsumable(item);
			if(consumable != null) {
				return true;
			}
		}
		return false;
	}
	
	private Item searchConsumable(Item item) {
		if(item instanceof ConsumableItem) {
			for(int i = 0; i < items.size(); ++i) {
				if(items.get(i).getId() == item.getId()) {
					return items.get(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return index of inventory space available for use, otherwise -1 signalling full inventory
	 */
	private int getVacantSlot() {
		for(int i = 0; i < DEFAULT_SIZE_UNIQUE_ITEM; ++i) {
			if(items.get(i).getId() == Item.ID_ITEM_NONE) {
				return i;
			}
		}
		return INVENTORY_FULL;
	}
	
	public void add(int position, Item item) {
		if(items.get(position) == null) {
			items.set(position, item);
		} else if(items.get(position).getId() == Item.ID_ITEM_NONE) {
			Item consumable = searchConsumable(item);
			if(consumable != null) {
				((ConsumableItem) consumable).add();
			} else {
				items.set(position, item);
			}
		} else {
			add(item);
		}
	}
	
	/**
	 * TODO: Maybe return-type boolean to signal success of swap
	 * Swaps items in the inventory at given positions
	 * @param pos0
	 * @param pos1
	 */
	public void swap(int pos0, int pos1) {
		if(pos0 < 0 || pos1 < 0 || pos0 >= DEFAULT_SIZE_UNIQUE_ITEM || pos1 >= DEFAULT_SIZE_UNIQUE_ITEM) {
			return;
		}
		
		System.out.println("Swapping inventory slot " + pos0 + " with " + pos1);
		
		//Hold the item at position 0
		Item tmp = items.get(pos0);
		//Set the item at position 0 to the item at position 1
		items.set(pos0, items.get(pos1));
		//Set the item at position 1 to what the position 0 was (tmp)
		items.set(pos1, tmp);
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 */
	public boolean remove(Item item) {
		return items.remove(item);
	}
	
	/**
	 * Removes an item from given slot
	 * @param slot
	 */
	public void remove(int slot) {
		if(slot < 0 || slot > DEFAULT_SIZE_UNIQUE_ITEM) {
			return;
		}
		Item item = items.get(slot);
//		if(item instanceof ConsumableItem) {
//			ConsumableItem cItem = (ConsumableItem) item;
//			cItem.remove();
//			
//			if(cItem.getAmount() == 0) {
//				items.set(slot, new Item());
//			}
//		} else {
			items.set(slot, new Item());
//		}
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

	public int size() {
		return items.size();
	}
}
