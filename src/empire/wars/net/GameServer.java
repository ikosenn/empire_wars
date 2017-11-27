package empire.wars.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import empire.wars.EmpireWars;

public class GameServer extends Thread {
	
	private DatagramSocket socket;
	private EmpireWars game;
	 
	public GameServer(EmpireWars game, DatagramSocket s) {
		this.game = game;
		this.socket = s;
	}

	public void run() {
		while(true) {
			byte[] data = new byte[5120];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
				this.deserializeMessage(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Takes a packet and creates the Message equivalent version
	 * @param packet. Consists of the data received.
	 * @return Message. The packet converted to messages that 
	 * our system can comprehend.
	 * @throws IOException 
	 */
	private void deserializeMessage(DatagramPacket packet) throws IOException {
		byte[] data = packet.getData();
		ByteArrayInputStream packetByteStream = new ByteArrayInputStream(data);
		ObjectInputStream objInputStream = new ObjectInputStream(packetByteStream);
		try {
			Message msg = (Message)objInputStream.readObject();
			this.game.appendReceivedPackets(msg);
			if (this.game.getSessionType() == "SERVER") {
				// server should send this to everyone else
				this.game.appendSendPackets(msg);
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Ensure the class returned exists.");
		}
	}
}
