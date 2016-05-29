package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import game.client.GameClient;

public class M2DPacket {
	private static final int MAX_PACKET_TYPES = 256;
	
	private int id;
	
	protected int port;
	protected InetAddress ip; //ip_dst
	protected DatagramPacket packet;
	protected String reply = ""; //String for storing the reply in case the packet has an implemented recv method
	
	public static final M2DPacket[] packets = new M2DPacket[MAX_PACKET_TYPES];
	
	public static final M2DPacket join = new M2DPacketJoin(M2DProtocol.M2DP_JOIN);
	public static final M2DPacket disconnect = new M2DPacketDisconnect(M2DProtocol.M2DP_DISCONNECT);
	public static final M2DPacket updatePlayerPosition = new M2DPacketUpdatePosition(M2DProtocol.M2DP_UPDATE_POSITION);
	public static final M2DPacket updatePlayerSprite = new M2DPacketUpdateSprite(M2DProtocol.M2DP_UPDATE_SPRITE);
	
	public M2DPacket(int id) {
		if(packets[id] != null) {
			throw new IllegalArgumentException("Attempted to create new packet type with conflicting id (already exists)");
		}
		packets[id] = this;
		this.id = id;
	}
	
	public void send(DatagramSocket socket, InetAddress dst, int port) {
	}
	
	public void recv(DatagramSocket socket) {
	}
	
	public M2DPacket parse(M2DProtocol m2dp) {
		switch(m2dp.getDataId()) {
		case M2DProtocol.M2DP_JOIN:
			break;
		case M2DProtocol.M2DP_DISCONNECT:
			break;
		case M2DProtocol.M2DP_UPDATE_POSITION:
			break;
		case M2DProtocol.M2DP_UPDATE_SPRITE:
			break;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return String reply
	 */
	public String getReply() {
		return reply;
	}
	
	public DatagramPacket getPacket() {
		return packet;
	}
}
