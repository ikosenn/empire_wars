package empire.wars;

import java.util.UUID;

import jig.Entity;

/**
 * Extend this class if you intend for the entity to 
 * be shared across the network. 
 * This class will automatically send the Entity's position updates
 * It will also send updates when an Entity is to be destroyed.
 * 
 * Usage:
 * 	With position updates you don't need to do anything.
 *  For an entity to be destroyed set ``isDestoyed`` to be true.
 *  This way we can have a loop in both the clients that destroys 
 *  objects.
 *  
 * @author peculiaryak
 *
 */
public class NetworkEntity extends Entity {
	private boolean isDestroyed = false;
	private UUID objectUUID;
	
	// Don't set this they are used by the 
	// networking system to track changes.
	private boolean _isDestroyed = false;
	private float _currentX;
	private float _currentY;
	
	public NetworkEntity(final float x, final float y) {
		super(x, y);
		objectUUID = UUID.randomUUID();
	}
}
