package com.spacerats.entity;

import com.spacerats.GameContext;
import com.spacerats.Health;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Håkan Larsson
 */
public interface Obstacle
{
	public void handleCollision (GameContext gameContext, Obstacle otherObstacle);

	public void setX (float x);
	public void setY (float y);
	public void setVelocityX(float x);
	public void setVelocityY(float y);
	public float getX ();
	public float getY ();
	public float getFireX();
	public float getFireY();
	public int getWidth ();
	public int getHeight ();

	public float getVelocityX();
	public float getVelocityY();
	public float getMaxSpeedX();
	public float getMaxSpeedY();

	public Image getImage ();
	public void draw(Graphics2D g, int x, int y);

	/**
	 * Get the bounding radius of a Obstacle
	 * @return the bounding radius.
	 */
	public int getBoundingRadius ();
	public boolean invincible();

	/**
	 * Handle impact from another entity.
	 * @param gameContext
	 * @param amount The amount of damage to be absorbed.
	 * @return true if the impact of the bullet was absorbed. False if the bullet should continue.
	 */
	public boolean delegateImpact(GameContext gameContext, int amount);

	public AffineTransform getTransform(int x, int y);
}
