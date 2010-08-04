package com.spacerats.weapon;

import com.spacerats.weapon.ammo.Lameshot;
import com.spacerats.entity.Obstacle;


/**
 * @author Axel Andrén
 */
public class LameShot extends AbstractWeapon
{
	public LameShot(Obstacle gunman)
	{
		super (gunman);
		
		ammo = new Lameshot ();
		fireSFX = "boop2";
		type = "lameshot";
	}
	public LameShot(Obstacle gunman, int ammocount)
	{
		super (gunman);
		
		ammo = new Lameshot ();
		fireSFX = "boop2";
		type = "lameshot";
		this.ammocount = ammocount;
	}
}
