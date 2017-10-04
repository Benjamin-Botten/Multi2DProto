package engine.world.entity.npc;

import engine.world.entity.Entity;

public class NPC extends Entity {
	
	protected float walkRadius;
	protected float xMarker, yMarker; //'Move To'-marker
	protected long timeSinceMoved;
	protected long timeMoved;
	protected boolean reachedMarker;
	protected long attackDelay;

	protected float xOrigin, yOrigin;
	
	public NPC() {
		super();
	}
	
	public NPC(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
