package engine.visuals;

import engine.visuals.viewport.Viewport;

public class Sprite {
	
	public static final int MAX_FRAMES_ANIMATION = 8;
	public final int MAX_ROW_INDEX;
	
	private boolean isAnimated = true;
	
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

	public Sprite(SpriteSheet spriteSheet, int rowIndex, int numFrames, int directionMovement, int directionFacing) {
		this.spriteSheet = spriteSheet;
		this.numFrames = numFrames;
		this.directionMovement = directionMovement;
		this.directionFacing = directionFacing;
		MAX_ROW_INDEX = spriteSheet.h / 16;
	}
	
	public void render(Viewport viewport, int x, int y) {
		viewport.render(this, x, y);
	}
	
	public void tick() {
		if(isAnimated) {
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
	
	public void setAnimated(boolean animated) {
		this.isAnimated = animated;
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
	
	public int getCurrentColumnIndex() {
		return columnIndex + currentFrame;
	}
	
	public int getCurrentRowIndex() {
		return rowIndex + directionFacing;
	}
	
	public int getColumnIndex() {
		return columnIndex;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
}
