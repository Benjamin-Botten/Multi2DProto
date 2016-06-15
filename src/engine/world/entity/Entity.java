package engine.world.entity;

import engine.visuals.Sprite;
import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.item.Item;

public class Entity {
	
	public static final int DIR_DOWN_RIGHT = 0;
	public static final int DIR_DOWN_LEFT = 1;
	public static final int DIR_UP_RIGHT = 2;
	public static final int DIR_UP_LEFT = 3;
	public static final int DIR_DOWN = 4;
	public static final int DIR_UP = 5;
	public static final int DIR_RIGHT = 6;
	public static final int DIR_LEFT = 7;
	
	public float x, y;
	public float velX = 0, velY = 0;
	public float maxSpeed = 3, speed = maxSpeed;
	protected int w, h;
	protected int ticks = 0;
	protected Sprite sprite;
	protected int directionMovement, directionFacing;
	protected boolean collidable;
	protected Entity target;
	protected Inventory inventory = new Inventory();
	
	//Stats
	protected int maxLife = 1000, life = maxLife;
	protected int maxLove = 100, love = maxLove;
	
	//Networking
	protected int netId; //The entity's id on the server
	
	public Entity() {
	}
	
	public void tick() {
		ticks++;
		if(ticks > 32) {
			ticks = 0;
		}
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
	
	public float getCenter() {
		return x + (w >> 1);
	}
	
	public float getCenterY() {
		return y + (h >> 1);
	}
}
