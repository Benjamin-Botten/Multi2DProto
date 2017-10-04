package game.server;

import engine.world.entity.Entity;
import engine.world.entity.action.Action;
import engine.world.entity.action.ActionHandler;

public class ServerActionHandler {
	
	private GameServer gameServer;
	
	public ServerActionHandler(GameServer gameServer) {
		this.gameServer = gameServer;
	}
	
	public int handle(Entity src, Entity dst, Action action) {
		if(dst == null) return Action.noAction.getId();
		
		//System.out.print("Handling action on server-side");
		if(action == Action.noAction) {
			//System.out.println(", " + action.toString());
		}
		else if(action == Action.weaponAttack) {
			System.out.println(", " + action.toString());
			if(src.getActionProgress() >= 100) {
				dst.setLife(dst.getLife() - 400);
			}
			
			return Action.weaponAttack.getId();
		} else if(action == Action.frostbite) {
			if(!src.isCasting()) {
				src.setCastTime(System.currentTimeMillis());
				src.setIsCasting(true);
				System.out.println("Casting Frostbite");
			}
		}
		
		return Action.noAction.getId();
	}
}
