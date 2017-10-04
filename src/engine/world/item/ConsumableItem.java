package engine.world.item;

import engine.world.World;
import engine.world.entity.Entity;
import engine.world.entity.Inventory;

/**
 * Consumables capture the entirety of items that are used as a resource and "enthropied" by the world
 * Consumables are all stackable so far, might change in the future
 * @author robot
 *
 */
public class ConsumableItem extends Item {
	protected int amount; //amount of the consumable in the stack
	
	public ConsumableItem() {
		amount = 1;
	}
	
	public void use(Entity entity) {
	}
	
	public void add() {
		if(amount >= Inventory.DEFAULT_SIZE_ITEM_STACK) return;
		++amount;
	}
	
	public void remove() {
		if(amount <= 0) return;
		--amount;
	}

	public int getAmount() {
		return amount;
	}
}
