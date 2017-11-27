package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import jig.Vector;
import jig.ResourceManager;

public class Bullet extends NetworkEntity {
	private Vector velocity;
	private String bullet_image;
	private boolean exploded;
	
	public enum BULLET_TYPE
	{
		PLAYER,
		CREPES,
		CASTLE
	}
	
	private BULLET_TYPE bullet_type;
	
	public Bullet(final float x, final float y, final float vx, final float vy, final String in_bullet_image, final BULLET_TYPE in_bullet_type){
		super(x,y);
		this.exploded = false;
		this.velocity = new Vector(vx, vy);
		this.bullet_image = in_bullet_image;
		this.bullet_type = in_bullet_type;
		
		addImageWithBoundingBox(ResourceManager.getImage(this.bullet_image));
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
		translate(velocity.scale(delta));
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}
	
	public void explode() {
		exploded = true;
	}
	
	public boolean isExploded() {
		return exploded;
	}
}
