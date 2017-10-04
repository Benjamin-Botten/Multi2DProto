package engine.util;

/**
 * 2-dimensional rectangle
 * @author robot
 *
 */
public class Rect2D {
	public float x, y;
	private int w, h;
	
	public Rect2D(float x, float y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	/** Setters */
	
	
	public void setWidth(int w) {
		this.w = Math.abs(w);
	}
	
	public void setHeight(int h) {
		this.h = Math.abs(h);
	}
	
	/** Getters */
	
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
	
	public boolean isColliding(Rect2D bounds) {
		return isColliding(this, bounds);
	}
	
	public boolean isColliding(float x, float y, int w, int h) {
		return isColliding(this.x, this.y, this.w, this.h, x, y, w, h);
	}
	
	public static boolean isColliding(float x0, float y0, int w0, int h0, float x1, float y1, int w1, int h1) {
		if(x0 >= x1 + w1) return false; //one right of the other
		if(x0 + w0 <= x1) return false; //one left of the other
		if(y0 >= y1 + h1) return false; //one beneath the other
		if(y0 + h0 <= y1) return false; //one above the other
		
		return true; //one is somewhat inside the other
	}
	
	public static boolean isColliding(Rect2D bounds0, Rect2D bounds1) {
		return isColliding
				(
				bounds0.x, bounds0.y, bounds0.getWidth(), bounds0.getHeight(),
				bounds1.x, bounds1.y, bounds1.getWidth(), bounds1.getHeight()
				);
	}
}
