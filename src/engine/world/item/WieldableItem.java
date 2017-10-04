package engine.world.item;

import engine.world.entity.EntityAttributes;

public class WieldableItem extends Item {
	
	public static final int SLOT_HEAD = 0;
	public static final int SLOT_CHEST = 1;
	public static final int SLOT_LEGS = 2;
	public static final int SLOT_BOOTS = 3;
	public static final int SLOT_WEAPON = 4;
	public static final int SLOT_ORB = 5;
	
	protected int type;
	protected EntityAttributes attributes;
	
	public EntityAttributes getAttributes() {
		return attributes;
	}
	
	public int getSlotType() {
		return type;
	}
}
