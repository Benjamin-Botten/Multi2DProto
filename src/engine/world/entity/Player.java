package engine.world.entity;

import com.sun.glass.events.KeyEvent;

import engine.io.Input;
import engine.visuals.Sprite;
import engine.visuals.SpriteSheet;

public class Player extends Entity {
	
	private Input input;
	
	public Player(Input input) {
		super();
		
		this.input = input;
		sprite = new Sprite(SpriteSheet.entities, 0, 2, 0, 0);
	}
	
	public void tick() {
		sprite.tick();
		
		if(input.keys[KeyEvent.VK_A]) {
			directionMovement = DIR_LEFT;
			directionFacing = DIR_LEFT;
			x -= 1;
		}
		if(input.keys[KeyEvent.VK_D]) {
			directionMovement = DIR_RIGHT;
			directionFacing = DIR_RIGHT;
			x += 1;
		}
		if(input.keys[KeyEvent.VK_W]) {
			directionMovement = DIR_UP;
			directionFacing = DIR_UP;
			y -= 1;
		}
		if(input.keys[KeyEvent.VK_S]) {
			directionMovement = DIR_DOWN;
			directionFacing = DIR_DOWN;
			y += 1;
		}
		
		sprite.setDirectionMovement(directionMovement);
		sprite.setDirectionFacing(directionFacing);
	}
	
	public boolean solid() {
		return collidable;
	}
}
