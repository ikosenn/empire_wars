package empire.wars.net;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import empire.wars.EmpireWars;



public class GameClient extends Thread {

	private DatagramSocket socket;
	private EmpireWars game;
	
	public GameClient(EmpireWars game, DatagramSocket s) {
		this.game = game;
		this.socket = s;
	}


	public void run() {
		while(true) {
			ArrayList<Message> sendMsg = this.game.getSendPackets();

			if (sendMsg.size() > 0) {
				Message tempMsg = sendMsg.get(0);
				if (this.game.getSessionType().equals("CLIENT")) {
					this.sendToServer(tempMsg);
				} else if (this.game.getSessionType().equals("SERVER")) {
					this.sendToOtherClients(tempMsg);
				}
				this.game.popSendPackets(tempMsg);
			}
			try {
				Thread.sleep(EmpireWars.THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Serializes the Message class to bytes
	 * @param msg. The message object to send
	 * @return byte array of the data to be sent
	 * @throws IOException 
	 */
	private byte[] serializerMessage(Message msg) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
		objectStream.writeObject(msg);
		byte[] data = outputStream.toByteArray();
		return data;
	}
	
	/*
	 * Sends the data to the server this is done by the clients
	 */
	private void sendToServer(Message msg) {

		try {
			byte[] data = this.serializerMessage(msg);
			ConnectedPlayers server = this.game.getBroadcastServer();
			InetAddress serverIP = server.getIpAddress();
			int serverPort = server.getPort();
			DatagramPacket packet = new DatagramPacket(
				data, data.length, serverIP, serverPort
			);
			this.socket.send(packet);
		} catch (IOException e) {
			System.out.println(
				"An error occurred while serializing/ sending Message to server." + e.toString());
		}  
	}
	
	/*
	 * Sends a message to all the other clients. This is done 
	 * by the server. The server also filters out messages so a 
	 * message is not returned back to a client that sent it
	 */
	private void sendToOtherClients(Message msg) {
		try {
			byte[] data = this.serializerMessage(msg);
			ArrayList<ConnectedPlayers> connectedPlayers = this.game.getConnectedPlayers();
			for (int i = 0; i < connectedPlayers.size(); i++) {
				ConnectedPlayers temp = connectedPlayers.get(i);
				if (temp.getIpAddress() != msg.getIpAddress() &&
						temp.getPort() != msg.getPort()) {
					DatagramPacket packet = new DatagramPacket(
						data, data.length, temp.getIpAddress(), temp.getPort()
					);
					this.socket.send(packet);
					
				}
					
			}
		} catch (IOException e) {
			System.out.println(
				"An error occurred while serializing/ sending Message to other clients" + e.toString());
		}  
	}
	
	public void broadCastMessage(Message msg) {
		
		Message tempMsg = msg;
		
		try {
			byte[] data = this.serializerMessage(tempMsg);
			
			DatagramPacket packet = new DatagramPacket(
				data, data.length, InetAddress.getByName("255.255.255.255"), EmpireWars.SERVER_PORT
			);
			this.socket.send(packet);
		} catch (IOException e) {
			System.out.println(
				"An error occurred while broadcasting the message" + e.toString());
		}  
	}
}
