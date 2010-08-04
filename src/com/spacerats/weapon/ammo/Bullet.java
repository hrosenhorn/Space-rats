package com.spacerats.weapon.ammo;

import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;

/**
 * @author Håkan Larsson
 */
public class Bullet extends AbstractAmmo
{
	public Bullet()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("bullet"), 1000);

		boundingRadius = 5;
		
		setVelocityX(0.75f);
		damage = 100;
	}
}
