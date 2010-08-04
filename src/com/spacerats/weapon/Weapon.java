package com.spacerats.weapon;

import com.spacerats.GameContext;
import com.spacerats.entity.Obstacle;

import java.awt.geom.AffineTransform;

/**
 * @author Håkan Larsson
 */
public interface Weapon
{
	public String getType();
	
	public void setAmmocount(int ammoIn, boolean absolute);
	public int getAmmoCount();
	
	public void fire(GameContext gameContext);

	public AffineTransform getTransform(int x, int y);

	public long getLastFireTime();

	public long getFireInterval ();

	public void setFireInterval(long time);

	/**
	 * Makes the bullets fire to the left
	 */
	public void setFireDirectionLeft ();

	/**
	 * Makes the bullets fire to the right
	 */
	public void setFireDirectionRight ();	
}
