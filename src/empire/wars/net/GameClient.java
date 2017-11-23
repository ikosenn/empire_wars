package empire.wars.net;
import empire.wars.ConnectedPlayers;
import empire.wars.EmpireWars;
import empire.wars.net.packets.LoginPacket;
import empire.wars.net.packets.Packet;
import empire.wars.net.packets.Packet.PacketTypes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;



public class GameClient extends Thread {
	
	private InetAddress ipAddress;
	private DatagramSocket socket;
	private EmpireWars game;
	
	public GameClient(EmpireWars game, String ipAddress) {
		this.game = game;
		try {
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}


	public void run() {
		while(true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data,data.length); 
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			/*String message = new String(packet.getData());
			System.out.println("Server: "+ message);*/
			
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		LoginPacket packet  = null;
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPackets(message.substring(0,2));
		System.out.println(type);
		switch(type) {
		case LOGIN:
			packet = new LoginPacket(data);
			System.out.println(address.getHostAddress() + " " +((LoginPacket) packet).getUsername() + "has joined the game");
			new ConnectedPlayers(100,100,((LoginPacket) packet).getUsername(), address, port);
			//add the player.
			break;
		case DISCONNECT: 
			break;
		default:
			break;
		}
		
	}


	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1323);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
