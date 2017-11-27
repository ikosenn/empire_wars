package empire.wars;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import empire.wars.Castle.TEAM;
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
	
	public enum Direction
	{
	  UP, 
	  RIGHT, 
	  DOWN, 
	  LEFT
	}
	
	public enum Team
	{
	 RED,
	 BLUE
	}
	
	
	public final static int PLAY_STATE_ID = 1;
	public final static  int GAMEOVERSTATE_ID = 2;
	public final static int SPLASH_SCREEN_STATE_ID = 0;
	public final static int SCREEN_WIDTH = 1024;
	public final static int SCREEN_HEIGHT = 768;
	public final static int SCREEN_SMALL_WIDTH = 900;
	public final static int SCREEN_SMALL_HEIGHT = 600;
	
	public final static float PLAYER_SPEED = 0.50f;
	
	TiledMap map;
	Player player;
	Camera camera;
	int mapHeight, mapWidth;
	int tileHeight, tileWidth;
	
	public static final String PLAYER_IMG_RSC = "images/hero.png";
	public static final String SPLASH_SCREEN_IMG_RSC = "images/splash.png";
	public static final String LOGO_IMG_RSC = "images/logo.png";
	
	public static final String CREEP_UP_IMG_RSC = "images/creep_up.png";
	public static final String CREEP_DOWN_IMG_RSC = "images/creep_down.png";
	public static final String CREEP_LEFT_IMG_RSC = "images/creep_left.png";
	public static final String CREEP_RIGHT_IMG_RSC = "images/creep_right.png";
	
	public static final String BLUE_PLAYER_UP_IMG_RSC = "images/blue_up.png";
	public static final String BLUE_PLAYER_DOWN_IMG_RSC = "images/blue_down.png";
	public static final String BLUE_PLAYER_LEFT_IMG_RSC = "images/blue_left.png";
	public static final String BLUE_PLAYER_RIGHT_IMG_RSC = "images/blue_right.png";
	
	public static final String RED_PLAYER_UP_IMG_RSC = "images/red_up.png";
	public static final String RED_PLAYER_DOWN_IMG_RSC = "images/red_down.png";
	public static final String RED_PLAYER_LEFT_IMG_RSC = "images/red_left.png";
	public static final String RED_PLAYER_RIGHT_IMG_RSC = "images/red_right.png";
	
	// network related values
	ArrayList<Message> receivedPackets = new ArrayList<Message>();
	ArrayList<Message> sendPackets  = new ArrayList<Message>();
	
	// stupid way to track other client entities
	// stupid way works best sometimes
	ArrayList<Player> clientPlayer = new ArrayList<>();
	
	ArrayList<Creep> creeps;
	
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

		ResourceManager.loadImage(CREEP_UP_IMG_RSC);
		ResourceManager.loadImage(CREEP_DOWN_IMG_RSC);
		ResourceManager.loadImage(CREEP_LEFT_IMG_RSC);
		ResourceManager.loadImage(CREEP_RIGHT_IMG_RSC);
		
		ResourceManager.loadImage(BLUE_PLAYER_UP_IMG_RSC);
		ResourceManager.loadImage(BLUE_PLAYER_DOWN_IMG_RSC);
		ResourceManager.loadImage(BLUE_PLAYER_LEFT_IMG_RSC);
		ResourceManager.loadImage(BLUE_PLAYER_RIGHT_IMG_RSC);
		
		ResourceManager.loadImage(RED_PLAYER_UP_IMG_RSC);
		ResourceManager.loadImage(RED_PLAYER_DOWN_IMG_RSC);
		ResourceManager.loadImage(RED_PLAYER_LEFT_IMG_RSC);
		ResourceManager.loadImage(RED_PLAYER_RIGHT_IMG_RSC);
		
		
		map = new TiledMap("src/tilemaps/maze.tmx");
		mapWidth = map.getWidth() * map.getTileWidth();
		mapHeight = map.getHeight() * map.getTileHeight();
		
		tileHeight = map.getTileHeight();
        tileWidth = map.getTileWidth();
        player = new Player(tileWidth*4, tileHeight*4, 0, 0, Team.BLUE);
        camera = new Camera(map, mapWidth, mapHeight);
        
        creeps = new ArrayList<Creep>();
        
        Random rand = new Random();
        int roadIndex = map.getLayerIndex("road");
        int wallIndex = map.getLayerIndex("walls");
        
        for (int i = 0; i< 30; i++)
        {
        	int xTilePos, yTilePos;
        	while(true)
        	{
        		xTilePos = rand.nextInt(this.mapWidth/tileWidth);
            	yTilePos = rand.nextInt(this.mapHeight/tileHeight);
            	if (map.getTileId(xTilePos, yTilePos, roadIndex) != 0 && map.getTileId(xTilePos, yTilePos, wallIndex) == 0)
            	{
            		if (xTilePos - 1 <= 0 || map.getTileId(xTilePos-1, yTilePos, wallIndex) != 0)
            			continue;
            		
            		if (yTilePos - 1 <= 0 || map.getTileId(xTilePos, yTilePos-1, wallIndex) != 0)
            			continue;
            		
            		if (xTilePos + 1 >= (int)mapWidth/tileWidth || map.getTileId(xTilePos+1, yTilePos, wallIndex) != 0)
            			continue;
            		
            		if (yTilePos + 1 >= (int)mapHeight/tileHeight || map.getTileId(xTilePos, yTilePos+1, wallIndex) != 0)
            			continue;
            		
            		break;
            	}
        	}
        	creeps.add(new Creep(tileWidth*xTilePos, tileHeight*yTilePos));	
        }
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
