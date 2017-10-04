package game.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import engine.world.entity.Player;
import engine.world.entity.PlayerOnline;
import game.network.M2DProtocol;
import game.network.packet.M2DPacket;

/**
 * 
 * @author robot
 *
 */

public class ClientConnectionTCP implements Runnable {

	private Socket connection;
	private boolean connected;
	private static int connections;
	private int id;
	private PrintWriter writer;
	private GameServer gameServer;

	public ClientConnectionTCP(Socket connection, GameServer gameServer) {
		this.connection = connection;
		this.gameServer = gameServer;

		try {
			writer = new PrintWriter(new BufferedOutputStream(connection.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (connection.isConnected()) {
			connected = true;
		}

		id = connections++;
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		long startTime = System.currentTimeMillis();

		while (connected) {
			// getServerInput();
			if (connection.isClosed() || !connection.isConnected()) {
				connected = false;
				--connections;
				break;
			}

			try {
				String line = "";
				
				line = reader.readLine();
				if (line == null)
					break;
				System.out.println("From Client " + id + "> " + line);

				if (line.contains(M2DProtocol.M2DP_DATA_JOIN)) {
					System.out.println("Message from client asked to join, sending response");
					M2DProtocol m2dp = M2DProtocol.parseTCP(line, connection.getInetAddress(), connection.getPort());
					Player plr = new Player(m2dp.getData(), connection.getInetAddress(), connection.getPort());
					plr.setNetId(gameServer.netIdPool.allocatePlayer(plr));
					gameServer.addPlayer(plr);
					sendMessage(M2DProtocol.M2DP_REPLY_JOIN_ACCEPT + "," + plr.getNetId());
				}
				if (line.contains(M2DProtocol.M2DP_DATA_DISCONNECT)) {
					M2DProtocol m2dp = M2DProtocol.parseTCP(line, connection.getInetAddress(), connection.getPort());
					String username = m2dp.getData();
					System.out.println("Disconnecting player '" + username + "' with netId " + gameServer.getPlayerByName(username).getNetId());
					gameServer.netIdPool.deallocatePlayer(gameServer.getPlayerByName(username));
					gameServer.disconnectPlayerByName(m2dp.getData());
					sendMessage(M2DProtocol.M2DP_REPLY_DISCONNECT_ACCEPT);
					connection.close();
				}
//				if (line.equalsIgnoreCase("/quit") || line.equalsIgnoreCase("/exit")
//						|| line.equalsIgnoreCase("/stop")) {
//					connection.close();
//				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			if (System.currentTimeMillis() - startTime > 500) {
			}
		}

	}

	public void sendMessage(String msg) {
		writer.println(msg);
		writer.flush();
	}
	
	public boolean isConnected() {
		return connected;
	}
}
