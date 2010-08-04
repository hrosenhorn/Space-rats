package com.spacerats.weapon.ammo;

import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;

/**
 * @author Håkan Larsson
 */
public class Lameshot extends AbstractAmmo
{
	public Lameshot()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("lameammo"), 1000);

		boundingRadius = 5;
		
		setVelocityX(0.75f);
		damage = 40;
	}
}
