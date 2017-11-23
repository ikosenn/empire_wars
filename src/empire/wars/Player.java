package empire.wars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.Bullet.BULLET_TYPE;
import jig.ResourceManager;
import jig.Vector;

public class Player extends NetworkEntity {
	public ArrayList<Bullet> bullets;
	public List<PowerUp> powerups;
	public HealthBar health;
	public float hbXOffset = 16; // health bar offset so its on top of the players head
	public float hbYOffset = 25; // health bar offset so its on top of the players head
	private String username;
	
	// direction info of a player
	private int direction;
	private final int UP = 1;
	private final int DOWN = 2;
	private final int LEFT = 3;
	private final int RIGHT = 4;
	
	
	public Player(final float x, final float y, final float vx, final float vy){
		super(x,y);
		this.velocity = new Vector(vx, vy);
		this.health = new HealthBar(this.getX() - hbXOffset,  this.getY() - hbYOffset);
		
		//TODO: initialize bullet and powerups
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.PLAYER_IMG_RSC));
		
		direction = 1;
		bullets = new ArrayList<Bullet>();
	}
	
	public void shoot(){
		ResourceManager.getSound(EmpireWars.PLAYER_SHOOTSND_RSC).play();
		switch(direction){
		case UP:
			bullets.add(new Bullet(getX(), getY(), 0.f, -EmpireWars.PLAYER_BULLETSPEED, 
					EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER));
			break;
		case DOWN:
			bullets.add(new Bullet(getX(), getY(), 0.f, EmpireWars.PLAYER_BULLETSPEED, 
					EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER));
			break;
		case LEFT:
			bullets.add(new Bullet(getX(), getY(), -EmpireWars.PLAYER_BULLETSPEED, 0.f, 
					EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER));
			break;
		case RIGHT:
			bullets.add(new Bullet(getX(), getY(), EmpireWars.PLAYER_BULLETSPEED, 0.f, 
					EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER));
			break;
		default:
			break;
		}
		
	}
	
	public Player(float x, float y, String username) {
		super(x,y);
		System.out.println("added Player");
		this.username = username;
		this.velocity = new Vector(0.1F, 0.1F);
		this.health = new HealthBar(this.getX() - hbXOffset,  this.getY() - hbYOffset);
		
		//TODO: initialize bullet and powerups
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.PLAYER_IMG_RSC));
	}

	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
		EmpireWars ew = (EmpireWars) game;
		this.networkUpdate(ew);  // network updates
		// get user input
		
		Input input = container.getInput();
	
		Vector previousPoition = this.getPosition();

		if(input.isKeyDown(Input.KEY_W) || input.isKeyDown(Input.KEY_UP)){
			setVelocity(new Vector(0.f, -ew.PLAYER_SPEED));
		}
		if(input.isKeyDown(Input.KEY_S) || input.isKeyDown(Input.KEY_DOWN)){
			setVelocity(new Vector(0.f, ew.PLAYER_SPEED));
		}
		if(input.isKeyDown(Input.KEY_A) || input.isKeyDown(Input.KEY_LEFT)){
			setVelocity(new Vector(-ew.PLAYER_SPEED, 0.f));
		}
		if(input.isKeyDown(Input.KEY_D) || input.isKeyDown(Input.KEY_RIGHT)){
			setVelocity(new Vector(ew.PLAYER_SPEED, 0.f));
		}
		if(!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_UP) 
				&& !input.isKeyDown(Input.KEY_S) && !input.isKeyDown(Input.KEY_DOWN)
				&& !input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_LEFT)
				&& !input.isKeyDown(Input.KEY_D) && !input.isKeyDown(Input.KEY_RIGHT)){
			setVelocity(new Vector(0.f, 0.f));
		}
		
		translate(velocity.scale(delta));
		
		if(velocity.getY() < 0 ){
			direction = UP;
		}else if(velocity.getY()>0){
			direction = DOWN;
		}else if(velocity.getX()<0){
			direction = LEFT;
		}else if(velocity.getX()>0){
			direction = RIGHT;

		}
		
		// player shooting bullets
		if(input.isKeyPressed(Input.KEY_J)){
			shoot();
		}
		


		// update health bar pos
		this.setHealthBarPos();
		
		int wallIndex = ew.map.getLayerIndex("walls");
		int minX = (int)(this.getCoarseGrainedMinX()/32);
		int minY = (int)(this.getCoarseGrainedMinY()/32);
		int maxX = (int)(this.getCoarseGrainedMaxX()/32);
		int maxY = (int)(this.getCoarseGrainedMaxY()/32);
		
		System.out.println(Integer.toString(minX) + Integer.toString(minY) + Integer.toString(maxX) + Integer.toString(maxY));
		
		if (ew.map.getTileId(minX, minY, wallIndex) != 0 ||
				ew.map.getTileId(minX, maxY, wallIndex) != 0 ||
						ew.map.getTileId(maxX, minY, wallIndex) != 0 ||
								ew.map.getTileId(maxX, maxY, wallIndex) != 0)
		{
			this.setPosition(previousPoition);
			this.setHealthBarPos();
		}
		
		// update and collision detection for bullets
		for(Bullet b:bullets){
			b.update(container, game, delta, mapWidth, mapHeight, tileWidth, tileHeight);
			int bx = (int) b.getX()/32;
			int by = (int) b.getY()/32;
			
			if(ew.map.getTileId(bx, by, wallIndex)!=0){
				b.explode();
			}
		}
		
		for(Iterator<Bullet> i = bullets.iterator(); i.hasNext();){
			if(i.next().isExploded() == true){
				i.remove();
			}
		}
	}
	
	/**
	 * Update the health bar based on the players movement.
	 *
	 */
	public void setHealthBarPos() {
		this.health.setPosition(this.getX() - hbXOffset,  this.getY() - hbYOffset);
	}

	public String getUserName() {
		return this.username;
	}

	/**
	 * Draws all boundaries and images associated with the entity at their
	 * designated offset values. We override this so we can be able to debug the paths
	 * @param g The current graphics context
	 */
	@Override
	public void render(final Graphics g) {
		super.render(g);
		this.health.render(g);
		
		for(Bullet b:bullets){
			b.render(g);
		}
	}

}
