package engine.world.entity;

import java.util.ArrayList;
import java.util.List;

import engine.util.Direction;
import engine.visuals.FloaterText;
import engine.visuals.Sprite;
import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.entity.action.Action;
import engine.world.entity.action.ActionQueue;
import engine.world.item.Item;
import game.Game;

public class Entity {
	//Entity IDs
	public static final String ID_NPC_NEO = "NeoEntity";
	public static final String ID_NPC_TRINITY = "TrinityEntity";
	public static final String ID_ITEM_LIFEFLASK = "LifeFlaskEntity";
	public static final String ID_ITEM_SUSTAINFLASK ="SustainFlaskEntity";
	
	public static final String[] typenames = {
			ID_NPC_NEO, ID_NPC_TRINITY, ID_ITEM_LIFEFLASK, ID_ITEM_SUSTAINFLASK,
			
	};

	//Entity Network IDs
	public static final int ID_NET_NONE = 0;
	
	//Entity attributes
	public float x, y;
	public float xSpawn, ySpawn; //Keep track of the entity's spawn location
	public float velX, velY;
	public float velHitX, velHitY; //Velocity on hit from other entity
	public float maxSpeed = 3, speed = maxSpeed;
	public int w = 16 * Game.SCALE;
	public int h = 16 * Game.SCALE;
	protected int ticks = 0;
	protected Sprite sprite;
	protected int directionMovement, directionFacing;
	protected int pickupRadius;
	protected EntityAttributes baseAttributes; //Baseline attributes for any entity
	protected int combatLevel;
	
	//Inventory & Items
	protected Inventory inventory = new Inventory();
	protected Item weapon;
	protected Item pieceChest, pieceLegs, pieceHead;
	
	//Target, interactions&actions
	protected Entity target;
	protected boolean hasTarget = false;
	protected boolean attacking = false; //maybe add a different boolean state "inCombat" to keep track of combat state, and keep attacking for actual "in progress" atks
	protected boolean shouldRemove = false;
	protected boolean collidable = false;
	
	protected Action currentAction = Action.noAction; //TODO: Maybe just keep track of it using bytes/shorts since we have an id-lookup table
	protected int actionProgress; //How far along is the player doing the queued action?
	protected List<AttackEntity> attackEntities = new ArrayList<>();
	protected boolean isCasting = false;
	
	//Typename for this *particular* entity
	protected String name;
	
	
	//Timers
	protected long attackTime;
	protected long castTime;
	
	//Stats
	protected int maxLife = 1000, life = maxLife;
	protected int maxLove = 100, love = maxLove;
	
	//Networking
	protected int netId; //The entity's id on the server
	protected int targetNetId;
	
	public Entity() {
	}
	
	public void tick() {
		ticks++;
		if(ticks > Game.DEFAULT_CLIENT_TICKS) {
			ticks = 0;
		}
	}
	
	public void handle(World world) {
	}
	
	/**
	 * TODO: Make sure you use an error-checking method here, so the entity doesn't bounce back at the speed of light
	 * @param velX
	 * @param velY
	 */
	public void hit(float velHitX, float velHitY) {
		this.velHitX = velHitX;
		this.velHitY = velHitY;
	}
	
	public void move() {
	}
	
	public boolean solid() {
		return collidable;
	}
	
	public void interact(Entity entity) {
	}
	
	public void interact(World world, Entity entity) {
	}
	
