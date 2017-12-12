package empire.wars;

import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.net.Message;


public class PlayState extends BasicGameState {
	
	public int game_timer = 0;
	private TrueTypeFont ttf;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		Font font = new Font("Comic Sans MS", Font.PLAIN, 16);
		ttf = new TrueTypeFont(font, false);
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
		
		for (Iterator<HashMap.Entry<UUID, Flag>> i = ew.getFlags().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		for (Iterator<HashMap.Entry<UUID, Creep>> i = ew.creeps.entrySet().iterator(); i.hasNext(); ) {
			HashMap.Entry<UUID, Creep> itr = i.next();
			itr.getValue().render(g);
		}

		for (Iterator<HashMap.Entry<UUID, Player>> i = ew.getClientPlayer().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}

		for (Iterator<HashMap.Entry<UUID, HeartPowerUp>> i = ew.getHeartPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		for (Iterator<HashMap.Entry<UUID, BananaPowerUp>> i = ew.getBananaPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		// bullets
		for (Iterator<HashMap.Entry<UUID, Bullet>> i = ew.getClientBullets().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		g.setFont(ttf);
		g.setColor(Color.black);
		g.fillRect((-1 * ew.camera.getXIndicator()),(-1 * ew.camera.getYIndicator()),EmpireWars.SCREEN_WIDTH,35);
		g.setColor(Color.white);
		g.drawString("Time Left: "+ (150000 - game_timer)/ 60000 + ":" + ((150000 - game_timer) % 60000 / 1000) ,(-1 * ew.camera.getXIndicator() + 440),(-1 * ew.camera.getYIndicator())+10);
		g.drawString("Lives: "  + ew.getLives(), (-1 * ew.camera.getXIndicator() + 20),(-1 * ew.camera.getYIndicator()) + 10);
		g.setColor(Color.red);
		g.drawString(
			"Red: "  + ew.getScore().getRedTeam(), (-1 * ew.camera.getXIndicator() + 830),(-1 * ew.camera.getYIndicator()) + 10);
		g.setColor(Color.blue);
		g.drawString(
			"Blue: "  + ew.getScore().getBlueTeam(), (-1 * ew.camera.getXIndicator() + 930),(-1 * ew.camera.getYIndicator()) + 10);
		ew.player.render(g);
		
	}
	

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		EmpireWars ew = (EmpireWars) game;
		game_timer += delta;
		ew.player.update(container, game, delta, ew.mapWidth, ew.mapHeight, ew.tileWidth, ew.tileHeight);
		ew.getScore().update(game);
		
		for (Iterator<HashMap.Entry<UUID, Creep>> i = ew.creeps.entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update(game, delta);
		}
		
		for (Iterator<HashMap.Entry<UUID, HeartPowerUp>> i = ew.getHeartPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update();
		}
		
		for (Iterator<HashMap.Entry<UUID, BananaPowerUp>> i = ew.getBananaPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update();
		}
		
		for (Iterator<HashMap.Entry<UUID, Bullet>> i = ew.getClientBullets().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update(container, game, delta, ew.mapWidth, ew.mapHeight, ew.tileWidth, ew.tileHeight);
		}
		
		for (Iterator<HashMap.Entry<UUID, Flag>> i = ew.getFlags().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update(container, game, delta);
		}
					
		// process network message
		for (Iterator<Message> i = ew.receivedPackets.iterator(); i.hasNext(); ) {
			Message.determineHandler(i.next(), ew);
			i.remove();
		}
				
		// remove bullets
		HashMap<UUID, Bullet> bulletMap = ew.getClientBullets();
		bulletMap.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		// remove heart power-up
		HashMap<UUID, HeartPowerUp> heartPowerup = ew.getHeartPowerup();
		heartPowerup.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		
		// remove heart power-up
		HashMap<UUID, BananaPowerUp> bananaPowerup = ew.getBananaPowerup();
		bananaPowerup.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		
		// remove creep 
		HashMap<UUID, Creep> creepMap = ew.getCreeps();
		creepMap.entrySet().removeIf(entry->entry.getValue().isExploded() == true);

	}

	@Override
	public int getID() {
		return EmpireWars.PLAY_STATE_ID;
	}

}
