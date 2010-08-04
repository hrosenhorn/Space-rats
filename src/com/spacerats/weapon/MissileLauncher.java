package com.spacerats.weapon;

import com.spacerats.weapon.ammo.Missile;
import com.spacerats.entity.Obstacle;

/**
 * @author Håkan Larsson
 */
public class MissileLauncher extends AbstractWeapon
{
	public MissileLauncher(Obstacle gunman)
	{
		super (gunman);
		
		ammo = new Missile ();
		fireSFX = "gun";
		type = "missile";
	}
	public MissileLauncher(Obstacle gunman, int ammocount)
	{
		super(gunman);

		ammo = new Missile();
		fireSFX = "gun";
		type = "missile";
		this.ammocount = ammocount;
	}
}
