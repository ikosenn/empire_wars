package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;
import jig.Vector;
import jig.ResourceManager;

public class PowerUp extends Entity{
	
	public enum PowerUpType
	{
		ONE,
		TWO
	}

	public PowerUpType type;
	
	public PowerUp(final float x, final float y, final PowerUpType in_type){
		super(x,y);
		this.type = in_type;

		//TODO: addImageWithBoundingBox(ResourceManager.getImage(this.bullet_image));
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
	}
}