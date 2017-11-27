package empire.wars;

import java.net.InetAddress;

public class ConnectedPlayers extends Player{
	
	public InetAddress ipAddress;
	public int port;
	
	public ConnectedPlayers(float x, float y,String username, InetAddress ipAddress, int port) {
		super(x,y,username);
		this.ipAddress = ipAddress;
		this.port = port;
	}
}
