package empire.wars.net;

import java.net.InetAddress;

public class ConnectedPlayers {
	
	private InetAddress ipAddress;
	private int port;
	private String username;
	
	public ConnectedPlayers(String username, InetAddress ipAddress, int port) {
		this.setUsername(username);
		this.setIpAddress(ipAddress);
		this.setPort(port);
	}

	/*
	 * ipAddress getter
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	/*
	 * ipAddress setter
	 */
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * port getter
	 */
	public int getPort() {
		return port;
	}

	/*
	 * port setter
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/*
	 * Username getter
	 */
	public String getUsername() {
		return username;
	}

	/*
	 * username setter
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
