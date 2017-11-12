package empire.wars;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

public class Camera {
	private int x, y;
	private int mapWidth, mapHeight;
	private Rectangle viewPort;
	
    public Camera(TiledMap map, int mapWidth, int mapHeight) {
        x = 0;
        y = 0;
        viewPort = new Rectangle(0, 0, EmpireWars.SCREEN_WIDTH, EmpireWars.SCREEN_HEIGHT);
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }
    
    public void translate(Graphics g, Player player) {

        if (player.getX() - EmpireWars.SCREEN_WIDTH / 2 + 16 < 0) {
            x = 0;
        } else if (player.getX() + EmpireWars.SCREEN_WIDTH / 2 + 16 > mapWidth) {
            x = -mapWidth + EmpireWars.SCREEN_WIDTH;
        } else {
            x = (int) -player.getX() + EmpireWars.SCREEN_WIDTH / 2 - 16;
        }

        if (player.getY() - EmpireWars.SCREEN_HEIGHT / 2 + 16 < 0) {
            y = 0;
        } else if (player.getY() + EmpireWars.SCREEN_HEIGHT / 2 + 16 > mapHeight) {
            y = -mapHeight + EmpireWars.SCREEN_HEIGHT;
        } else {
            y = (int) - player.getY() + EmpireWars.SCREEN_HEIGHT / 2 - 16;
        }
        g.translate(x, y);
        viewPort.setX(-x);
        viewPort.setY(-y);
    }
	
}
