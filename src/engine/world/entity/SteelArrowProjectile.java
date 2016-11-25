package engine.world.entity;

import engine.visuals.viewport.Viewport;
import engine.world.World;
import game.Game;

public class SteelArrowProjectile extends Entity {
	
	private int damageOutput;
	private boolean reachedTarget, dealtDamage;
	public int id = 0;
	
	public SteelArrowProjectile(Entity target, float x, float y, int directionFacing, int damageOutput) {
		this.x = x;
		this.y = y;
		this.directionFacing = directionFacing;
		this.target = target;
		this.damageOutput = damageOutput;
		
		id += directionFacing;
		
		w = 8 * Game.SCALE;
		h = 8 * Game.SCALE;
		speed = 8;
	}
	
	public void tick() {
		float dirX = target.x - x;
		float dirY = target.y - y;
		double mag = Math.sqrt(dirX * dirX + dirY * dirY);
		dirX /= mag;
		dirY /= mag;
		velX = dirX * speed;
		velY = dirY * speed;
		
		move();
	}
	
	public void handle(World world) {
		
		if(reachedTarget) {
			if(!dealtDamage) {
				target.addLife(-damageOutput);
				dealtDamage = true;
				shouldRemove = true;
			}
		}
		
		x += velX;
		
		if(world.getEntity((int) x, (int) y, w, h) == target) {
			x -= velX;
			
			reachedTarget = true;
		}
		
		y += velY;
		
		if(world.getEntity((int) x, (int) y, w, h) == target) {
			y -= velY;
			
			reachedTarget = true;
		}
		
		velX = 0;
		velY = 0;
	}
}
