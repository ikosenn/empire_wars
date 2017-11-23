package empire.wars.net;

import java.io.Serializable;
import java.util.UUID;

import empire.wars.EmpireWars;

/*
 * We try restrict our game to sending packets using this class.
 * This makes it easier to handle the messaging system since all
 * we expect is data constructed in a certain fashion.
 * 
 * MESSAGE CATEGORY TYPES
 * 
 * SETVEL: Contains velocity updates.
 * SETPOS: Contains position updates.
 * 
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private UUID objectUUID;
	private String msg;
	private String msgType; // e.g UPDATE, CONTROL
	private String categoryType; // more specific e.g SETPOS.
	private String className; // The class that is being updated

	// mostly used for control packets such as creating connections
	public Message(String msg, String messageType) {
		this.msg = msg;
		this.msgType = messageType;
	}
	
	// Use this for class updates
	public Message(
			UUID objectUUID, String messageType, String categoryType,
			String msg, String className) {
		this.objectUUID = objectUUID;
		this.msg = msg;
		this.msgType = messageType;
		this.categoryType = categoryType;
		this.className = className;
	}
	
	/**
	 * ObjectUUID setter
	 * @param uuid. The new UUID. This allows us to 
	 * change the UUID for newly created objects when need be
	 */
	public void setObjectUUID(UUID uuid) {
		this.objectUUID = uuid;
	}
	
	/*
	 * ObjectUUID getter.
	 */
	public UUID getObjectUUID() {
		return this.objectUUID;
	}
	
	/**
	 *  MSG getter
	 * @return String. THe message string.
	 */
	public String getMsg() {
		return this.msg;
	}
	
	/**
	 * MsgType getter
	 */
	public String getMsgType() {
		return this.msgType;
	}
	
	/*
	 * CategoryType getter
	 */
	public String getCategoryType() {
		return this.categoryType;
	}
	
	/*
	 * className getter.
	 */
	public String getClassName() {
		return this.className;
	}
	
	/**
	 * Determines which handler will process the message
	 * @param msgPacket. The packet sent across the network.
	 * @param ew. The current game state.
	 */
	public static void determineHandler(Message msgPacket, EmpireWars ew) {
		String msgType = msgPacket.getMsgType();
		String className = msgPacket.getClassName();
		
		if (className == "PLAYER") {
			new PlayerMessageHandler(msgPacket, ew);
		}
	}
}
