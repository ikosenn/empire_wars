package empire.wars.net;

import empire.wars.*;
import empire.wars.net.packets.LoginPacket;
import empire.wars.net.packets.Packet;
import empire.wars.net.packets.Packet.PacketTypes;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread {
	
	private DatagramSocket socket;
	private EmpireWars game;
	
	private List<ConnectedPlayers> connectedPlayers = new ArrayList<ConnectedPlayers>();
	
	public GameServer(EmpireWars game) {
		this.game = game;
		try {
			this.socket = new DatagramSocket(1323);
		} catch (SocketException e) {
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
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		LoginPacket packet  = null;
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPackets(message.substring(0,2));
		switch(type) {
		case LOGIN:
			packet = new LoginPacket(data);
			System.out.println(address.getHostAddress() + " " +((LoginPacket) packet).getUsername() + " has connected");
			ConnectedPlayers player =  new ConnectedPlayers(300,300,((LoginPacket) packet).getUsername(), address, port);
			this.addConnection(player,(LoginPacket) packet);
			break;
		case DISCONNECT: 
			break;
		default:
			break;
		}
			
	}

	public void addConnection(ConnectedPlayers player, LoginPacket packet) {
		boolean CheckConnected = false;
		for(ConnectedPlayers p : this.connectedPlayers) {
			if(player.getUserName().equalsIgnoreCase(p.getUserName())) {
				if(p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}
				if(p.port == -1) {
					p.port = player.port;
				}
				CheckConnected = true;
			} else {
				sendData(packet.getData(),p.ipAddress,p.port);
			}
			if(!CheckConnected) {
				this.connectedPlayers.add(player);
			}
		}
		
	}

	private void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void sendDataToAllClients(byte[] data) {
		System.out.println("hi");
		for(ConnectedPlayers p : connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}
		
	}

}
