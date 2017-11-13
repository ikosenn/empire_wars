package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class PlayState extends BasicGameState {
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		EmpireWars ew = (EmpireWars) game;
		ew.camera.translate(g, ew.player);
		ew.map.render(0, 0);
		ew.player.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		EmpireWars ew = (EmpireWars) game;
		ew.player.update(container, game, delta, ew.mapWidth, ew.mapHeight, ew.tileWidth, ew.tileHeight);
	}

	@Override
	public int getID() {
		return EmpireWars.PLAY_STATE_ID;
	}

}
