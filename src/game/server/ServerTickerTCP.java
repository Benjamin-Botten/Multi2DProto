package game.server;

/**
 * 
 * @author robot
 *
 */
public class ServerTickerTCP implements Runnable {
	
	private ServerTCP server;
	
	public ServerTickerTCP(ServerTCP server) {
		this.server = server;
	}
	
	public void broadcastMessage(String msg) {
		for(ClientConnectionTCP connection : server.getConnections()) {
			//connection.sendMessage("< ServerTicker's Broadcaster! >");
		}
	}

	public void start() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {

		long startTime = System.currentTimeMillis();
		
		while(!server.isClosed()) {
			if(System.currentTimeMillis() - startTime > 500) {
				broadcastMessage("Ananas!");
				startTime = System.currentTimeMillis();
			}
		}
	}
}
