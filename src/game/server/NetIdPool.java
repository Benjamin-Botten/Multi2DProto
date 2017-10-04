package game.server;

import java.util.concurrent.ConcurrentHashMap;

import engine.world.entity.AttackEntity;
import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import engine.world.entity.npc.NPC;

public class NetIdPool {
	
	private static final int MAX_ATTACK_ENTITIES_PER_ENTITY = 5;
	
	private static final int START_ID_PLAYERS = 1;
	private static final int MAX_PLAYERS = 2000;
	private static final int START_ID_NPCS = (START_ID_PLAYERS + MAX_PLAYERS);
	private static final int MAX_NPCS = 40000;
	private static final int START_ID_ATTACK_ENTITIES = START_ID_NPCS + MAX_NPCS;
	private static final int MAX_ATTACK_ENTITIES = (MAX_PLAYERS + MAX_NPCS) * MAX_ATTACK_ENTITIES_PER_ENTITY;
	
	private ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, NPC> npcs = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, AttackEntity> attackEntities = new ConcurrentHashMap<>();
	
	//Placeholders for the hashmaps
	private Player placeholderPlayer = new Player(null);
	private NPC placeholderNPC = new NPC();
//	private AttackEntity placeholderAttackEntity = new AttackEntity(null, null, 0, 0, 0);
	
	public NetIdPool() {
		initLists();
	}
	
	private void initLists() {
//		for(int i = START_ID_NPCS; i < START_ID_NPCS + MAX_NPCS; ++i) { 
//			npcs.put(i, placeholderNPC);
//		}
//		for(int i = START_ID_PLAYERS; i < START_ID_PLAYERS + MAX_PLAYERS; ++i) {
//			players.put(i, placeholderPlayer);
//		}
//		for(int i = START_ID_ATTACK_ENTITIES; i < START_ID_ATTACK_ENTITIES + MAX_ATTACK_ENTITIES; ++i) {
//			attackEntities.put(i, placeholderAttackEntity);
//		}
	}
	
	/**
	 * allocates an integer id value representing the player online
	 * @param plr
	 * @return int, player's net id on success, return -1 if server is full
	 */
	public int allocatePlayer(Player plr) {
		for(int i = START_ID_PLAYERS; i < START_ID_PLAYERS + MAX_PLAYERS; ++i) {
			if(players.get(i) == null) {
				players.put(i, placeholderPlayer);
				return i;
			}
		}
		return -1;
	}
	
	public int allocateAttackEntity() {
		for(int i = START_ID_ATTACK_ENTITIES; i < START_ID_ATTACK_ENTITIES + MAX_ATTACK_ENTITIES; ++i) {
			if(attackEntities.get(i) == null) {
//				attackEntities.put(i, placeholderAttackEntity);
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Puts a specific NPC onto a specific Net ID in the pool, and so remains for the entire server session
	 * @param id
	 * @param npc
	 */
	public void putNPC(int id, NPC npc) {
		npcs.put(id, npc);
	}
	
	public void deallocatePlayer(Player player) {
		players.remove(player.getNetId());
	}
	
	public void deallocateAttackEntity(AttackEntity attackEntity) {
		attackEntities.remove(attackEntity.getNetId());
	}
}
