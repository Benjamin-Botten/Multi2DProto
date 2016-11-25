package game.network;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class M2DProtocol {

	// String representations of the data/msg broadcasts
	public static final String M2DP_BROADCAST_UPDATE_POSITION = "0005";
	public static final String M2DP_BROADCAST_UPDATE_SPRITE = "0006";

	// String representations of the data/msg replies
	public static final String M2DP_REPLY_JOIN_ACCEPT = "0001";
	public static final String M2DP_REPLY_JOIN_DENY = "0002";
	public static final String M2DP_REPLY_DISCONNECT_ACCEPT = "0003";
	public static final String M2DP_REPLY_DISCONNECT_DENY = "0004";

	// String representation of the data/msg identifiers for transmissions
	public static final String M2DP_DATA_JOIN = "0001";
	public static final String M2DP_DATA_DISCONNECT = "0002";
	public static final String M2DP_DATA_UPDATE_PLAYER = "0003";

	// M2D-protocol data/msg identifiers
	public static final int M2DP_JOIN = 1;
	public static final int M2DP_DISCONNECT = 2;
	public static final int M2DP_UPDATE_PLAYER = 3;
	public static final int M2DP_JOIN_REPLY = 4;
	public static final int M2DP_DISCONNECT_REPLY = 5;

	// Protocol message-indexes for message-content (i.e. where the data-id
	// starts and ends in the message, and such)
	public static final int M2DP_DATA_ID_POS_START = 0;
	public static final int M2DP_DATA_ID_POS_END = 4; // 4 bytes
	public static final int M2DP_DATA_MSG_LENGTH_POS_START = 4;
	public static final int M2DP_DATA_MSG_LENGTH_POS_END = 6;

	private int dataId;
	private int dataLength;
	private String data;
	private InetAddress ip;
	private int port;
	
	public M2DProtocol(int dataId, int dataLength, String data, InetAddress ip, int port) {
		this.dataId = dataId;
		this.dataLength = dataLength;
		this.data = data;
		this.ip = ip;
		this.port = port;
	}
	
	/**
	 * 
	 * @param reply
	 * @return M2DPacket type that the parser finds
	 */
	public static M2DProtocol parseUDP(DatagramPacket packet) {
		InetAddress ip = packet.getAddress();
		int port = packet.getPort();
		String msg = new String(packet.getData());
		int dataId = Integer.parseInt(msg.substring(M2DP_DATA_ID_POS_START, M2DP_DATA_ID_POS_END));
		int dataLength = Integer.parseInt(msg.substring(M2DP_DATA_MSG_LENGTH_POS_START, M2DP_DATA_MSG_LENGTH_POS_END));
		int startIndex = M2DP_DATA_MSG_LENGTH_POS_END;
		int endIndex = startIndex + dataLength;
		String data = msg.substring(startIndex, endIndex);
		
		return new M2DProtocol(dataId, dataLength, data, ip, port);
	}
	
	/**
	 * Alternative to parsing the packet itself, this takes the packet's data field
	 * @param msg
	 * @return
	 */
	public static M2DProtocol parseMessage(String msg, InetAddress ip, int port) {
		int dataId = Integer.parseInt(msg.substring(M2DP_DATA_ID_POS_START, M2DP_DATA_ID_POS_END));
		int dataLength = Integer.parseInt(msg.substring(M2DP_DATA_MSG_LENGTH_POS_START, M2DP_DATA_MSG_LENGTH_POS_END));
		int startIndex = M2DP_DATA_MSG_LENGTH_POS_END;
		int endIndex = startIndex + dataLength;
		String data = msg.substring(startIndex, endIndex);
		
		return new M2DProtocol(dataId, dataLength, data, ip, port);
	}
	
	public static M2DProtocol parseTCP(String msg, InetAddress ip, int port) {
		int dataId = Integer.parseInt(msg.substring(M2DP_DATA_ID_POS_START, M2DP_DATA_ID_POS_END));
		int dataLength = Integer.parseInt(msg.substring(M2DP_DATA_MSG_LENGTH_POS_START, M2DP_DATA_MSG_LENGTH_POS_END));
		int startIndex = M2DP_DATA_MSG_LENGTH_POS_END;
		int endIndex = startIndex + dataLength;
		String data = msg.substring(startIndex, endIndex);
		
		return new M2DProtocol(dataId, dataLength, data, ip, port);
	}
	
	public String getData() {
		return data;
	}
	
	public int getDataLength() {
		return dataLength;
	}
	
	public int getDataId() {
		return dataId;
	}
	
	public int getPort() {
		return port;
	}
	
	public InetAddress getAddress() {
		return ip;
	}
}
