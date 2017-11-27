package empire.wars.net;

import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.NetworkEntity;

/**
 * An abstract class that handles the common 
 * cases/ methods for the entity packet handler.
 * Entity packet handlers should ideally inherit this.
 * 
 * @author peculiaryak
 *
 */
public abstract class EntityMessageHandler {
	Message msgPacket;
	protected EmpireWars ew;
	
	public EntityMessageHandler(Message msg, EmpireWars ew) {
		this.msgPacket = msg;
		this.ew = ew;
		this.handle();
	}
	
	public abstract NetworkEntity getOrCreate(UUID objectUUID);
	
	
	/*
	 * Determines what update is required. If it's an update for the 
	 * position, velocity or destroying the object
	 */
	public void update(NetworkEntity entity, String categoryType, String msg) {
		if (categoryType.equals("SETPOS")) {
			String[] pos = msg.split("\\:");
			entity.setPosition(
				Float.parseFloat(pos[0]), Float.parseFloat(pos[1]));
		}
	}
	
	/**
	 * Processes the packets and updates the necessary attributes to update
	 * in the entity.
	 */
	public void handle() {
		UUID objectUUID = msgPacket.getObjectUUID();
		String categoryType = msgPacket.getCategoryType();
		String msg = msgPacket.getMsg();
		NetworkEntity entity = this.getOrCreate(objectUUID);
		this.update(entity, categoryType, msg);
	}
}
