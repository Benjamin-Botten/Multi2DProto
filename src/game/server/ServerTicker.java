package game.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import engine.world.entity.AttackEntity;
import engine.world.entity.FrostbiteEntity;
import engine.world.entity.Player;
import engine.world.entity.action.Action;
import game.network.packet.M2DPacket;
import game.network.packet.M2DPacketUpdatePlayer;

public class ServerTicker implements Runnable {
	
	private GameServer gameServer;
	private int ticks = 0;
	private long timer;
	
	public ServerTicker(GameServer gameServer) {
		this.gameServer = gameServer;
	}
	
	/**
	 * Tick the server-state world, entities, etc
	 * Tickrate should be fixed to a constant 
	 */
	public void tick() {
		List<Player> players = gameServer.getPlayers();
		List<AttackEntity> attackEntities = gameServer.getAttackEntities();
		for(int i = 0; i < players.size(); ++i) {
//			gameServer.getPlayers().get(i).tick();
			
			Player player = players.get(i);
			
//			gameServer.serverActionHandler.handle(player, player.getTarget(), player.getCurrentAction());
	
			if(player.getActionProgress() >= 100) {
//				attackEntities.add(new FrostbiteEntity(player, player.getTarget(), gameServer.netIdPool.allocateAttackEntity(), (int) player.x, (int) player.y));
				
				player.setActionProgress(0);
				player.setCurrentAction(Action.noAction);
				player.setIsCasting(false);
				
				System.out.println("Adding AttackEntity for action & setting action to No Action");
			}
			
			for(int j = 0; j < players.size(); ++j) {
				M2DPacket.updatePlayer.send(gameServer.getSocket(), players.get(i).getPlayerOnline().getAddress(), players.get(i).getPlayerOnline().getPortUDP(), players.get(j));
			}
			
			if(player.getCurrentAction() == Action.frostbite) {
				long timeSinceCast = System.currentTimeMillis() - player.getCastTime();
				int actionProgress = (int) (((float) timeSinceCast / player.getCurrentAction().getCastTime()) * 100.f);
				player.setActionProgress(actionProgress);
				
//				System.out.println("Action Progress in ServerTicker > " + actionProgress);
			}
		}
		
		for(int i = 0; i < attackEntities.size(); ++i) {
			attackEntities.get(i).tick();
		}
		++ticks;
	}
	
	public void run() {
		//Set the timer of the start of this ticker instance
		timer = System.currentTimeMillis(); 
		
		long startTime = System.currentTimeMillis();
		long timer = startTime;
		long tickTime = 0;
		int frames = 0, ticks = 0;
		long ticksPerSecond = 1000 / GameServer.MAX_TICKS;
		long deltaTime = System.currentTimeMillis();
		long frameTime = System.currentTimeMillis();
		
		while(gameServer.isRunning()) {
			
			deltaTime = System.currentTimeMillis() - frameTime;
			
			if(deltaTime >= ticksPerSecond) {
				tick();
				
				frameTime = System.currentTimeMillis();
			}
			
			if(System.currentTimeMillis() - timer > 1000) {
				
//				System.out.println("GameServer FPS > " + frames + "| TPS > " + this.ticks);
				
				this.ticks = 0;
				timer += 1000;
				frames = 0;
			} else {
				frames++;
			}
		}
	}
}
