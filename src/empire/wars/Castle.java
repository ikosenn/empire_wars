package empire.wars;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.Bullet.BULLET_TYPE;
import empire.wars.EmpireWars.TEAM;
import jig.ResourceManager;
import jig.Vector;


public class Castle extends NetworkEntity {
	TEAM team;
	int timer;
	Random rand;
	
	public Castle(final float x, final float y, final TEAM in_team){
		super(x,y);
		this.team = in_team;
		this.rand = new Random();
		
		if (in_team == TEAM.BLUE)
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.CASTLE_BLUE_RSC));
		else
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.CASTLE_RED_RSC));
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta, int mapWidth, int mapHeight, int tileWidth, int tileHeight)
	{
		EmpireWars ew = (EmpireWars)game;
		timer += delta;
		if (timer > 4000)
		{
			//CastleFire tempCastleFire = new CastleFire(getX(), getY()-130, 0f, 0f, this.team);
			float vx = this.rand.nextFloat() * (0.2f);
			if (rand.nextInt() % 2 == 0)
				vx = -vx;
			
			float vy = (this.team == TEAM.BLUE) ? -0.1f : 0.1f;
			
			Bullet bullet = new Bullet(getX(), getY(), vx, vy, EmpireWars.FIRE_IMAGE_RSC, BULLET_TYPE.CASTLE, this.team);
			ew.clientBullets.put(bullet.getObjectUUID(), bullet);
			
			//tempCastleFire.setVelocity(new Vector(vx, vy));
			//ew.clientCastleFires.put(tempCastleFire.getObjectUUID(), tempCastleFire);
			this.timer = 0;
		}
	}
}