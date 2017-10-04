package engine.world.entity;

import java.util.HashMap;

/**
 * 
 * @author robot
 *
 */
public class AttackEntityRegistry {
	
	private static final int MAX_ATTACK_ENTITIES = 128;
	
	public static final int ID_ATTACK_ENTITY_FROSTBITE = 0;
	public static final int ID_ATTACK_ENTITY_FIREBOLT = 1;
	
	public static final String NAME_ATTACK_ENTITY_FROSTBITE = "frostbite";
	public static final String NAME_ATTACK_ENTITY_FIREBOLT = "firebolt";
	
	private static final String[] entries = new String[MAX_ATTACK_ENTITIES];
	private static final HashMap<String, Integer> attackEntityTable = new HashMap<>();
	
	private static final AttackEntityRegistry registry = new AttackEntityRegistry();
	
	public AttackEntityRegistry() {
		add(ID_ATTACK_ENTITY_FROSTBITE, NAME_ATTACK_ENTITY_FROSTBITE)
		.add(ID_ATTACK_ENTITY_FIREBOLT, NAME_ATTACK_ENTITY_FIREBOLT);
	}
	
	public AttackEntityRegistry add(int id, String name) {
		if(entries[id] != null) {
			throw new IllegalArgumentException("Duplicate AttackEntity-entry in AttackEntityRegistry!");
		} else {
			attackEntityTable.put(name, id);
			entries[id] = name;
			return this;
		}
	}
	
	public static String getName(int id) {
		if(id < 0 || id >= MAX_ATTACK_ENTITIES) throw new IllegalArgumentException("Attempting to get AttackEntity entry from registry with ID out of bounds!");
		return entries[id];
	}
	
	public static int getId(String name) {
		if(attackEntityTable.get(name) == null) {
			return -1;
		}
		return attackEntityTable.get(name);
	}
	
	public static AttackEntity create(int id, Entity owner, Entity target, int netId, int x, int y) {
		switch (id) {
		case ID_ATTACK_ENTITY_FROSTBITE:
			return new FrostbiteEntity(owner, target, x, y);
		case ID_ATTACK_ENTITY_FIREBOLT:
			return null;
		default:
			return null;
		}
	}
}
