package engine.visuals.viewport;

/**
 * Simple 2D-camera class to maintain position
 * NOTE: In the future it might be interesting to add more attributes to the camera,
 * and maybe even have a processor-interface for activating visual/post-processing effects on the "lens"
 * @author robot
 *
 */
public class Camera {
	
	private int x, y;
	
	public Camera() {
	}
	
	public Camera(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
