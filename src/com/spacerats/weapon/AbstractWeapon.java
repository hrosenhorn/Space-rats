package com.spacerats.weapon;

import com.spacerats.GameContext;
import com.spacerats.weapon.ammo.Ammo;
import com.spacerats.weapon.ammo.AbstractAmmo;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;

import java.awt.geom.AffineTransform;

/**
 * @author Håkan Larsson
 */
abstract public class AbstractWeapon implements Weapon
{
	protected Ammo ammo;
	protected String fireSFX;
	protected String type;
	protected int ammocount;
	private long lastFireTime;
	private static long DEFAULT_FIRETIME = 2000;
	private long firetime = 0;
	private Obstacle gunman;
	private FireDirection fireDirection = FireDirection.RIGHT;

	protected AbstractWeapon(Obstacle gunman)
	{
		this.gunman = gunman;
		ammocount = -1;
	}
	protected AbstractWeapon(Obstacle gunman, int ammocount)
	{
		this.gunman = gunman;
		this.ammocount = ammocount;
	}

	protected AffineTransform transform = new AffineTransform();

	public String getType()
	{
		return type;
	}

	public void setAmmocount(int ammoIn, boolean absolute)
	{ //if 'absolute', SET 'ammocount' to 'ammoIn', else ADD.
		if (absolute && ammocount >= 0) 
			ammocount = ammoIn;
		else if (ammocount >= 0)
			ammocount += ammoIn;
	}//ammocount >= 0 needs to be checked or else weapons with infinite ammo will suddenly have finite ammo
	//after picking up an ammobox

	public int getAmmoCount()
	{
		return ammocount;
	}
	
	public void fire(GameContext gameContext)
	{
		if (ammocount != 0)
		{
			Object object = null;

			try
			{
				object = ammo.getClass().newInstance();
			}
			catch (InstantiationException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			
			Obstacle obstacle = (Obstacle) object;

			//obstacle.setX(gunman.getX());
			//obstacle.setY(gunman.getY());

			// A bit dirty...
			//((AbstractAmmo)obstacle).setGunman(gunman);

			AbstractAmmo abstractAmmo = (AbstractAmmo) obstacle;
			abstractAmmo.setGunman(gunman);
			abstractAmmo.setX(gunman.getFireX());
			abstractAmmo.setY(gunman.getFireY());

			if (fireDirection.equals(FireDirection.LEFT))
			{
				if (abstractAmmo.getVelocityX() > 0)
				{
					abstractAmmo.setVelocityX(-abstractAmmo.getVelocityX());
				}
			}

			if (fireDirection.equals(FireDirection.RIGHT))
			{
				if (abstractAmmo.getVelocityX() < 0)
				{
					abstractAmmo.setVelocityX(-abstractAmmo.getVelocityX());
				}
			}

			gameContext.getEngine().getMap().addObstacle((Obstacle)object);
			gameContext.getEngine().getSoundManager().playSFX(fireSFX);

			lastFireTime = System.currentTimeMillis();
			ammocount -= 1;
		}
		else
			gameContext.getEngine().getSoundManager().playSFX("click");
	}

	public AffineTransform getTransform(int x, int y)
	{
		transform.setToTranslation(x,y);
		return transform;
	}

	public long getLastFireTime()
	{
		return lastFireTime;
	}

	public long getFireInterval()
	{
		if (firetime == 0) return AbstractWeapon.DEFAULT_FIRETIME;
		else return firetime;
	}

	public void setFireInterval(long time)
	{
		firetime = time;
	}

	public void setFireDirectionLeft()
	{
		fireDirection = FireDirection.LEFT;
	}

	public void setFireDirectionRight()
	{
		fireDirection = FireDirection.RIGHT;
	}	

	private enum FireDirection
	{
		LEFT,
		RIGHT;
	}
}
