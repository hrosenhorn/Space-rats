package com.spacerats.weapon;

import com.spacerats.weapon.ammo.EnemyBullet;
import com.spacerats.entity.Obstacle;

/**
 * @author Staffan Wennberg
 */
public class EnemyGun extends AbstractWeapon
{
	public EnemyGun(Obstacle gunman)
	{
		super (gunman);

		ammo = new EnemyBullet();
		//fireSFX = "boop2";
	}
	public EnemyGun(Obstacle gunman, int ammocount)
	{
		super (gunman);

		ammo = new EnemyBullet();
		//fireSFX = "boop2";
		this.ammocount = ammocount;
	}
}
