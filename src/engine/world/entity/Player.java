package engine.world.entity;

import com.sun.glass.events.KeyEvent;

import engine.io.Input;
import engine.visuals.Sprite;
import engine.visuals.SpriteSheet;
import engine.world.World;

public class Player extends Entity {
	
	private Input input;
	
	public Player(Input input) {
		super();
		
		this.input = input;
		sprite = new Sprite(SpriteSheet.entities, 0, 2, 0, 0);
	}
	
	public void tick() {
		super.tick();
		
		sprite.tick();
		
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
		
		sprite.setDirectionMovement(directionMovement);
		sprite.setDirectionFacing(directionFacing);
	}
	
	public void handle(World world) {
		x += velX;
		
		if(world.isColliding(this)) {
			Entity interactable = world.getInteractable(this);
			if(interactable != null) {
				System.out.println("Interacting with " + interactable);
				interactable.interact(world, this);
			}
			x -= velX;
		}
		
		y += velY;
		
		if(world.isColliding(this)) {
			Entity interactable = world.getInteractable(this);
			if(interactable != null) {
				System.out.println("Interacting with " + interactable);
				interactable.interact(world, this);
			}
			y -= velY;
		}
		
		velX = 0;
		velY = 0;
		
//		if(interactable instanceof ItemEntity) {
//			ItemEntity item = (ItemEntity) interactable;
//			item.interact(this);
//			world.removeEntity(interactable);
//		}
	}
	
	public boolean solid() {
		return collidable;
	}
}
