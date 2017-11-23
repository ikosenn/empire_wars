package empire.wars.net.packets;

import empire.wars.net.GameClient;
import empire.wars.net.GameServer;

public class LoginPacket extends Packet{

	private String username;
	
	public LoginPacket(byte[] data) {
		super(00);
		this.username = readData(data);
	}

	public LoginPacket(String username) {
		super(00);
		this.username = username;
	}
	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public byte[] getData() {
		return ("00" + this.username).getBytes();
	}

	public String getUsername() {
		return username;
	}

}
