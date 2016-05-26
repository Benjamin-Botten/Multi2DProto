package game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class M2DPacket {
	protected int port;
	protected InetAddress ip; //ip_dst
	protected DatagramPacket packet;
	protected String reply = ""; //String for storing the reply in case the packet has an implemented recv method
	
	public M2DPacket(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public void send(DatagramSocket socket) {
	}
	
	public void recv(DatagramSocket socket) {
	}
	
	/**
	 * 
	 * @return String reply
	 */
	public String getReply() {
		return reply;
	}
	
}
