package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.Bullet;
import empire.wars.EmpireWars;
import empire.wars.NetworkEntity;
import empire.wars.Bullet.BULLET_TYPE;

public class BulletMessageHandler extends EntityMessageHandler {

	public BulletMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	/**
	 * Makes network updates to the bullet entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		if (categoryType.equals("SETCOL")) {
			Bullet bullet = (Bullet)entity;
			bullet.setExploded();
		}
	}
	
	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		Bullet bulletTemp;
		bulletTemp = new Bullet(
			0f, 0f, 0f, 0f, EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER);
		bulletTemp.setObjectUUID(objectUUID);
		bulletTemp.setObjectType("NETWORK");
		ew.getClientBullets().put(bulletTemp.getObjectUUID(), bulletTemp);
		return (NetworkEntity)bulletTemp;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return ew.getClientBullets();
	}
}
