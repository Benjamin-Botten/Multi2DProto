package engine.world.entity;

import com.sun.glass.events.KeyEvent;

import engine.io.Input;

public class Player extends Entity {
	
	private Input input;
	
	private boolean connected = false;
	public final String name;
	
	public Player(Input input, int color, String name) {
		super();
		
		this.input = input;
		this.color = color;
		this.name = name;
	}
	
	public void tick() {
		if(input.keys[KeyEvent.VK_A]) {
			x -= 1;
		}
		if(input.keys[KeyEvent.VK_D]) {
			x += 1;
		}
	}
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public boolean isConnected() {
		return connected;
	}
}
