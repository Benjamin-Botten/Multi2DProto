package game.network;

import java.net.DatagramSocket;
import java.net.InetAddress;

import engine.world.entity.PlayerOnline;

public class M2DPacketUpdatePosition extends M2DPacket {

	public M2DPacketUpdatePosition(InetAddress ip, int port) {
		super(ip, port);
	}

	public void send(PlayerOnline player, DatagramSocket socket) {
		
	}
}
