package engine.world.entity.npc;

import java.util.Random;

import engine.util.Direction;
import engine.visuals.Sprite;
import engine.visuals.SpriteSheet;
import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.entity.Entity;
import engine.world.entity.FrostbiteEntity;
import engine.world.entity.SpiritFlaskEntity;
import engine.world.entity.action.Action;
import game.Game;

public class NeoEntity extends NPC {
	
	public NeoEntity(float x, float y) {
		this.x = x;
		this.y = y;
		
		//Set the origin/pivot point of the NPC
		this.xOrigin = x;
		this.yOrigin = y;
		
		sprite = new Sprite(SpriteSheet.entities, 8, 0, true);
		
		walkRadius = 64;
		xMarker = x;
		yMarker = y;
		timeMoved = System.currentTimeMillis();
	}
	
	public void handle(World world) {
		
		if(currentAction == Action.frostbite && actionProgress >= 100) {
			world.addAttackEntity(new FrostbiteEntity(this, target, (int) x, (int) y));
		}
		
		if(!reachedMarker && !attacking) {
			x += velX;
			y += velY;
		}
		
		if(shouldRemove) {
			dropLoot(world);
		}

		
		x += velHitX;
		y += velHitY;
		
		velHitX = 0;
		velHitY = 0;
	}
	
	public void tick() {
		super.tick();
		
		if(actionProgress >= 100) {
			actionProgress = 0;
			currentAction = Action.noAction;
		}
		
		if(target != null && !target.dead()) {
			if(currentAction == Action.noAction) {
				currentAction = Action.frostbite;
				castTime = System.currentTimeMillis();
			}
			attacking = true;
		} else {
			attacking = false;
		}
		
		if(dead()) {
			shouldRemove = true;
		}
		
		if(reachedMarker) {
			timeSinceMoved = System.currentTimeMillis() - timeMoved;
			Random random = new Random();
			
			if(timeSinceMoved > 2000 + random.nextInt(20000)) {
				assignPositionMarker();
				timeMoved = System.currentTimeMillis();
			}
			reachedMarker = false;
		}
		
		if(Math.abs(x - xMarker) < 1.f && Math.abs(y - yMarker) < 1.f) {
			reachedMarker = true;
		}
		
		if(currentAction == Action.frostbite) {
			updateActionProgress();
		}
		
		updateSprite();
		sprite.tick();
	}
	
	private void updateSprite() {
		
		//Moving right & down
		if(velX > 0 && velY > 0) {
			directionFacing = Direction.DIR_DOWN_RIGHT;
		}
		//Moving right & up
		if(velX > 0 && velY < 0) {
			directionFacing = Direction.DIR_UP_RIGHT;
		}
		//Moving left & down
		if(velX < 0 && velY > 0) {
			directionFacing = Direction.DIR_DOWN_LEFT;
		}
		//Moving left & up
		if(velX < 0 && velY < 0) {
			directionFacing = Direction.DIR_UP_LEFT;
		}
		
		directionFacing = getDirectionToTarget();
//		directionFacing = 0;
		
		sprite.setDirectionMovement(directionMovement);
		sprite.setDirectionFacing(directionFacing);
	}
	
	private void assignPositionMarker() {
		Random random = new Random();
		xMarker = xOrigin + random.nextInt((int) walkRadius);
		yMarker = yOrigin + random.nextInt((int) walkRadius);
		
		float mag = (float) Math.sqrt((xMarker - x) * (xMarker - x) + (yMarker - y) * (yMarker - y));
		
		velX = (xMarker - x) / mag;
		velY = (yMarker - y) / mag;
	}
	
	public void render(Viewport viewport) {
		sprite.render(viewport, (int) x, (int) y);
		viewport.renderHealthBar(this, Game.SCALE);
		if(actionProgress != 0) {
			viewport.renderActionProgressBar(this, actionProgress, Game.SCALE);
		}
		
		viewport.renderBounds(this);
	}
	
	public void dropLoot(World world) {
		
		Random random = new Random();
		int xSign = random.nextInt(2) - 1;
		int ySign = random.nextInt(2) - 1;
		world.addEntity(new SpiritFlaskEntity(x + random.nextFloat() * w * xSign, y + random.nextFloat() * h * ySign));
		world.addEntity(new SpiritFlaskEntity(x + random.nextFloat() * w * xSign, y + random.nextFloat() * h * ySign));
	}
	
	public String getName() {
		return ID_NPC_NEO;
	}
}
