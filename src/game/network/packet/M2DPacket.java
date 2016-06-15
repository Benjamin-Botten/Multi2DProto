package game.network.packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import game.client.GameClient;
import game.network.M2DProtocol;
import game.network.parcel.M2DParcel;
import game.network.parcel.M2DParcelDisconnect;
import game.network.parcel.M2DParcelJoin;
import game.network.parcel.M2DParcelUpdatePlayer;

public class M2DPacket {
	private static final int MAX_PACKET_TYPES = 256;
	
	public final int id;
	
	protected int port;
	protected InetAddress ip; //ip_dst
	protected DatagramPacket packet;
	protected String reply = ""; //String for storing the reply in case the packet has an implemented recv method
	
	public static final M2DPacket[] packets = new M2DPacket[MAX_PACKET_TYPES];
	
	public static final M2DPacketJoin join = new M2DPacketJoin(M2DProtocol.M2DP_JOIN, new M2DParcelJoin());
	public static final M2DPacketDisconnect disconnect = new M2DPacketDisconnect(M2DProtocol.M2DP_DISCONNECT, new M2DParcelDisconnect());
	public static final M2DPacketUpdatePlayer updatePlayer = new M2DPacketUpdatePlayer(M2DProtocol.M2DP_UPDATE_PLAYER, new M2DParcelUpdatePlayer());
	public static final M2DPacketJoinReply joinReply = new M2DPacketJoinReply(M2DProtocol.M2DP_JOIN_REPLY);
	public static final M2DPacketDisconnectReply disconnectReply = new M2DPacketDisconnectReply(M2DProtocol.M2DP_DISCONNECT_REPLY, new M2DParcelDisconnect());
	
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
	
	public void parse(M2DProtocol m2dp) {
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
