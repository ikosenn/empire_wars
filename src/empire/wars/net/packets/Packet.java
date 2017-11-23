package empire.wars.net.packets;

import empire.wars.net.GameClient;
import empire.wars.net.GameServer;

public abstract class Packet {

	public static enum PacketTypes {
		LOGIN(00), DISCONNECT(01), INCORRECT(10);
		
		private int packetId;
		private PacketTypes(int packetId) {
			this.packetId = packetId;
		}
		
		public int getId() {
			return packetId;
		}
	}
	
	public byte packetId;
	
	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}
	
	public abstract void writeData(GameServer server);
	public abstract void writeData(GameClient client);
	public abstract byte[] getData();
	public static PacketTypes lookupPackets(String packetId) {
		try {
			return lookupPacket(Integer.parseInt(packetId));
		} catch (NumberFormatException e) {
			return PacketTypes.INCORRECT;
		}	
	}
	
	public String readData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(2);			//for reading the packet name : for example - 00 or 01
		
	}
	
	public static PacketTypes lookupPacket(int id) {
		for(PacketTypes p : PacketTypes.values())
			if(p.getId() == id) {
				return p;
			}
		return PacketTypes.INCORRECT;
	}
}
