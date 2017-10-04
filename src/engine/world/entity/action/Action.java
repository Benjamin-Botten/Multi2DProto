package engine.world.entity.action;

public class Action {
	public static final int MAX_ACTIONS = 128;
	public static final long GLOBAL_COOLDOWN_DURATION = 500; //ms
	
	protected final int id;
	protected final String name;
	protected final String description;
	protected final long castTime; //ms
	protected final long cooldownTime; //ms
	
	private static final Action[] actions = new Action[MAX_ACTIONS];
	
	public static final Action noAction = new NoAction(0, "noaction", "Feels like I'm doing nothing at all!", 0, 0);
	public static final Action weaponAttack = new WeaponAttack(1, "weaponattack", "Attacking using weapon", 500, 0); //Weapon attack gets cast time from weapon wielded by entity
	public static final Action frostbite = new FrostBite(2, "frostbite", "Magic spell used by wizards which chills the target", 750, 0);
	
	public Action(int id, String name, String description, long castTime, long cooldownTime) {
		if(actions[id] != null) throw new IllegalArgumentException("Duplicate Actions");
		actions[id] = this;
		
		this.id = id;
		this.name = name;
		this.description = description;
		this.castTime = castTime;
		this.cooldownTime = cooldownTime;
	}
	
	public String toString() {
		return name;
	}
	
	public int getId() {
		return id;
	}

	public long getCastTime() {
		return castTime;
	}
	
	public long GetCooldownTime() {
		return cooldownTime;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static Action getAction(int actionId) {
		if(actionId < 0 || actionId >= MAX_ACTIONS) return noAction;
		return actions[actionId];
	}
}
