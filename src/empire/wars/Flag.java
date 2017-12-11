package empire.wars;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;
import empire.wars.EmpireWars.TEAM;
import jig.ResourceManager;
import jig.Vector;

public class Flag extends NetworkEntity {
	public TEAM team;
	public int player_stay_timer = 3000; // player has to stay for 3 sec to change the color
	public Vector flagTileIdx;
	
	public Flag(final float x, final float y){
		super(x, y);
		flagTileIdx = new Vector(x/32, y/32);
		team = TEAM.GREY;
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_GREYIMG_RSC));
	}
	
	public Flag(final float x, final float y, final TEAM team){
		super(x, y);
		flagTileIdx = new Vector(x/32, y/32);
		this.team = team;
		if(team == TEAM.BLUE){
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
		}else if(team == TEAM.RED){
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
		}else{
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_GREYIMG_RSC));
		}
	}
	
	public void changeTeam(){
		if(team == TEAM.BLUE){
			removeImage(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
		}else if(team == TEAM.RED){
			removeImage(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
		}
	}
	
	private Vector getTileIdx(Vector v){
		return new Vector(v.getX()/32, v.getY()/32);
	}
	
	private boolean playerIsNear(Vector playerTileIdx){
		for(int i=-1; i<2; i++){
			for(int j=-1; j<2; j++){
				if(i==0&&j==0){
					continue;
				}
				if((int)playerTileIdx.getX() == (int)(flagTileIdx.getX()+i)
						&& (int)playerTileIdx.getY() == (int)(flagTileIdx.getY()+j)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void update(GameContainer container, StateBasedGame game,final int delta) {
		EmpireWars ew = (EmpireWars) game;
		
		//examine this flag's neighborhood to check if there is a player
		Vector playerTileIdx = getTileIdx(ew.player.getPosition());
		if(ew.player.team != team && playerIsNear(playerTileIdx)){
			player_stay_timer -= delta;
		}else{
			player_stay_timer = 3000;
		}
		
		//if the player has stayed for enough time, change the flag's color
		if(player_stay_timer <= 0){
			if(team == TEAM.GREY){
				removeImage(ResourceManager.getImage(EmpireWars.FLAG_GREYIMG_RSC));
				if(ew.player.team == TEAM.RED){
					team = TEAM.RED;
					addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
				}else if(ew.player.team == TEAM.BLUE){
					team = TEAM.BLUE;
					addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
				}
			}else{
				changeTeam();
			}
			player_stay_timer = 3000;
		}
		
	}
	
	@Override
	public void render(final Graphics g) {
		super.render(g);
		float x = this.getX() - 0;  
		float y = this.getY() - 25;
		if(player_stay_timer < 3000){
			g.setColor(Color.blue);
			g.fillRect(x, y, 20.0f*player_stay_timer/3000, 5);
		}
		
	}
	
	
	
}
