package empire.wars;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.net.Message;


public class PlayState extends BasicGameState {
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		EmpireWars ew = (EmpireWars) game;
		ew.camera.translate(g, ew.player);
		ew.map.render(0, 0);
		ew.player.render(g);
		
		for (Creep creep : ew.creeps)
		{
			creep.render(g);
		}
		
		// render other client stuff
		for (Iterator<Player> i = ew.getClientPlayer().iterator(); i.hasNext(); ) {
			i.next().render(g);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		EmpireWars ew = (EmpireWars) game;
		ew.player.update(container, game, delta, ew.mapWidth, ew.mapHeight, ew.tileWidth, ew.tileHeight);
		
		for (Creep creep : ew.creeps)
			creep.update(game, delta);
		
		// process network message
		for (Iterator<Message> i = ew.receivedPackets.iterator(); i.hasNext(); ) {
			Message.determineHandler(i.next(), ew);
			i.remove();
		}
	}

	@Override
	public int getID() {
		return EmpireWars.PLAY_STATE_ID;
	}

}
