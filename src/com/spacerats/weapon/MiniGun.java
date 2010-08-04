package com.spacerats.weapon;

import com.spacerats.weapon.ammo.Bullet;
import com.spacerats.entity.Obstacle;

/**
 * @author Håkan Larsson
 */
public class MiniGun extends AbstractWeapon
{
	public MiniGun(Obstacle gunman)
	{
		super (gunman);
		
		ammo = new Bullet ();
		fireSFX = "boop2";
		type = "minigun";
	}
	public MiniGun(Obstacle gunman, int ammocount)
	{
		super (gunman);
		
		ammo = new Bullet ();
		fireSFX = "boop2";
		type = "minigun";
		this.ammocount = ammocount;
	}
}
