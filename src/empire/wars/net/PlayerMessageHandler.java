package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.EmpireWars.Direction;
import empire.wars.EmpireWars.TEAM;
import empire.wars.NetworkEntity;
import empire.wars.Player;

public class PlayerMessageHandler extends EntityMessageHandler {

	public PlayerMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}
	
	/**
	 * update the player's direction. This is used for direction updates.
	 * @param player. The player entity.
	 * @param msg. Message containing the direction to change to. 
	 */
	private void setDirection(Player player, String msg) {
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
	
	/**
	 * update the player's color.
	 * @param player. The player entity.
	 * @param msg. Message containing the color to set the player entity. 
	 */
	private void setColor(Player player, String msg) {
		TEAM team;
		if (msg.equals("BLUE")) {
			team = TEAM.BLUE;
		} else {
			team = TEAM.RED;
		}
		player.changeColor(team);
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
			this.setDirection(player, msg);
		} else if (categoryType.equals("SETCOLOR")) {
			this.setColor(player, msg);
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
