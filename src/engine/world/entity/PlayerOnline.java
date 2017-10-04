package engine.world.entity;

import java.net.InetAddress;

import engine.world.entity.action.Action;
import engine.world.entity.action.ActionQueue;

/**
 * The online data-abstraction of the player
 * @author robot
 *
 */
public class PlayerOnline {

	private InetAddress ip;
	private int port;
	private int portUDP;
	private boolean connected = false;
	
	public final String username;
	
	public PlayerOnline(String username) {
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
	
	public int getPortUDP() {
		return portUDP;
	}
	
	public InetAddress getAddress() {
		return ip;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public void setAddress(InetAddress address) {
		this.ip = address;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setPortUDP(int portUDP) {
		this.portUDP = portUDP;
	}
	
	public boolean isConnected() {
		return connected;
	}
}
