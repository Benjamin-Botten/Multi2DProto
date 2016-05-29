package game.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import engine.world.entity.PlayerOnline;
import game.network.M2DPHandler;
import game.network.M2DPacket;
import game.network.M2DPacketJoin;
import game.network.M2DPacketUpdatePosition;
import game.network.M2DProtocol;

public class ServerRequest extends Thread implements M2DPHandler {
	
	private String msg;
	private int port;
	private InetAddress ip;
	private GameServer gameServer;
	private DatagramPacket packet;
	
	public ServerRequest(GameServer gameServer, DatagramPacket packet) {
		this.gameServer = gameServer;
		this.packet = packet;
		
		msg = new String(packet.getData());
		port = packet.getPort();
		ip = packet.getAddress();
	}
	
	/**
	 * 
	 * @param msg
	 * @return byte[] buffer of the data to send back to client
	 */
	private byte[] handleMessage(String msg, InetAddress ip, int port) {
		M2DProtocol m2dp = M2DProtocol.parseMessage(msg, ip, port);
		M2DPacket packet = M2DPacket.packets[m2dp.getDataId()];
		packet.parse();
//		if(packet instanceof M2DPacketJoin) {
//			((M2DPacketJoin) packet).parse();
//		}
//		if(packet instanceof M2DPacketUpdatePosition) {
//			PlayerOnline player = getPlayerByName(packet.getName());
//			((M2DPacketUpdatePosition) packet).setPlayer(player);
//		}
//		switch(m2dp.getDataId()) {
//		case M2DProtocol.M2DP_JOIN:
//			//Check that connection doesn't already exist!
//			return M2DProtocol.M2DP_REPLY_JOIN_ACCEPT.getBytes();
//			
//		case M2DProtocol.M2DP_DISCONNECT:
//			//Check that connection is alive. Validate connection?
//			return M2DProtocol.M2DP_REPLY_DISCONNECT_ACCEPT.getBytes();
//			
//		case M2DProtocol.M2DP_UPDATE_POSITION:
//			//Extract the values and broadcast the data to other connections
//			M2DPacketUpdatePosition updatePosition = M2DPacket.parse(m2dp);
//			break;
//			
//		case M2DProtocol.M2DP_UPDATE_SPRITE:
//			//Extract the values and broadcast the data to other connections
//			break;
//		}
		return null;
	}
	
	public void broadcastPlayerPositions(String msg, InetAddress ip, int port) {
		try {
			List<PlayerOnline> players = gameServer.getPlayers();
			for(int i = 0; i < players.size(); ++i) {
				if(players.get(i).getAddress().equals(ip) && players.get(i).getPort() == port) continue;
				byte[] buf = msg.getBytes();
				packet = new DatagramPacket(buf, buf.length, players.get(i).getAddress(), players.get(i).getPort());
				System.out.println("Sending packet: \"" + msg + "\", " + players.get(i).getAddress().toString() + ", " + players.get(i).getPort());
				gameServer.getSocket().send(packet);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void broadcastPlayerSprites(String msg, InetAddress ip, int port) {
		try {
			List<PlayerOnline> players = gameServer.getPlayers();
			for(int i = 0; i < players.size(); ++i) {
				if(players.get(i).getAddress().equals(ip) && players.get(i).getPort() == port) continue;
				byte[] buf = msg.getBytes();
				packet = new DatagramPacket(buf, buf.length, players.get(i).getAddress(), players.get(i).getPort());
				System.out.println("Sending packet: \"" + msg + "\", " + players.get(i).getAddress().toString() + ", " + players.get(i).getPort());
				gameServer.getSocket().send(packet);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		handleMessage(msg, ip, port);
	}

	@Override
	public void handle(String msg, InetAddress ip, int port) {
		
	}
	
	
}
