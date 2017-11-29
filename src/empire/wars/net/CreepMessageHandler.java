package empire.wars.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import empire.wars.Creep;
import empire.wars.EmpireWars;
import empire.wars.NetworkEntity;
import empire.wars.Player;
import empire.wars.Castle.TEAM;
import empire.wars.EmpireWars.Direction;

public class CreepMessageHandler extends EntityMessageHandler {

	public CreepMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}


	/**
	 * Makes network updates to the creep entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		
		if (categoryType.equals("SETDIRECTION")) {
			Creep creep = (Creep)entity;
			Direction direction = Direction.UP;
			if (msg.equals("UP")) {
				direction = Direction.UP;
			} else if (msg.equals("DOWN")) {
				direction = Direction.DOWN;
			} else if (msg.equals("LEFT")) {
				direction = Direction.LEFT;
			} else if (msg.equals("RIGHT")) {
				direction = Direction.RIGHT;
			}
			creep.changeDirection(direction, this.ew);
		}
	}
	
	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		Creep creepTemp;
		creepTemp = new Creep(ew.getTileWidth() * 4, ew.getTileHeight() * 4);
		creepTemp.setObjectUUID(objectUUID);
		creepTemp.setObjectType("NETWORK");
		ew.getCreeps().put(creepTemp.getObjectUUID(),creepTemp);
		return (NetworkEntity)creepTemp;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return ew.getCreeps();
	}
}
