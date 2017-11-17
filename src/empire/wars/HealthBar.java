package empire.wars;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import jig.Entity;

/**
 * Implements the player health.
 * @author peculiaryak
 *
 */
public class HealthBar extends Entity {
	private int maxHealth = 20;
	private int currentHealth;
	
	public HealthBar(float x, float y) {
		super(x, y);
		this.currentHealth = this.maxHealth;
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
				g.setColor(Color.red);
				g.fillRect(drawXAt, drawYAt, 3, 3);
			} else {
				g.setColor(Color.black);
				g.fillRect(drawXAt, drawYAt, 3, 3);
			}
			drawXAt += 3;
		}
	}
	
}
