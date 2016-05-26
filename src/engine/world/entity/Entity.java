package engine.world.entity;

import engine.visuals.Sprite;
import engine.visuals.viewport.Viewport;

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
	protected Sprite sprite;
	protected int directionMovement, directionFacing;
	protected boolean collidable;
	protected Entity target;
	
	//Stats
	protected int life;
	protected int love;
	
	//Networking
	protected int netId; //The entity's id on the server
	
	public Entity() {
	}
	
	public void tick() {
	}
	
	public void move() {
	}
	
	public boolean solid() {
		return collidable;
	}
	
	public void interact(Entity entity) {
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
}
