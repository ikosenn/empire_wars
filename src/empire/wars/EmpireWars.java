package empire.wars;

import java.util.ArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import empire.wars.net.Message;
import jig.Entity;
import jig.ResourceManager;


/**
 * Empire Wars is a 2D multi-player online battle arena video game. 
 * There are two teams in the game: Red team and the Blue team, and the 
 * objective is to acquire as many flags as possible in the field.
 * 
 * @author priya, peculiaryak, jun, shubham
 *
 */
public class EmpireWars extends StateBasedGame {
	
	public final static int PLAY_STATE_ID = 1;
	public final static  int GAMEOVERSTATE_ID = 2;
	public final static int SPLASH_SCREEN_STATE_ID = 0;
	public final static int SCREEN_WIDTH = 1024;
	public final static int SCREEN_HEIGHT = 768;
	public final static int SCREEN_SMALL_WIDTH = 900;
	public final static int SCREEN_SMALL_HEIGHT = 600;
	
	public final static float PLAYER_SPEED = 0.50f;
	public final static float PLAYER_BULLETSPEED = 0.30f;
	
	TiledMap map;
	Player player;
	Camera camera;
	int mapHeight, mapWidth;
	int tileHeight, tileWidth;
	
	//image resources
	public static final String PLAYER_IMG_RSC = "images/hero.png";
	public static final String PLAYER_BULLETIMG_RSC = "images/player_bullet.gif";
	public static final String SPLASH_SCREEN_IMG_RSC = "images/splash.png";
	public static final String LOGO_IMG_RSC = "images/logo.png";
	
	//sound resources
	public static final String PLAYER_SHOOTSND_RSC = "sounds/gun_shot.wav";
	
	// network related values
	ArrayList<Message> receivedPackets = new ArrayList<Message>();
	ArrayList<Message> sendPackets  = new ArrayList<Message>();
	
	// stupid way to track other client entities
	// stupid way works best sometimes
	ArrayList<Player> clientPlayer = new ArrayList<>();
	
	public EmpireWars(String title) {
		super(title);
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
	}
	
	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		// add game states
		addState(new SplashScreenState());
		addState(new PlayState());
		addState(new GameOverState());
		
		ResourceManager.loadImage(PLAYER_IMG_RSC);
		ResourceManager.loadImage(SPLASH_SCREEN_IMG_RSC);
		ResourceManager.loadImage(LOGO_IMG_RSC);
		ResourceManager.loadImage(PLAYER_BULLETIMG_RSC);
		
		ResourceManager.loadSound(PLAYER_SHOOTSND_RSC);
		
		
		map = new TiledMap("src/tilemaps/maze.tmx");
		mapWidth = map.getWidth() * map.getTileWidth();
		mapHeight = map.getHeight() * map.getTileHeight();
		
		tileHeight = map.getTileHeight();
        tileWidth = map.getTileWidth();
        player = new Player(tileWidth*4, tileHeight*4, 0, 0);
        camera = new Camera(map, mapWidth, mapHeight);
	}
	
	
	/**
	 * ClientPlayer getter
	 * @return Player. Returns and array-list of other client player.
	 */
	public ArrayList<Player> getClientPlayer() {
		return clientPlayer;
	}
	
	/**
	 * Tile Height getter
	 * @return float. The height of a single tile in the map
	 */
	public float getTileHeight() {
		return this.tileHeight;
	}
	
	/**
	 * Tile Width getter
	 * @return float. The width of a single tile in the map
	 */
	public float getTileWidth() {
		return this.tileWidth;
	}
	
	public  static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new EmpireWars("Empire Wars"));
			app.setDisplayMode(EmpireWars.SCREEN_SMALL_WIDTH, EmpireWars.SCREEN_SMALL_HEIGHT, false);
			app.setShowFPS(false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

}
