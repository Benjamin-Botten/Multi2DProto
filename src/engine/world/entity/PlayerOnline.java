package engine.world.entity;

import java.net.InetAddress;

/**
 * The online abstraction of player
 * @author robot
 *
 */
public class PlayerOnline extends Player {

	private InetAddress ip;
	private int port;
	private boolean connected = false;
	
	public final String username;
	
	
	public PlayerOnline(String username) {
		super(null);
		
		this.username = username;
	}
	
	public PlayerOnline(String username, InetAddress ip, int port) {
		this(username);
		this.ip = ip;
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	
	public InetAddress getAddress() {
		return ip;
	}
	
	public String getUsername() {
		return username;
	}

	public boolean isConnected() {
		return connected;
	}
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
