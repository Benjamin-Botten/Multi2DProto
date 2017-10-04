package engine.world.entity.action;

import engine.world.entity.Entity;
import game.server.GameServer;

public interface ActionHandler {
	public int handle(Entity src, Entity dst, Action action);
}
