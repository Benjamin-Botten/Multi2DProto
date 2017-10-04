package game.client;

import engine.world.entity.Entity;
import engine.world.entity.action.Action;
import engine.world.entity.action.ActionHandler;

public class ClientActionHandler implements ActionHandler {

	@Override
	public int handle(Entity src, Entity dst, Action action) {
		return 0;
	}
	
}
