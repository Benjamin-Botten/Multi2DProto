package engine.visuals;

import engine.visuals.viewport.Viewport;

/**
 * TODO: Add constants to enumerate sprite locations, e.g. ID_ROW_ENTITY_NEO_RUNNING, etc
 * 
 * @author robot
 *
 */
public class Sprite {
	
	public static final int MAX_FRAMES_ANIMATION = 2;
	
	private final boolean animated;
	
	/**
	 * Based on the logical update rate of the server (tick rate)
	 * Typically the server will be either 24, 32 or 64.
	 */
	private int ticks = 1;
	private int rtt = 20; //round-trip-time measured in amount of ticks
	private int numFrames = MAX_FRAMES_ANIMATION;
	private int currentFrame = 0;
	private int rowIndex = 0; //row to clip from in the spritesheet
	private int columnIndex = 0;
	
	private int directionMovement;
	private int directionFacing;
	
	private SpriteSheet spriteSheet;

	public Sprite(SpriteSheet spriteSheet, int rowIndex, int columnIndex, boolean animated) {
		this.spriteSheet = spriteSheet;
		this.rowIndex = rowIndex;
		this.numFrames = MAX_FRAMES_ANIMATION;
		this.animated = animated;
	}
	
	public Sprite(SpriteSheet spriteSheet, int rowIndex, int columnIndex, int directionMovement, int directionFacing, boolean animated) {
		this.spriteSheet = spriteSheet;
		this.rowIndex = rowIndex;
		this.numFrames = MAX_FRAMES_ANIMATION;
		this.directionMovement = directionMovement;
		this.directionFacing = directionFacing;
		this.animated = animated;
	}
	
	public void render(Viewport viewport, int x, int y) {
		viewport.render(this, x, y);
	}
	
	public void tick() {
		if(animated) {
			if(ticks % rtt == 0) {
				currentFrame++; 
				if(currentFrame >= numFrames) {
					currentFrame = 0;
				}
			}
		}
		
		ticks++;
	}
	
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	
	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}
	
	public void setDirectionMovement(int directionMovement) {
		this.directionMovement = directionMovement;
	}
	
	public void setDirectionFacing(int directionFacing) {
		this.directionFacing = directionFacing;
	}
	
	public void setNumFrames(int numFrames) {
		if(numFrames < 0) {
			numFrames = 0;
			return;
		}
		if(numFrames > MAX_FRAMES_ANIMATION) {
			numFrames = MAX_FRAMES_ANIMATION; 
			return;
		}
		this.numFrames = numFrames;
	}
	
	public void setRTT(int rtt) {
		if(rtt < 0) {
			rtt = 0;
		}
		this.rtt = rtt;
	}
	
	public int getDirectionMovement() {
		return directionMovement;
	}
	
	public int getDirectionFacing() {
		return directionFacing;
	}
	
	public int getCurrentFrame() {
		return currentFrame;
	}
	
	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}
	
	/**
	 * Column / x-position in spritesheet changes the animation of the current direction the sprite is facing by adding the current frame of animation
	 * @return the current column index in the animation of the sprite
	 */
	public int getCurrentColumnIndex() {
		return columnIndex + currentFrame;
	}
	
	
	/**
	 * Row / y-position in spritesheet changes the current direction the sprite is facing by adding the facing direction to the row index of the current sprite
	 * @return the current row index in the animation of the sprite
	 */
	public int getCurrentRowIndex() {
		return rowIndex + directionFacing;
	}
	
	/**
	 * Get initial column index
	 * @return int, representing the initial column index
	 */
	public int getColumnIndex() {
		return columnIndex;
	}
	
	/**
	 * Get initial row index
	 * @return int, representing the initial row index
	 */
	public int getRowIndex() {
		return rowIndex;
	}
	
	/**
	 * Get animation state of this sprite
	 * @return boolean, based on the sprite being animated or not
	 */
	public boolean getAnimated() {
		return animated;
	}
}
