package empire.wars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.EmpireWars.Direction;
import empire.wars.EmpireWars.TEAM;
import empire.wars.net.Message;
import jig.Collision;
import jig.ResourceManager;
import jig.Vector;

public class Creep extends NetworkEntity {
	private Vector velocity;
	private Direction direction;
	private Direction _direction;
	public HealthBar health;
	public float hbXOffset = 16; // health bar offset so its on top of the players head
	public float hbYOffset = 25; // health bar offset so its on top of the players head
	
	Random rand = new Random();
	int timer = 0;
	final int timer_max = 5;
	
	public static ArrayList<Vector> speedVectors = new ArrayList<Vector>() {{
		add(new Vector(0f,-0.1f));
		add(new Vector(0.1f,0f));
		add(new Vector(0f,0.1f));
		add(new Vector(-0.1f,0f));
	}};
	
	private Animation creep_movement_up = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.CREEP_MOVING_IMG_RSC, 48, 48), 0, 3, 2, 3, true, 150, true);
	private Animation creep_movement_down = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.CREEP_MOVING_IMG_RSC, 48, 48), 0, 0, 2, 0, true, 150, true);
	private Animation creep_movement_left = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.CREEP_MOVING_IMG_RSC, 48, 48), 0, 1, 2, 1, true, 150, true);
	private Animation creep_movement_right = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.CREEP_MOVING_IMG_RSC, 48, 48), 0, 2, 2, 2, true, 150, true);
	
	public Creep(final float x, final float y, final TEAM in_team){
		super(x,y);
		int randNumber = rand.nextInt(4);
		direction = Direction.values()[randNumber];
		this.team = in_team;
		this.health = new HealthBar(this.getX() - hbXOffset,  this.getY() - hbYOffset, in_team);
		
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.PLAYER_IMG_RSC));
		addAnimation(getAnimation(direction));
		setVelocity(speedVectors.get(direction.ordinal()));
	}
	
	public Animation getAnimation(Direction direction)
	{

		switch (direction)
		{
		case UP:
			return creep_movement_up;
		case DOWN:
			return creep_movement_down;
		case LEFT:
			return creep_movement_left;
		case RIGHT:
			return creep_movement_right;
		default:
			return creep_movement_down;
		}
	}
	
	public void sendHealthBarUpdates(EmpireWars game) {
		if (this.objectType == "ORIGINAL" && this.health.hasChanged()) {
			String className = this.getClass().getSimpleName().toUpperCase();
			String msg = Double.toString(this.health.getCurrentHealth());
			Message healthUpdate = new Message(
				this.getObjectUUID(), "UPDATE", "SETHEALTH", msg, className);
			game.sendPackets.add(healthUpdate);
		}
	}
	
	@Override
	public void networkUpdate(EmpireWars game) {
		super.networkUpdate(game);
		this.sendColorUpdate(game);
		this.sendHealthBarUpdates(game);
	}
	
	/**
	 * Used by the network to set the players color to the right color.
	 * @param team
	 */
	public void changeColor(TEAM team) {
		removeAnimation(getAnimation(Direction.UP));
		removeAnimation(getAnimation(Direction.DOWN));
		removeAnimation(getAnimation(Direction.LEFT));
		removeAnimation(getAnimation(Direction.RIGHT));
		this.team = team;
		this.health.setTeam(team);
		addAnimation(getAnimation(this.direction));
	}
	
	public void changeDirection(Direction new_direction, EmpireWars game)
	{
		removeAnimation(getAnimation(Direction.UP));
		removeAnimation(getAnimation(Direction.DOWN));
		removeAnimation(getAnimation(Direction.LEFT));
		removeAnimation(getAnimation(Direction.RIGHT));
		addAnimation(getAnimation(new_direction));
		direction = new_direction;
		setVelocity(speedVectors.get(new_direction.ordinal()));
		this.sendDirectionUpdates(game, "SETDIRECTION");
	}
	
	private void sendDirectionUpdates(EmpireWars game, String categoryType) {
		if (this.objectType == "ORIGINAL" && this.direction != this._direction) {
			String className = this.getClass().getSimpleName().toUpperCase();
			String msg = this.direction.toString();
			Message posUpdate = new Message(
				this.getObjectUUID(), "UPDATE", categoryType, msg, className);
			game.sendPackets.add(posUpdate);
			this._direction = this.direction;
		}
	}
	
	/**
	 * Allows the server to send health updates to the client that owns that player.
	 * 
	 * @param x. How much life to reduce
	 * @param game. Current game state
	 * @param player. The entity to update the health bar
	 */
	private void sendPlayerHealth(EmpireWars game, double x, Player player) {
		String className = this.getClass().getSimpleName().toUpperCase();
		// only this player should process it
		String msg = Double.toString(x);
		Message msgUpdate = new Message(
			this.getObjectUUID(), "UPDATE", "SETPLAYERLIFE", msg, className);
		msgUpdate.setSingleClient(true);
		msgUpdate.setPort(player.getPort());
		msgUpdate.setIpAddress(player.getIpAddress());
		game.sendPackets.add(msgUpdate);
	};
	
	public void bounce(float surfaceTangent) {
		velocity = velocity.bounce(surfaceTangent);
	}
	
	public void update(StateBasedGame game, int delta){
		this.timer --;
		EmpireWars ew = (EmpireWars) game;
		this.networkUpdate(ew);  // network updates
		
		// run this code on the server only 
		if (!ew.getSessionType().equals("SERVER")) {
			return;
		}
		if (this.health.isDead()) {
			this.explode();
			// the entity is dead. No need to go on
			return;
		}
		for (Iterator<HashMap.Entry<UUID, Bullet>> i = ew.getClientBullets().entrySet().iterator(); i.hasNext(); ) {
			Bullet bullet = i.next().getValue();
			if (bullet.collides(this) != null && !bullet.isDestroyed()) {
				if (bullet.team != this.team) {
					this.health.setHealth(-4);
				}
				if (!bullet.getObjectType().equals("ORIGINAL")) {
					bullet.serverSendCollisionUpdates(ew);
				}
				bullet.explode();
			}
		}
		// server player
		if (this.collides(ew.player) != null) {
			if (this.team != ew.player.team)
			{
				ew.player.health.setHealth(-0.02);
			}
		}
		
		for (Iterator<HashMap.Entry<UUID, Player>> i = ew.getClientPlayer().entrySet().iterator(); i.hasNext(); ) {
			Player tempPlayer = i.next().getValue();
			Collision isPen = this.collides(tempPlayer);
			if (isPen != null && this.team != tempPlayer.team) {
				this.sendPlayerHealth(ew, -0.02, tempPlayer);
			}
		}
		
		// network players
		
		for(Iterator<HashMap.Entry<UUID, Creep>> i = ew.creeps.entrySet().iterator(); i.hasNext();){
			HashMap.Entry<UUID, Creep> itr = i.next();
			if (this.collides(itr.getValue()) != null && this.team != itr.getValue().team)
			{
				this.health.setHealth(-0.1);
				itr.getValue().health.setHealth(-0.08);
			}
		
		}
		
		int wallIndex = ew.map.getLayerIndex("walls");
		int minX = (int)(this.getCoarseGrainedMinX()/32);
		int minY = (int)(this.getCoarseGrainedMinY()/32);
		int maxX = (int)(this.getCoarseGrainedMaxX()/32);
		int maxY = (int)(this.getCoarseGrainedMaxY()/32);
		
		if (this.getCoarseGrainedMinX() < 32)
		{
			setX(ew.tileWidth*1.7f);
			minX = 2;
		}
		
		if (this.getCoarseGrainedMinY() < 32)
		{
			setY(ew.tileHeight*1.7f);
			minY = 2;
		}
		
		if (this.getCoarseGrainedMaxX() > ew.mapWidth-32)
		{
			setX(ew.mapWidth - ew.tileWidth*1.7f);
			maxX = (int)(getX()/32.0f);
		}
		
		if (this.getCoarseGrainedMaxY() > ew.mapHeight-32)
		{
			setX(ew.mapHeight - ew.tileHeight*1.7f);
			maxY = (int)(getY()/32.0f);
		}
		
		try
		{
			if (ew.map.getTileId(minX, minY, wallIndex) != 0 ||
					ew.map.getTileId(minX, maxY, wallIndex) != 0 ||
							ew.map.getTileId(maxX, minY, wallIndex) != 0 ||
									ew.map.getTileId(maxX, maxY, wallIndex) != 0)
			{
				if (this.timer <= 0)
				{
					this.timer = this.timer_max;
					Direction new_direction = Direction.values()[(this.direction.ordinal() + 2)%4]; //always reverse when hitting a wall
					/*while(new_direction == this.direction)
					{
						int randNumber = rand.nextInt(4);
						new_direction = Direction.values()[randNumber];
					}*/

					changeDirection(new_direction, ew);
				}
				else
					translate(velocity.scale(delta));
			}
			else		
				translate(velocity.scale(delta));

		}
		catch (Exception e)
		{
			System.out.println(minX);
			System.out.println(minY);
			System.out.println(maxX);
			System.out.println(maxY);
			throw e;
		}
		
		this.setHealthBarPos();
	}
	
	/**
	 * Update the health bar based on the creeps movement.
	 *
	 */
	public void setHealthBarPos() {
		this.health.setPosition(this.getX() - hbXOffset,  this.getY() - hbYOffset);
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
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