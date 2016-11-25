package engine.world.entity;

import com.sun.glass.events.KeyEvent;

import engine.io.Input;
import engine.visuals.Sprite;
import engine.visuals.SpriteSheet;
import engine.visuals.viewport.Camera;
import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.entity.attack.Attack;
import game.Game;

public class Player extends Entity {
	
	private Input input;
	private Camera camera;
	
	public Player(Input input, Camera camera) {
		super();
		
		sprite = new Sprite(SpriteSheet.entities, 0, 2, 0, 0);
	
		this.input = input;
		this.camera = camera;
	}
	
	public void setCamera(int camX, int camY) {
		if(camX < 0) {
			camX = 0;
		}
		if(camY < 0) {
			camY = 0;
		}
		camera.set(camX, camY);
	}	
	
	public void tick() {
		super.tick();
		
		sprite.tick();
		
		setCamera((int) x - Game.HALF_WIDTH, (int) y - Game.HALF_HEIGHT);
		
		if(input.keys[KeyEvent.VK_A]) {
			directionMovement = DIR_LEFT;
			directionFacing = DIR_LEFT;
			velX = -speed;
		}
		if(input.keys[KeyEvent.VK_D]) {
			directionMovement = DIR_RIGHT;
			directionFacing = DIR_RIGHT;
			velX = speed;
		}
		if(input.keys[KeyEvent.VK_W]) {
			directionMovement = DIR_UP;
			directionFacing = DIR_UP;
			velY = -speed;
		}
		if(input.keys[KeyEvent.VK_S]) {
			directionMovement = DIR_DOWN;
			directionFacing = DIR_DOWN;
			velY = speed;
		}
		if(input.keys[KeyEvent.VK_D] && input.keys[KeyEvent.VK_W]) {
			directionMovement = DIR_UP_RIGHT;
			directionFacing = DIR_UP_RIGHT;
			velX = speed;
			velY = -speed;
		}
		if(input.keys[KeyEvent.VK_D] && input.keys[KeyEvent.VK_S]) {
			directionMovement = DIR_DOWN_RIGHT;
			directionFacing = DIR_DOWN_RIGHT;
			velX = speed;
			velY = speed;
		}
		if(input.keys[KeyEvent.VK_A] && input.keys[KeyEvent.VK_W]) {
			directionMovement = DIR_UP_LEFT;
			directionFacing = DIR_UP_LEFT;
			velX = -speed;
			velY = -speed;
		}
		if(input.keys[KeyEvent.VK_A] && input.keys[KeyEvent.VK_S]) {
			directionMovement = DIR_DOWN_LEFT;
			directionFacing = DIR_DOWN_LEFT;
			velX = -speed;
			velY = speed;
		}
		if(input.keys[KeyEvent.VK_TAB]) {
		}
		if(input.keys[KeyEvent.VK_SPACE]) {
			if(hasTarget) {
				attacking = true;
			}
		}
		
		directionFacing = getDirectionToTarget();
		
		sprite.setDirectionMovement(directionMovement);
		sprite.setDirectionFacing(directionFacing);
	}
	
	private int getDirectionToTarget() {
		if(!hasTarget) return directionFacing;
		float dx = x - target.x;
		float dy = y - target.y;
		
//		float angle = (float) Math.asin(dy / Math.sqrt((dx * dx + dy * dy)));
//		angle *= (180.f / Math.PI);
		
		float angle = (float) (Math.atan2(dy, dx) * (180f / Math.PI));
		angle += 180;
		angle = 360 - angle;
		
//		System.out.println("Angle to target " + angle);
		
		int right = 0;
		int up = 90;
		int left = 180;
		int down = 270;
		float da = (360 / 8f) / 2f;
		
		//Right
		if(angle >= 0 && angle <= da ||
				angle >= (360 - da) && angle <= 360) {
			return DIR_RIGHT;
		}
		//Right + Up
		else if(angle >= 45 && angle <= 45 + da ||
			angle >= (45 - da) && angle <= 45) {
				return DIR_UP_RIGHT;
		}
		//Up
		else if(	angle >= 90 && angle <= 90 + da ||
			angle >= (90 - da) && angle <= 90) {
			return DIR_UP;
		}
		//Up Left
		else if(	angle >= 135 && angle <= 135 + da ||
			angle >= (135 - da) && angle <= 135) {
			return DIR_UP_LEFT;
		}
		//Left
		else if(	angle >= 180 && angle <= 180 + da ||
			angle >= (180 - da) && angle <= 180) {
			return DIR_LEFT;
		}
		//Down Left
		else if(	angle >= 225 && angle <= 225 + da ||
			angle >= (225 - da) && angle <= 225) {
			return DIR_DOWN_LEFT;
		}
		//Down
		else if(	angle >= 270 && angle <= 270 + da ||
			angle >= (270 - da) && angle <= 270) {
			return DIR_DOWN;
		}
		//Down Right
		else if(	angle >= 315 && angle <= 315 + da ||
			angle >= (315 - da) && angle <= 315) {
			return DIR_DOWN_RIGHT;
		}
		return directionMovement;
	}
	
