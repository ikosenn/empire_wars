package empire.wars;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import empire.wars.Castle.TEAM;
import empire.wars.net.ConnectedPlayers;
import empire.wars.net.GameClient;
import empire.wars.net.GameServer;
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
	
	public final static int SPLASH_SCREEN_STATE_ID = 0;
	public final static int MENU_STATE_ID = 1;
	public final static int SESSION_STATE_ID = 2;
	public final static int SERVER_LOBBY_STATE_ID = 3;
	public final static int CLIENT_LOBBY_STATE_ID = 4;
	public final static int PLAY_STATE_ID = 5;
	public final static  int GAMEOVERSTATE_ID = 6;
	
	public final static int SCREEN_WIDTH = 1024;
	public final static int SCREEN_HEIGHT = 768;
	public final static int SCREEN_SMALL_WIDTH = 900;
	public final static int SCREEN_SMALL_HEIGHT = 600;
	public final static int SERVER_PORT = 1323;
	public final static int THREAD_SLEEP_TIME = 0;
	
	private GameClient socketClient;
	private GameServer socketServer;
	
	private String username;
	
	public final static float PLAYER_SPEED = 0.40f;
	public final static float PLAYER_BULLETSPEED = 0.50f;
	
	TiledMap map;
	Player player;
	Camera camera;
	int mapHeight, mapWidth;
	int tileHeight, tileWidth;
	
	//image resources
	public static final String PLAYER_IMG_RSC = "images/background.gif";
	public static final String PLAYER_MOVINGIMG_RSC = "images/jun.png";
	public static final String PLAYER_BULLETIMG_RSC = "images/player_bullet.gif";
	public static final String SPLASH_SCREEN_IMG_RSC = "images/splash.png";
	public static final String LOGO_IMG_RSC = "images/logo.png";
	public static final String MENU_BUTTONS_RSC = "images/menu_buttons.png";
	
	//sound resources
	public static final String PLAYER_SHOOTSND_RSC = "sounds/gun_shot.wav";
	
	public static final String CREEP_UP_IMG_RSC = "images/creep_up.png";
	public static final String CREEP_DOWN_IMG_RSC = "images/creep_down.png";
	public static final String CREEP_LEFT_IMG_RSC = "images/creep_left.png";
	public static final String CREEP_RIGHT_IMG_RSC = "images/creep_right.png";
	
	public static final String BLUE_PLAYER_MOVING_IMG_RSC = "images/blue_players.png";
	public static final String BLUE_PLAYER_UP_IMG_RSC = "images/blue_up.png";
	public static final String BLUE_PLAYER_DOWN_IMG_RSC = "images/blue_down.png";
	public static final String BLUE_PLAYER_LEFT_IMG_RSC = "images/blue_left.png";
	public static final String BLUE_PLAYER_RIGHT_IMG_RSC = "images/blue_right.png";
	
	public static final String RED_PLAYER_MOVING_IMG_RSC = "images/red_players.png";
	public static final String RED_PLAYER_UP_IMG_RSC = "images/red_up.png";
	public static final String RED_PLAYER_DOWN_IMG_RSC = "images/red_down.png";
	public static final String RED_PLAYER_LEFT_IMG_RSC = "images/red_left.png";
	public static final String RED_PLAYER_RIGHT_IMG_RSC = "images/red_right.png";
	
	// network related values
	String sessionType;  // whether I am just a client or a client with a "server".
	Queue<Message> receivedPackets = new ConcurrentLinkedQueue<Message>();
	// server sends this to all clients
	// A client sends to the server only
	Queue<Message> sendPackets  = new ConcurrentLinkedQueue<Message>();
	ArrayList<ConnectedPlayers> connectedPlayers = new ArrayList<>();
	ConnectedPlayers broadcastServer;
	
	// stupid way to track other client entities
	// stupid way works best sometimes
	ArrayList<Player> clientPlayer = new ArrayList<>();
	
	ArrayList<Creep> creeps = new ArrayList<Creep>();
	
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
		addState(new SessionState());
		addState(new MenuState());
		addState(new ServerLobbyState());
		addState(new ClientLobbyState());
		
		ResourceManager.loadImage(PLAYER_IMG_RSC);
		ResourceManager.loadImage(PLAYER_MOVINGIMG_RSC);
		ResourceManager.loadImage(SPLASH_SCREEN_IMG_RSC);
		ResourceManager.loadImage(LOGO_IMG_RSC);

		ResourceManager.loadImage(PLAYER_BULLETIMG_RSC);
		ResourceManager.loadSound(PLAYER_SHOOTSND_RSC);
		ResourceManager.loadImage(MENU_BUTTONS_RSC);

		ResourceManager.loadImage(CREEP_UP_IMG_RSC);
		ResourceManager.loadImage(CREEP_DOWN_IMG_RSC);
		ResourceManager.loadImage(CREEP_LEFT_IMG_RSC);
		ResourceManager.loadImage(CREEP_RIGHT_IMG_RSC);
		
		ResourceManager.loadImage(BLUE_PLAYER_MOVING_IMG_RSC);
		ResourceManager.loadImage(BLUE_PLAYER_UP_IMG_RSC);
		ResourceManager.loadImage(BLUE_PLAYER_DOWN_IMG_RSC);
		ResourceManager.loadImage(BLUE_PLAYER_LEFT_IMG_RSC);
		ResourceManager.loadImage(BLUE_PLAYER_RIGHT_IMG_RSC);
		
		ResourceManager.loadImage(RED_PLAYER_MOVING_IMG_RSC);
		ResourceManager.loadImage(RED_PLAYER_UP_IMG_RSC);
		ResourceManager.loadImage(RED_PLAYER_DOWN_IMG_RSC);
		ResourceManager.loadImage(RED_PLAYER_LEFT_IMG_RSC);
		ResourceManager.loadImage(RED_PLAYER_RIGHT_IMG_RSC);

		map = new TiledMap("src/tilemaps/maze.tmx");
		mapWidth = map.getWidth() * map.getTileWidth();
		mapHeight = map.getHeight() * map.getTileHeight();
		
		tileHeight = map.getTileHeight();
        tileWidth = map.getTileWidth();
        player = new Player(tileWidth*4, tileHeight*4, 0, 0, TEAM.BLUE);
        camera = new Camera(map, mapWidth, mapHeight);
	}
	
	
	public void createOnServer() {
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
        	creeps.add(new Creep(tileWidth * xTilePos, tileHeight * yTilePos));	
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
	
	/*
	 * username getter
	 */
	public String getUsername() {
		return username;
	}

	/*
	 * username setter
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/*
	 * clientType getter
	 */
	public String getSessionType() {
		return sessionType;
	}

	/**
	 * clientType getter
	 * @param clientType. The clientType
	 */
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	
	/*
	 * connected-players getter
	 */
	public ArrayList<ConnectedPlayers> getConnectedPlayers() {
		return this.connectedPlayers;
	}
	
	/*
	 * Adds a new client to the connected player arraylist.
	 */
	public void appendConnectedPlayers(ConnectedPlayers player) {
		// check if player exists first.
		for (Iterator<ConnectedPlayers> i = this.getConnectedPlayers().iterator(); i.hasNext(); ) {
			ConnectedPlayers tempPlayer = i.next();
			if (player.getUsername().equals(tempPlayer.getUsername())) {
				return;
			}
		}
		this.connectedPlayers.add(player);
	}
	
	/*
	 * send packets getter
	 */
	public Queue<Message> getSendPackets() {
		return this.sendPackets;
	}
	
	/*
	 * Adds a new messages in the send queue.
	 */
	public void appendSendPackets(Message msg) {
		this.sendPackets.add(msg);
	}
	
	/*
	 * Removes sent from send queue.
	 */
	public void popSendPackets(Message msg) {
		this.sendPackets.remove(msg);
	}
	
	/*
	 * receivedpackets getter.
	 */
	public Queue<Message> getReceivedPackets() {
		return this.receivedPackets;
	}
	
	/*
	 * Adds a new messages in the received queue.
	 */
	public void appendReceivedPackets(Message msg) {
		this.receivedPackets.add(msg);
	}
	
	/**
	 * broadCastServer getter.
	 * 
	 */
	public ConnectedPlayers getBroadcastServer() {
		return broadcastServer;
	}

	/**
	 * Creates a connectPlayer object that points to where the server is located
	 */
	public void setBroadcastServer(ConnectedPlayers broadcastServer) {
		this.broadcastServer = broadcastServer;
	}
	
	/*
	 * Creates the client and server instances.
	 * This will be on separate threads
	 */
	public void startUpServers(DatagramSocket s) {
		this.socketClient = new GameClient(this, s);
		this.socketServer = new GameServer(this, s);
		socketClient.start();
		socketServer.start();
	};
	
	/**
	 * creep getter
	 */
	public ArrayList<Creep> getCreeps() {
		return this.creeps;
	}

	/**
	 * Socket client getter
	 * 
	 */
	public GameClient getSocketClient() {
		return this.socketClient;
	}
	
	public  static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new EmpireWars("Empire Wars"));
			app.setDisplayMode(EmpireWars.SCREEN_SMALL_WIDTH, EmpireWars.SCREEN_SMALL_HEIGHT, false);
			app.setShowFPS(false);
			app.setVSync(true);
			app.setAlwaysRender(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}