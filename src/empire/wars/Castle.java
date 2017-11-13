package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;
import jig.Vector;
import jig.ResourceManager;

public class Castle extends Entity{
	
	public enum TEAM
	{
		RED,
		BLUE
	}
	
	Bullet fireBullet;
	TEAM team;
	
	
	public Castle(final float x, final float y, final TEAM in_team){
		super(x,y);
		this.team = in_team;
		
		//TODO: instantiate bullet for castle
		//TODO: addImageWithBoundingBox(ResourceManager.getImage(this.bullet_image));
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
	}
}