package empire.wars.net;

import java.util.Iterator;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.NetworkEntity;
import empire.wars.Player;

public class PlayerMessageHandler extends EntityMessageHandler {

	public PlayerMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	/**
	 * Attempts to find a player Entity in the clientPlayer arrayList
	 * One is created if it doesn't already exist.
	 * 
	 * @return The entity instance.
	 */
	@Override
	public NetworkEntity getOrCreate(UUID objectUUID) {
		Player playerTemp;
		for (Iterator<Player> i = ew.getClientPlayer().iterator(); i.hasNext();) {
			playerTemp = i.next();
			if (playerTemp.getObjectUUID() == objectUUID) {
				return (NetworkEntity)playerTemp;
			}
		}
		
		playerTemp = new Player(ew.getTileWidth() * 4, ew.getTileHeight() * 4, 0, 0);
		playerTemp.setObjectUUID(objectUUID);
		ew.getClientPlayer().add(playerTemp);
		return (NetworkEntity)playerTemp;
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
	}
}
