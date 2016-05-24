package engine.world.entity;

import java.net.InetAddress;

public class PlayerOnline {

	private Player player;
	private InetAddress ip;
	private int port;
	
	public PlayerOnline(Player player, InetAddress ip, int port) {
		this.player = player;
		this.ip = ip;
		this.port = port;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public int getPort() {
		return port;
	}
	
	public InetAddress getAddress() {
		return ip;
	}
}
