package engine.world.entity;

import engine.visuals.viewport.Viewport;
import engine.world.World;

public class AttackEntity extends Entity {
	
	public int id;
	public int reachedTarget;
	protected Entity owner;
	protected int damageOutput;
	
	public AttackEntity(Entity src, Entity dst, float x, float y) {
		this.x = x;
		this.y = y;
		
		owner = src;
		target = dst;
		
		id = AttackEntityRegistry.getId(toString());
	}
	
	public void tick() {
		calculateMovementDirection();
		x += velX * 8;
		y += velY * 8;
	}
	
	public void handle(World world) {
	}

	private void calculateMovementDirection() {
		float dx = target.x - x;
		float dy = target.y - y;
		double mag = Math.sqrt(dx * dx + dy * dy);
		
		//Normalize
		dx /= mag;
		dy /= mag;
		
		velX = dx;
		velY = dy;
	}
	
	public int getOwnerNetId() {
		return owner.getNetId();
	}
	
	public void render(Viewport viewport) {
		viewport.render(this);
	}

	public int toInt() {
		return AttackEntityRegistry.getId(toString());
	}
	
	public String toString() {
		return "";
	}
}