	public void render(Viewport viewport) {
		super.render(viewport);
		
		int healthBarId = 4;
		if(getLife() >= (int) (getMaxLife() * 0.76)) {
			healthBarId = 0;
		}
		else if(getLife() >= (int) (getMaxLife() * 0.51)) {
			healthBarId = 1;
		}
		else if(getLife() >= (int) (getMaxLife() * 0.26)) {
			healthBarId = 2;
		}
		else if(getLife() >= (int) (getMaxLife() * 0.01)) {
			healthBarId = 3;
		}
		
		viewport.renderGuiElement(healthBarId, (int) x + 2 * Game.SCALE, (int) y - 6  * Game.SCALE, w, h);
		
		if(hasTarget) {
			if(target instanceof ItemEntity) {
				ItemEntity itemTarget = (ItemEntity) target;
				viewport.renderGuiElement(5, (int) itemTarget.x, (int) itemTarget.y + 5, itemTarget.w, itemTarget.h);
			} else {
				viewport.renderGuiElement(5, (int) target.x, (int) target.y + 5 * 2, target.w, target.h);
			}
			System.out.println(target);
			//viewport.renderGuiElement(6, (int) target.x, (int) target.y - 16, target.w, target.h, ticks, 0.1625f, 0.25f);
		}
	}
	
//	public void render(Viewport viewport) {
//		viewport.render(this);
//		viewport.renderBounds((int) x, (int) y, 16, 16);
//	}
	
	public void handle(World world) {
		x += velX;
		
		if(world.isColliding(this)) {
//			Entity interactable = world.getInteractable(this);
//			if(interactable != null) {
//				System.out.println("Interacting with " + interactable);
//				interactable.interact(world, this);
//			}
			x -= velX;
		}
		
		y += velY;
		
		if(world.isColliding(this)) {
//			Entity interactable = world.getInteractable(this);
//			if(interactable != null) {
//				System.out.println("Interacting with " + interactable);
//				interactable.interact(world, this);
//			}
			y -= velY;
		}
		
		velX = 0;
		velY = 0;
		
		if(input.mouseLeft()) {
			Entity e = world.getEntity(input.getMouseXTranslated(camera.getX()), input.getMouseYTranslated(camera.getY()), 8, 8);
			if(e != null) {
				if(e instanceof ItemEntity) {
					target = (ItemEntity) e;
				}
				else {
					target = e;
				}
				hasTarget = true;
			} else {
				hasTarget = false;
			}
		}
		
		if(hasTarget) {
			if(attacking) {
				if(!target.dead()) {
					int attackTicks = 16;
					if(ticks % Game.DEFAULT_CLIENT_TICKS == attackTicks) {
						int dmg = Attack.shootarrow.getDamageOutput(this, target);
						System.out.println("Attacking target, dealt \"" + dmg + "\" damage");
						world.addEntity(new SteelArrowProjectile(target, x, y, directionFacing, dmg));
					}
				} else {
					attacking = false;
				}
			}
		} else {
			attacking = false;
		}
	}
	
	public boolean solid() {
		return collidable;
	}

	public Camera getCamera() {
		return camera;
	}
}
