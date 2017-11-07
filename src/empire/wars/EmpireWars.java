package empire.wars;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;


/**
 * Empire Wars is a 2D multi-player online battle arena video game. 
 * There are two teams in the game: Red team and the Blue team, and the 
 * objective is to acquire as many flags as possible in the field.
 * 
 * @author priya, peculiaryak, jun, shubham
 *
 */
public class EmpireWars extends StateBasedGame {
	
	public final static int PLAY_STATE_ID = 0;
	public final static  int GAMEOVERSTATE_ID = 1;
	public final static int SCREEN_WIDTH = 1200;
	public final static int SCREEN_HEIGHT = 900;
	
	
	public EmpireWars(String title) {
		super(title);
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
	}
	
	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		// add game states
		addState(new PlayState());
		addState(new GameOverState());
	}
	
	public  static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new EmpireWars("Empire Wars"));
			app.setDisplayMode(EmpireWars.SCREEN_WIDTH, EmpireWars.SCREEN_HEIGHT, false);
			app.setShowFPS(false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

}
