package empire.wars;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import empire.wars.Castle.TEAM;
import jig.Entity;

/**
 * Implements the player health.
 * @author peculiaryak
 *
 */
public class HealthBar extends NetworkEntity {
	private int maxHealth = 16;
	private int currentHealth;
	TEAM team;
	
	public HealthBar(float x, float y, TEAM team) {
		super(x, y);
		this.currentHealth = this.maxHealth;
		this.team = team;
	}
	/**
	 * Determine if the player is dead.
	 * A player is dead when their health bar gets to zero
	 * @return true if player is dead otherwise false.
	 */
	public boolean isDead() {
		if (this.currentHealth <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Use to set the health of the player.
	 * Use negative x to reduce the health and 
	 * positive x to increase the value. This can be used
	 * by powerups that increase the players health.
	 * 
	 * @param x. The value to increment or decrement the health bar with
	 */
	public void setHealth(int x) {
		this.currentHealth += x;
		
		if (this.currentHealth > this.maxHealth) {
			// dont go beyond max
			this.currentHealth = this.maxHealth;
		} else if (this.currentHealth < 0) {
			// dont go below min
			this.currentHealth = 0;
		}
	}
	
	/**
	 * Determines how to render the health bar.
	 * The "empty" slots will be black.
	 */
	public void render(final Graphics g) {
		float drawXAt = this.getX();
		float drawYAt = this.getY();
		for (int i = 0; i <  this.maxHealth; i++) {
			if (i < this.currentHealth) {
				if (this.team == TEAM.RED)
					g.setColor(Color.red);
				else
					g.setColor(Color.blue);
				g.fillRect(drawXAt, drawYAt, 2, 3);
			} else {
				g.setColor(Color.black);
				g.fillRect(drawXAt, drawYAt, 2, 3);
			}
			drawXAt += 2;
		}
	}
	
}
