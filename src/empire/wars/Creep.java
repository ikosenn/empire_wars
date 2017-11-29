package empire.wars;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.EmpireWars.Direction;
import empire.wars.net.Message;
import jig.ResourceManager;
import jig.Vector;

public class Creep extends NetworkEntity {
	private Vector velocity;
	private Direction direction;
	
	Random rand = new Random();
	int timer = 0;
	final int timer_max = 5;
	
	public static ArrayList<Vector> speedVectors = new ArrayList<Vector>() {{
		add(new Vector(0f,-0.1f));
		add(new Vector(0.1f,0f));
		add(new Vector(0f,0.1f));
		add(new Vector(-0.1f,0f));
	}};
	
	public Creep(final float x, final float y){
		super(x,y);
		int randNumber = rand.nextInt(4);
		direction = Direction.values()[randNumber];
		addImageWithBoundingBox(ResourceManager.getImage(getImageName(direction)));
		setVelocity(speedVectors.get(randNumber));
	}
	
	public String getImageName(Direction direction)
	{
		switch (direction)
		{
		case UP:
			return EmpireWars.CREEP_UP_IMG_RSC;
		case DOWN:
			return EmpireWars.CREEP_DOWN_IMG_RSC;
		case LEFT:
			return EmpireWars.CREEP_LEFT_IMG_RSC;
		case RIGHT:
			return EmpireWars.CREEP_RIGHT_IMG_RSC;
		default:
			return "";
		}
	}
	
	public void changeDirection(Direction new_direction, EmpireWars game)
	{
		removeImage(ResourceManager.getImage(getImageName(direction)));
		addImageWithBoundingBox(ResourceManager.getImage(getImageName(new_direction)));
		direction = new_direction;
		setVelocity(speedVectors.get(new_direction.ordinal()));
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
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}
}