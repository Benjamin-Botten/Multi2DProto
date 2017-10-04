package engine.world.entity;

import java.awt.Color;

import engine.visuals.FloaterText;
import engine.world.World;

public class FrostbiteEntity extends AttackEntity {

	public FrostbiteEntity(Entity src, Entity dst, float x, float y) {
		super(src, dst, x, y);
		
		id = AttackEntityRegistry.getId(toString());
	}
	
	public void tick() {
		super.tick();
	}
	
	public void handle(World world) {
		if(target.dead()) {
			world.removeAttackEntity(this);
		}
		if(this.colliding(target)) {
			System.out.println("Removing attackentity from world");
			target.setLife(target.getLife() - 100);
			target.setTarget(owner);
			target.hit(velX, velY);
			
			world.addFloaterText(new FloaterText("100", x, y - 8, 0xffffff00, 1000));
			
			world.removeAttackEntity(this);
		}
	}
	
	public String toString() {
		return AttackEntityRegistry.NAME_ATTACK_ENTITY_FROSTBITE;
	}

}
