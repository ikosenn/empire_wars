package empire.wars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.Bullet.BULLET_TYPE;
import empire.wars.Castle.TEAM;
import empire.wars.EmpireWars.Direction;
import empire.wars.net.Message;
import jig.ResourceManager;
import jig.Vector;

public class Player extends NetworkEntity {
	public ArrayList<Bullet> bullets;
	public List<PowerUp> powerups;
	public HealthBar health;
	public float hbXOffset = 16; // health bar offset so its on top of the players head
	public float hbYOffset = 25; // health bar offset so its on top of the players head

	public TEAM team;
	public Direction direction;
	public Direction _direction;
	Random rand = new Random();
	
	private Animation blue_movement_up = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.BLUE_PLAYER_MOVING_IMG_RSC, 32, 32), 0, 3, 2, 3, true, 150, true);
	private Animation blue_movement_down = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.BLUE_PLAYER_MOVING_IMG_RSC, 32, 32), 0, 0, 2, 0, true, 150, true);
	private Animation blue_movement_left = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.BLUE_PLAYER_MOVING_IMG_RSC, 32, 32), 0, 1, 2, 1, true, 150, true);
	private Animation blue_movement_right = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.BLUE_PLAYER_MOVING_IMG_RSC, 32, 32), 0, 2, 2, 2, true, 150, true);
	
	private Animation red_movement_up = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.RED_PLAYER_MOVING_IMG_RSC, 32, 32), 0, 3, 2, 3, true, 150, true);
	private Animation red_movement_down = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.RED_PLAYER_MOVING_IMG_RSC, 32, 32), 0, 0, 2, 0, true, 150, true);
	private Animation red_movement_left = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.RED_PLAYER_MOVING_IMG_RSC, 32, 32), 0, 1, 2, 1, true, 150, true);
	private Animation red_movement_right = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.RED_PLAYER_MOVING_IMG_RSC, 32, 32), 0, 2, 2, 2, true, 150, true);
	
	public Player(final float x, final float y, final float vx, final float vy, final TEAM in_team){
		super(x,y);
		this.velocity = new Vector(vx, vy);
		this.health = new HealthBar(this.getX() - hbXOffset,  this.getY() - hbYOffset, in_team);
		this.team = in_team;

		int randNumber = rand.nextInt(4);
		direction = Direction.values()[randNumber];
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.PLAYER_IMG_RSC));
		addAnimation(getAnimation(direction));
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
	
	public Animation getAnimation(Direction direction)
	{
		if (team == TEAM.BLUE)
		{
			switch (direction)
			{
			case UP:
				return blue_movement_up;
			case DOWN:
				return blue_movement_down;
			case LEFT:
				return blue_movement_left;
			case RIGHT:
				return blue_movement_right;
			default:
				return blue_movement_down;
			}
		}
		else
		{
			switch (direction)
			{
			case UP:
				return red_movement_up;
			case DOWN:
				return red_movement_down;
			case LEFT:
				return red_movement_left;
			case RIGHT:
				return red_movement_right;
			default:
				return red_movement_down;
			}
		}
	}
	
	public void changeDirection(Direction new_direction, EmpireWars game)
	{
		while(getNumAnimations() > 2){
			removeAnimation(getAnimation(Direction.UP));
			removeAnimation(getAnimation(Direction.DOWN));
			removeAnimation(getAnimation(Direction.LEFT));
			removeAnimation(getAnimation(Direction.RIGHT));
		}
		addAnimation(getAnimation(new_direction));
		direction = new_direction;
		// update clients on player position
		this.sendDirectionUpdates(game, "SETDIRECTION");
	}
	
	private void sendDirectionUpdates(EmpireWars game, String categoryType) {
		if (this.objectType == "ORIGINAL" ) {
			String className = this.getClass().getSimpleName().toUpperCase();
			String msg = this.direction.toString();
			Message posUpdate = new Message(
				this.getObjectUUID(), "UPDATE", categoryType, msg, className);
			game.sendPackets.add(posUpdate);
		}
	}
	
//	public Player(float x, float y) {
//		super(x,y);
//		this.velocity = new Vector(0.1F, 0.1F);
//		this.health = new HealthBar(this.getX() - hbXOffset,  this.getY() - hbYOffset, team);
//		
//		//TODO: initialize bullet and powerups
//		addImageWithBoundingBox(ResourceManager.getImage(getImageName(Direction.DOWN)));
//	}

	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
		EmpireWars ew = (EmpireWars) game;
		this.networkUpdate(ew);  // network updates
		// get user input
		
		Input input = container.getInput();
	
		Vector previousPoition = this.getPosition();
		

		if(input.isKeyDown(Input.KEY_W) || input.isKeyDown(Input.KEY_UP)){
			setVelocity(new Vector(0.f, -ew.PLAYER_SPEED));
			changeDirection(Direction.UP, ew);
		}
		if(input.isKeyDown(Input.KEY_S) || input.isKeyDown(Input.KEY_DOWN)){
			setVelocity(new Vector(0.f, ew.PLAYER_SPEED));
			changeDirection(Direction.DOWN, ew);
		}
		if(input.isKeyDown(Input.KEY_A) || input.isKeyDown(Input.KEY_LEFT)){
			setVelocity(new Vector(-ew.PLAYER_SPEED, 0.f));
			changeDirection(Direction.LEFT, ew);
		}
		if(input.isKeyDown(Input.KEY_D) || input.isKeyDown(Input.KEY_RIGHT)){
			setVelocity(new Vector(ew.PLAYER_SPEED, 0.f));
			changeDirection(Direction.RIGHT, ew);
		}
		if(!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_UP) 
				&& !input.isKeyDown(Input.KEY_S) && !input.isKeyDown(Input.KEY_DOWN)
				&& !input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_LEFT)
				&& !input.isKeyDown(Input.KEY_D) && !input.isKeyDown(Input.KEY_RIGHT)){
			setVelocity(new Vector(0.f, 0.f));
		}
		
		translate(velocity.scale(delta));
		
		if(velocity.getY() < 0 ){
			direction = Direction.UP;
		}else if(velocity.getY()>0){
			direction = Direction.DOWN;
		}else if(velocity.getX()<0){
			direction = Direction.LEFT;
		}else if(velocity.getX()>0){
			direction = Direction.RIGHT;
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
