package empire.wars;


import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.ResourceManager;

/**
 * Implements the menu screen. The menu screen will have the option to 
 * play the game, view the high scores, and exit the game
 * 
 * Menu buttons resource courtesy of verique
 * https://opengameart.org/content/fantasy-buttons-0
 * 
 * @author peculiaryak
 *
 */


public class MenuState extends BasicGameState {
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		AppGameContainer gc = (AppGameContainer) container;
		gc.setDisplayMode(EmpireWars.SCREEN_WIDTH ,EmpireWars.SCREEN_HEIGHT, false);		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(ResourceManager.getImage(EmpireWars.LOGO_IMG_RSC), 190, 10);
		g.drawImage(
			ResourceManager.getSpriteSheet(EmpireWars.MENU_BUTTONS_RSC, 1000, 707).getSubImage(
					650, 400, 260, 300), 380, 200);
		g.drawImage(
				ResourceManager.getSpriteSheet(EmpireWars.MENU_BUTTONS_RSC, 1000, 707).getSubImage(
						650, 400, 260, 300), 380, 400);
		g.drawString("Play", 490, 325);
		g.drawString("Exit", 490, 525);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input input = container.getInput();
		
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			int mouseX = input.getMouseX();
			int mouseY = input.getMouseY();
			
			// play button
			if ((mouseX > 424 && mouseX < 604) && (mouseY > 297 && mouseY < 383)) {
				game.enterState(EmpireWars.SESSION_STATE_ID);
			}
			//  click on exit button
			if ((mouseX > 424 && mouseX < 604) && (mouseY > 495 && mouseY < 585)) {
				System.exit(0);
			}
		}
		
	}

	@Override
	public int getID() {
		return EmpireWars.MENU_STATE_ID;
	}

}
