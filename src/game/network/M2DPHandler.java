package game.network;

import java.net.InetAddress;

public interface M2DPHandler {
	public void handle(String msg, InetAddress ip, int port);
}