	public void render(Viewport viewport) {
		viewport.render(this);
		//Debugging purposes
//		viewport.renderBounds(this);
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public int getDirectionMovement() {
		return directionMovement;
	}
	
	public int getDirectionFacing() {
		return directionFacing;
	}
	
	
	public void setTarget(Entity entity) {
		this.target = entity;
	}
	
	public void setNetId(int netId) {
		this.netId = netId;
	}
	
	public Entity getTarget() {
		return target;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public int getNetId() {
		return netId;
	}
	
	public int getLife() {
		return life;
	}
	
	public int getLove() {
		return love;
	}
	
	public void addLife(int lifepoints) {
		if(life + lifepoints <= maxLife) {
			life += lifepoints;
		}
	}
	
	public int getMaxLife() {
		return maxLife;
	}
	
	public int getMaxLove() {
		return maxLove;
	}
	
	/**
	 * Adds item to inventory
	 * @param item
	 */
	public void addItem(Item item) {
		inventory.add(item);
	}
	
	public void damage(int amount) {
		life -= amount;
		
		if(life < 0) life = 0;
		if(life > maxLife) life = maxLife;
	}
	
	public float getCenterX() {
		return x + (w >> 1);
	}
	
	public float getCenterY() {
		return y + (h >> 1);
	}
	
	public void follow(Entity followTarget) {
	}
	
	public int getTargetNetId() {
		if(target == null) {
			return Entity.ID_NET_NONE;
		}
		return target.getNetId();
	}
	
	public int getActionProgress() {
		return actionProgress ;
	}
	
	public void setLife(int life) {
		if(life < 0) {
			life = 0;
			return;
		}
		if(life > maxLife) {
			life = maxLife;
			return;
		}
		
		this.life = life;
	}
	
	public void setActionProgress(int actionProgress) {
		this.actionProgress = actionProgress;
	}
	
	public void setCurrentAction(Action action) {
		currentAction = action;
	}
	
	public void setCastTime(long castTime) {
		this.castTime = castTime;
	}

	public void setAttackTime(long attackTime) {
		this.attackTime = attackTime;
	}
	
	public void setIsCasting(boolean isCasting) {
		this.isCasting = isCasting;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public Action getCurrentAction() {
		return currentAction;
	}
	
	public long getCastTime() {
		return castTime;
	}
	
	public long getAttackTime() {
		return attackTime;
	}
	
	public boolean isCasting() {
		return isCasting;
	}
	
	/**
	 * 
	 * @param entity
	 * @return true if entity is colliding with this entity
	 */
	public boolean colliding(Entity entity) {
		if(entity == null) return false;
		
		if(x >= (entity.x + entity.w)) return false;
		if((x + w) <= entity.x) return false;
		if(y >= (entity.y + entity.h)) return false;
		if((y + h) <= entity.y) return false;
		
		return true;
	}
	
	public boolean colliding(int xp, int yp, int ew, int eh) {
		if(x >= xp + ew) return false;
		if(x + w <= xp) return false;
		if(y >= yp + eh) return false;
		if(y + h <= yp) return false;
		return true;
	}
	
	/**
	 * Checks to see if a given entity is within a specific radial distance of another
	 * @param entity
	 * @param rSquared
	 * @return true if distance is less or equal to rSquared
	 */
	public boolean isWithinRadius(Entity entity, int rSquared) {
		float dx = entity.getCenterX() - getCenterX();
		float dy = entity.getCenterY() - getCenterY();
		
		
		
		float magnitudeSquared = (dx * dx + dy * dy);
		
		System.out.println("Squared Distance > " + magnitudeSquared);
		
		if(magnitudeSquared <= rSquared) {
			return true;
		}
		
		return false;
	}
	
	public double getDistance(Entity target) {
		float dx = target.getCenterX() - getCenterX();
		float dy = target.getCenterY() - getCenterY();
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public int getDirectionToTarget() {
		if (target == null) {
			return directionFacing;
		}
		
		float dx = x - target.x;
		float dy = y - target.y;


		float angle = (float) (Math.atan2(dy, dx) * (180f / Math.PI));
		angle += 180;
		angle = 360 - angle;

		int right = 0;
		int up = 90;
		int left = 180;
		int down = 270;
		float da = (360 / 8f) / 2f;

		// Right
		if (angle >= 0 && angle <= da || angle >= (360 - da) && angle <= 360) {
			return Direction.DIR_RIGHT;
		}
		// Right + Up
		else if (angle >= 45 && angle <= 45 + da || angle >= (45 - da) && angle <= 45) {
			return Direction.DIR_UP_RIGHT;
		}
		// Up
		else if (angle >= 90 && angle <= 90 + da || angle >= (90 - da) && angle <= 90) {
			return Direction.DIR_UP;
		}
		// Up Left
		else if (angle >= 135 && angle <= 135 + da || angle >= (135 - da) && angle <= 135) {
			return Direction.DIR_UP_LEFT;
		}
		// Left
		else if (angle >= 180 && angle <= 180 + da || angle >= (180 - da) && angle <= 180) {
			return Direction.DIR_LEFT;
		}
		// Down Left
		else if (angle >= 225 && angle <= 225 + da || angle >= (225 - da) && angle <= 225) {
			return Direction.DIR_DOWN_LEFT;
		}
		// Down
		else if (angle >= 270 && angle <= 270 + da || angle >= (270 - da) && angle <= 270) {
			return Direction.DIR_DOWN;
		}
		// Down Right
		else if (angle >= 315 && angle <= 315 + da || angle >= (315 - da) && angle <= 315) {
			return Direction.DIR_DOWN_RIGHT;
		}
		return directionMovement;
	}
	
	protected void updateActionProgress() {
		long timeSinceCast = System.currentTimeMillis() - castTime;
		actionProgress = (int) (((float) timeSinceCast / currentAction.getCastTime()) * 100.f);
	}
	
	public void dropLoot(World world) {
	}
	
	public boolean canRemove() {
		return shouldRemove;
	}
	
	public boolean dead() {
		return life <= 0;
	}
	
	public String getName() {
		return toString();
	}
	
	public String toString() {
		return name;
	}
	
	public int getType() {
		return EntityFactory.getId(getName());
	}
}
