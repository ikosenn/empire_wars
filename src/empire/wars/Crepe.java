package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;
import jig.Vector;
import jig.ResourceManager;

public class Crepe extends Entity{
	private Vector velocity;
	
	public Crepe(final float x, final float y, final float vx, final float vy){
		super(x,y);
		this.velocity = new Vector(vx, vy);
		
		//TODO: addImageWithBoundingBox(ResourceManager.getImage(this.bullet_image));
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
}