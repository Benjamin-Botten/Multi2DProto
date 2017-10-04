package engine.visuals;

import java.util.Random;

import engine.visuals.viewport.Viewport;

public class FloaterText {
	private long timeSpawned;
	private long fadeDelay;
	private String message;
	private float x, y;
	private float xOrigin, yOrigin;
	private int color;
	private float dissipation;
	private float angle = 0;
	private int direction;
	
	public FloaterText(String message, float x, float y, int color, long fadeDelay) {
		this.message = message;
		this.x = x;
		this.y = y;
		this.xOrigin = x;
		this.yOrigin = y;
		this.color = color;
		this.fadeDelay = fadeDelay;
		this.timeSpawned = System.currentTimeMillis();
		this.direction = new Random().nextInt(2);
	}
	
	public void tick() {
		Random random = new Random();
		if(y - yOrigin < 16) {
			if(direction == 0) {
				x -= Math.cos(angle) * (1 + random.nextInt(2));
			}
			else { 
				x += Math.cos(angle) * (1 + random.nextInt(2));
			}
			y -= Math.sin(angle) * (2 + random.nextInt(5));
		}
		angle -= 0.1f;
		
		dissipation = 1.f - ((System.currentTimeMillis() - timeSpawned) / (float)fadeDelay);
	}
	
	public void render(Viewport viewport) {
		viewport.render(message, (int) x, (int) y, Color.getColorMultipliedARGB(color, dissipation));
	}
	
	public boolean removable() {
		return System.currentTimeMillis() - timeSpawned >= fadeDelay ? true : false;
	}
}
