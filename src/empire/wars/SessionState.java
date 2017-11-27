package empire.wars;

import java.awt.Font;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.ResourceManager;

public class SessionState extends BasicGameState {
	private TextField txtField;
	private TrueTypeFont ttf;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
		Font font = new Font("Verdana", Font.BOLD, 20);
		ttf = new TrueTypeFont(font, false);
		txtField = new TextField(container, ttf, 350, 170, 450, 40);
		txtField.setBackgroundColor(Color.white);
		txtField.setTextColor(Color.black);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		EmpireWars ew = (EmpireWars)game;
		if (ew.getUsername() != null) {
			txtField.setText(ew.getUsername());
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {	
		txtField.render(container, g);
		g.drawImage(ResourceManager.getImage(EmpireWars.LOGO_IMG_RSC), 190, 10);
		
		g.drawImage(
				ResourceManager.getSpriteSheet(EmpireWars.MENU_BUTTONS_RSC, 1000, 707).getSubImage(
						650, 400, 260, 300), 380, 200);
			g.drawImage(
					ResourceManager.getSpriteSheet(EmpireWars.MENU_BUTTONS_RSC, 1000, 707).getSubImage(
							650, 400, 260, 300), 380, 400);
			g.drawString("Host", 490, 325);
			g.drawString("Join Session", 455, 525);
			g.setFont(ttf);
			g.drawString("Username:", 210, 180);
			g.resetFont();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input input = container.getInput();
		EmpireWars ew = (EmpireWars)game;
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			int mouseX = input.getMouseX();
			int mouseY = input.getMouseY();
			
			// host button
			if ((mouseX > 424 && mouseX < 604) && (mouseY > 297 && mouseY < 383)) {
				try {
					DatagramSocket serverSock = new DatagramSocket(
						EmpireWars.SERVER_PORT, InetAddress.getByName("0.0.0.0"));
					serverSock.setBroadcast(true);
					ew.startUpServers(serverSock);
					game.enterState(EmpireWars.PLAY_STATE_ID);
				} catch (SocketException | UnknownHostException e) {
					e.printStackTrace();
				}
			}
			//  join session
			if ((mouseX > 424 && mouseX < 604) && (mouseY > 495 && mouseY < 585)) {
				try {
					DatagramSocket clientSock = new DatagramSocket();
					clientSock.setBroadcast(true);
					ew.startUpServers(clientSock);
					game.enterState(EmpireWars.PLAY_STATE_ID);
				} catch (SocketException e) {
					e.printStackTrace();
				}
			}
		}
		
		// change to play state for client
		
	}

	@Override
	public int getID() {
		return EmpireWars.SESSION_STATE_ID;
	}

}
