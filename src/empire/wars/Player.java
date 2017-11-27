package empire.wars;

import java.util.List;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.EmpireWars.Direction;
import empire.wars.EmpireWars.Team;
import jig.ResourceManager;
import jig.Vector;

public class Player extends NetworkEntity {
	public Bullet bullet;
	public List<PowerUp> powerups;
	public HealthBar health;
	public float hbXOffset = 16; // health bar offset so its on top of the players head
	public float hbYOffset = 25; // health bar offset so its on top of the players head
	public Team team;
	public Direction direction;
	Random rand = new Random();
	
	public Player(final float x, final float y, final float vx, final float vy, final Team in_team){
		super(x,y);
		this.velocity = new Vector(vx, vy);
		this.health = new HealthBar(this.getX() - hbXOffset,  this.getY() - hbYOffset, in_team);
		this.team = in_team;
		
		int randNumber = rand.nextInt(4);
		direction = Direction.values()[randNumber];
		addImageWithBoundingBox(ResourceManager.getImage(getImageName(direction)));
	}
	
	public String getImageName(Direction direction)
	{
		if (team == Team.BLUE)
		{
			switch (direction)
			{
			case UP:
				return EmpireWars.BLUE_PLAYER_UP_IMG_RSC;
			case DOWN:
				return EmpireWars.BLUE_PLAYER_DOWN_IMG_RSC;
			case LEFT:
				return EmpireWars.BLUE_PLAYER_LEFT_IMG_RSC;
			case RIGHT:
				return EmpireWars.BLUE_PLAYER_RIGHT_IMG_RSC;
			default:
				return "";
			}
		}
		else
		{
			switch (direction)
			{
			case UP:
				return EmpireWars.RED_PLAYER_UP_IMG_RSC;
			case DOWN:
				return EmpireWars.RED_PLAYER_DOWN_IMG_RSC;
			case LEFT:
				return EmpireWars.RED_PLAYER_LEFT_IMG_RSC;
			case RIGHT:
				return EmpireWars.RED_PLAYER_RIGHT_IMG_RSC;
			default:
				return "";
			}
		}
	}
	
	public void changeDirection(Direction new_direction)
	{
		removeImage(ResourceManager.getImage(getImageName(direction)));
		addImageWithBoundingBox(ResourceManager.getImage(getImageName(new_direction)));
		direction = new_direction;
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
			changeDirection(Direction.UP);
		}
		if(input.isKeyDown(Input.KEY_S) || input.isKeyDown(Input.KEY_DOWN)){
			setVelocity(new Vector(0.f, ew.PLAYER_SPEED));
			changeDirection(Direction.DOWN);
		}
		if(input.isKeyDown(Input.KEY_A) || input.isKeyDown(Input.KEY_LEFT)){
			setVelocity(new Vector(-ew.PLAYER_SPEED, 0.f));
			changeDirection(Direction.LEFT);
		}
		if(input.isKeyDown(Input.KEY_D) || input.isKeyDown(Input.KEY_RIGHT)){
			setVelocity(new Vector(ew.PLAYER_SPEED, 0.f));
			changeDirection(Direction.RIGHT);
		}
		if(!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_UP) 
				&& !input.isKeyDown(Input.KEY_S) && !input.isKeyDown(Input.KEY_DOWN)
				&& !input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_LEFT)
				&& !input.isKeyDown(Input.KEY_D) && !input.isKeyDown(Input.KEY_RIGHT)){
			setVelocity(new Vector(0.f, 0.f));
		}
		
		translate(velocity.scale(delta));

		// update health bar pos
		this.setHealthBarPos();
		
		int wallIndex = ew.map.getLayerIndex("walls");
		int minX = (int)(this.getCoarseGrainedMinX()/32);
		int minY = (int)(this.getCoarseGrainedMinY()/32);
		int maxX = (int)(this.getCoarseGrainedMaxX()/32);
		int maxY = (int)(this.getCoarseGrainedMaxY()/32);
		
		if (ew.map.getTileId(minX, minY, wallIndex) != 0 ||
				ew.map.getTileId(minX, maxY, wallIndex) != 0 ||
						ew.map.getTileId(maxX, minY, wallIndex) != 0 ||
								ew.map.getTileId(maxX, maxY, wallIndex) != 0)
		{
			this.setPosition(previousPoition);
			this.setHealthBarPos();
		}
	}
	
	/**
	 * Update the health bar based on the players movement.
	 *
	 */
	public void setHealthBarPos() {
		this.health.setPosition(this.getX() - hbXOffset,  this.getY() - hbYOffset);
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
	}

}
