package empire.wars.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import empire.wars.EmpireWars;

public class GameServer extends Thread {
	
	private InetAddress ipAddress;
	private DatagramSocket socket;
	
	public GameServer() {
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
			String message = new String(packet.getData());
			System.out.println("Client: " + packet.getAddress().getHostAddress() +" : "+ packet.getPort() +" : "+ message);
			if(message.trim().equalsIgnoreCase("ping")) {
				sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
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

}
