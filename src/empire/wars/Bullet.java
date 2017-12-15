package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.EmpireWars.TEAM;
import empire.wars.net.Message;
import jig.Vector;
import jig.ResourceManager;

public class Bullet extends NetworkEntity {
	private Vector velocity;
	private String bullet_image;
	
	public enum BULLET_TYPE
	{
		PLAYER,
		CREPES,
		CASTLE
	}
	
	private BULLET_TYPE bullet_type;
	
	public Bullet(final float x, final float y, final float vx, final float vy, final String in_bullet_image, final BULLET_TYPE in_bullet_type, final TEAM in_team){
		super(x,y);
		this.velocity = new Vector(vx, vy);
		this.bullet_image = in_bullet_image;
		this.bullet_type = in_bullet_type;
		this.team = in_team;
		
		addImageWithBoundingBox(ResourceManager.getImage(this.bullet_image));
	}
	
	/**
	 * The server decides on creep collision detection. This is here so the 
	 * server can alert the client that created this bullet that they should delete it.
	 * @param game
	 */
	public void serverSendCollisionUpdates(EmpireWars game) {
		String className = this.getClass().getSimpleName().toUpperCase();
		Message posUpdate = new Message(
			this.getObjectUUID(), "UPDATE", "SETSERVERCOL", "", className);
		game.sendPackets.add(posUpdate);
	}
	
	/*
	 * collision between the bullets and the walls
	 */
	private void checkCollision(EmpireWars ew) {
		if (!this.objectType.equals("ORIGINAL")) {
			return;
		}
		int bx = (int) this.getX() / 32;
		int by = (int) this.getY() / 32;
		int wallIndex = ew.map.getLayerIndex("walls");
		if (ew.map.getTileId(bx, by, wallIndex) != 0 ) {
			this.explode();
		}
	}
	
	/**
	 * Used by the network to set the players color to the right color.
	 * @param team
	 */
	public void changeColor(TEAM team) {
		this.team = team;
	}
	
	@Override
	public void networkUpdate(EmpireWars game) {
		super.networkUpdate(game);
		this.sendColorUpdate(game);
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
		EmpireWars ew = (EmpireWars) game;
		translate(velocity.scale(delta));
		this.checkCollision(ew);
		this.networkUpdate(ew);  // network updates
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}
}
