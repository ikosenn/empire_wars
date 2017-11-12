package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;
import jig.Vector;
import jig.ResourceManager;

public class Player extends Entity{
	private Vector velocity;
	
	
	public Player(final float x, final float y, final float vx, final float vy){
		super(x,y);
		this.velocity = new Vector(vx, vy);
		
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.PLAYER_IMG_RSC));
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
		EmpireWars ew = (EmpireWars) game;
		// get user input
		Input input = container.getInput();
	
		if(input.isKeyDown(Input.KEY_W)){
			setVelocity(new Vector(0.f, -ew.PLAYER_SPEED));
		}
		if(input.isKeyDown(Input.KEY_S)){
			setVelocity(new Vector(0.f, ew.PLAYER_SPEED));
		}
		if(input.isKeyDown(Input.KEY_A)){
			setVelocity(new Vector(-ew.PLAYER_SPEED, 0.f));
		}
		if(input.isKeyDown(Input.KEY_D)){
			setVelocity(new Vector(ew.PLAYER_SPEED, 0.f));
		}
		if(!input.isKeyDown(Input.KEY_W)
				&& !input.isKeyDown(Input.KEY_S)
				&& !input.isKeyDown(Input.KEY_A)
				&& !input.isKeyDown(Input.KEY_D)){
			setVelocity(new Vector(0.f, 0.f));
		}
		
		translate(velocity.scale(delta));
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}
	
}
