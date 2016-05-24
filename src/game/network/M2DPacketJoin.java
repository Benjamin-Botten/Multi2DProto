package game.network;

import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;

public class M2DPacketJoin extends M2DPacket {
	
	private PlayerOnline player;

	public M2DPacketJoin(InetAddress ip, int port, PlayerOnline player) {
		super(ip, port);
		
		this.player = player;
	}
	
	public void send(DatagramSocket socket) {
		
	}
	
	public void recv(DatagramSocket socket) {
		
	}
	
}
