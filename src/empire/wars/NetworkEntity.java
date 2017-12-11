package empire.wars;

import java.util.UUID;

import empire.wars.net.Message;
import jig.Entity;
import jig.Vector;

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
	protected boolean exploded = false;
	protected Vector velocity;
	private UUID objectUUID;
	// Don't set this they are used by the 
	// networking system to track changes.
	// determines if the object is for that client
	// or another client.
	// ORGINAL: Means its for this client
	// NETWORK:: MEans its for another client
	protected String objectType = "ORIGINAL"; 
	private boolean _exploded = false;
	private float _currentX = 0;
	private float _currentY = 0;
	
	public NetworkEntity(final float x, final float y) {
		super(x, y);
		objectUUID = UUID.randomUUID();
	}
	
	/*
	 * ObjectUUID getter.
	 */
	public UUID getObjectUUID() {
		return this.objectUUID;
	}
	
	/*
	 * ObjectUUID setter.
	 */
	public void setObjectUUID(UUID uuid) {
		this.objectUUID = uuid;
	}
	
	/**
	 * Object Type setter
	 * @param type
	 */
	public void setObjectType(String type) {
		this.objectType = type;
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}
	
	/*
	 * Creates Message Packets for updating the position
	 * of the Entity. This are sent through the network.
	 */
	public void sendPosUpdates(EmpireWars game) {
		if (this.objectType == "ORIGINAL" ) {
			if (this._currentX != this.getX() || this._currentY != this.getY()) {
				String className = this.getClass().getSimpleName().toUpperCase();
				String msg = this.getX() + ":" + this.getY();
				Message posUpdate = new Message(
					this.getObjectUUID(), "UPDATE", "SETPOS", msg, className);
				game.sendPackets.add(posUpdate);
				this._currentX = this.getX();
				this._currentY = this.getY();
			}
		}
	}
	
	public void sendCollisionUpdates(EmpireWars game) {
		if (this.objectType == "ORIGINAL" ) {
			if (this._exploded != this.exploded) {
				String className = this.getClass().getSimpleName().toUpperCase();
				Message posUpdate = new Message(
					this.getObjectUUID(), "UPDATE", "SETCOL", "", className);
				game.sendPackets.add(posUpdate);
				this._exploded = exploded;
			}
		}
	}
	
	/*
	 * Gets called with every frame.
	 */
	public void networkUpdate(EmpireWars game) {
		this.sendPosUpdates(game);
		this.sendCollisionUpdates(game);
	}
	
	/**
	 * called when the entity collides with another entity
	 */
	public void explode() {
		exploded = true;
	}
	
	/**
	 *  True if the entity is going to be destroyed in the next loop
	 */
	public Boolean isDestroyed() {
		return exploded;
	}
	
	
	/*
	 * exploded getter
	 */
	public boolean isExploded() {
		return _exploded;
	}
	
	/*
	 * _exploded setter
	 */
	public void setExploded() {
		_exploded = true;
	}
}
