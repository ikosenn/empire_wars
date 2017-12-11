package empire.wars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.net.Message;


public class PlayState extends BasicGameState {
	
	public int game_timer = 0;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		EmpireWars ew = (EmpireWars)game;
		
		if (ew.getSessionType() == "SERVER") {
			ew.createOnServer();
		}
		ew.createOnClients();
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		EmpireWars ew = (EmpireWars) game;
		ew.camera.translate(g, ew.player);
		ew.map.render(0, 0);
		ew.player.render(g);
		for (Iterator<HashMap.Entry<UUID, Creep>> i = ew.creeps.entrySet().iterator(); i.hasNext(); ) {
			HashMap.Entry<UUID, Creep> itr = i.next();
			itr.getValue().render(g);
			itr.getValue().health.render(g);
		}
		
		// render other client stuff
		for (Iterator<HashMap.Entry<UUID, Player>> i = ew.getClientPlayer().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		// bullets
		for (Iterator<HashMap.Entry<UUID, Bullet>> i = ew.getClientBullets().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		ew.flag_test.render(g);
		for(Flag f:ew.flags){
			f.render(g);
		}
		
		g.setColor(Color.black);
		g.fillRect((-1 * ew.camera.getXIndicator()),(-1 * ew.camera.getYIndicator()),EmpireWars.SCREEN_WIDTH,35);
		g.setColor(Color.white);
		g.drawString("Time Left: "+ (150000 - game_timer)/ 60000 + ":" + ((150000 - game_timer) % 60000 / 1000) ,(-1 * ew.camera.getXIndicator() + 440),(-1 * ew.camera.getYIndicator())+10);
		ew.player.render(g);
		
	}
	

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		EmpireWars ew = (EmpireWars) game;
		game_timer += delta;
		ew.player.update(container, game, delta, ew.mapWidth, ew.mapHeight, ew.tileWidth, ew.tileHeight);
		
		for (Iterator<HashMap.Entry<UUID, Creep>> i = ew.creeps.entrySet().iterator(); i.hasNext(); ) {
			if (i.next().getValue().health.isDead())
			{
				i.remove();
			}
		}
		
		for (Iterator<HashMap.Entry<UUID, Creep>> i = ew.creeps.entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update(game, delta);
		}
					
		// process network message
		for (Iterator<Message> i = ew.receivedPackets.iterator(); i.hasNext(); ) {
			Message.determineHandler(i.next(), ew);
			i.remove();
		}
		// remove bullets
		HashMap<UUID, Bullet> bulletMap = ew.getClientBullets();
		bulletMap.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		
		ew.flag_test.update(container, game, delta);
		for(Flag f:ew.flags){
			f.update(container, game, delta);
		}
	}

	@Override
	public int getID() {
		return EmpireWars.PLAY_STATE_ID;
	}

}
