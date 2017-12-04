package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.Castle.TEAM;
import empire.wars.EmpireWars;
import empire.wars.EmpireWars.Direction;
import empire.wars.NetworkEntity;
import empire.wars.Player;

public class PlayerMessageHandler extends EntityMessageHandler {

	public PlayerMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}
	
	/**
	 * Makes network updates to the player entity. It also update the 
	 * position of the health bar to follow the players movement.
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		Player player = (Player)entity;
		player.setHealthBarPos();
		
		if (categoryType.equals("SETDIRECTION")) {
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
			player.changeDirection(direction, this.ew);
		}
		
	}


	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		Player player = new Player(ew.getTileWidth() * 4, ew.getTileHeight() * 4, 0, 0, TEAM.BLUE);
		player.setObjectUUID(objectUUID);
		player.setObjectType("NETWORK");
		ew.getClientPlayer().put(player.getObjectUUID(), player);
		return (NetworkEntity)player;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return ew.getClientPlayer();
	}
}
