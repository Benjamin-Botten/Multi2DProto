package engine.world.entity;

import engine.world.entity.npc.NeoEntity;
import engine.world.entity.npc.TrinityEntity;

public class EntityFactory {
	
	
	/**
	 * Creates a new entity given the type name and position
	 * @param id
	 * @param x
	 * @param y
	 * @return
	 */
	public static Entity create(String name, float x, float y) {
		if(name.equals(Entity.ID_NPC_NEO)) {
			return new NeoEntity(x, y);
		}
		if(name.equals(Entity.ID_NPC_TRINITY)) {
			return new TrinityEntity(x, y);
		}
		
		return null;
	}
	
	/**
	 * Creates a new entity given the type ID and position
	 * @param id
	 * @param x
	 * @param y
	 * @return
	 */
	public static Entity create(int id, float x, float y) {
		return create(getName(id), x, y);
	}
	
	/**
	 * Sees if id exists in the types of entities declared
	 * @param id
	 * @return
	 */
	public static boolean exists(int id) {
		if(id < 0 || id > Entity.typenames.length) {
			return false;
		}
		return false;
	}
	
	/**
	 * Gets id of entity associated with given name by 
	 * searching through list of entity types linearly O(n)
	 * @param name
	 * @return if name exists, returns index/id value of name in the list of entity types, otherwise returns -1 as a "not found" message
	 */
	public static int getId(String name) {
		for(int i = 0; i < Entity.typenames.length; ++i) {
			if(Entity.typenames[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public static String getName(int id) {
		if(id < 0 || id > Entity.typenames.length) {
			throw new IllegalArgumentException("Attempted to get name of entity with id out of bounds of type-array");
		}
		return Entity.typenames[id];
	}
}
