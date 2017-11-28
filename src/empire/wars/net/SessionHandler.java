package empire.wars.net;

import empire.wars.EmpireWars;

/**
 * Handle connection message.
 * The client broadcasts CONNECTION messages.
 * Once they are received by the server the server 
 * add the client to the list of connectedPlayers and 
 * sends back a message to the client so the client can know 
 * where the server is.
 * Here "SERVER" refers to the central authority that 
 * send packets to everyone else.
 * 
 * @author peculiaryak
 *
 */
public class SessionHandler {
	private Message msg;
	private EmpireWars game;
	
	public SessionHandler(Message msg, EmpireWars game) {
		this.msg = msg;
		this.game = game;
		this.handle();
	}
	
	
	private void handle() {
		ConnectedPlayers player = new ConnectedPlayers(
			msg.getMsg(), msg.getIpAddress(), msg.getPort()
		);
		// if you are a client and you don't yet have a server
		if (game.getSessionType().equals("CLIENT") && game.getBroadcastServer() == null) {
			// set broadcast server
			game.setBroadcastServer(player);
		} else if (game.getSessionType().equals("SERVER")) {
			// append the client to connected clients
			game.appendConnectedPlayers(player);
			// construct message for client
			Message connectRes = new Message(game.getUsername(), "CONNECT");
			game.appendSendPackets(connectRes);
		}
	}
}
