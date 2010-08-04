package com.spacerats.weapon;

import com.spacerats.weapon.ammo.LaserWave;
import com.spacerats.entity.Obstacle;

/**
 * @author Håkan Larsson
 */
public class LaserGun extends AbstractWeapon
{
	public LaserGun(Obstacle gunman)
	{
		super (gunman);
		
		ammo = new LaserWave ();
		fireSFX = "prize";
		type = "lasergun";
	}
	public LaserGun(Obstacle gunman, int ammocount)
	{
		super(gunman);

		ammo = new LaserWave();
		fireSFX = "prize";
		type = "lasergun";
		this.ammocount = ammocount;
	}
}
