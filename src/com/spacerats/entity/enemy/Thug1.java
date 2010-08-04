package com.spacerats.entity.enemy;

import com.spacerats.utils.ResourceHelper;
import com.spacerats.graphics.Animation;
import com.spacerats.cache.CacheManager;
import com.spacerats.GameContext;

/**
 * @author Staffan Wennberg
 */

/**
 * @description Enemy type 1, moves along a small sinus curve
 */
public class Thug1 extends AbstractEnemy
{
	public Thug1()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyA1"), 150);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyA2"), 150);

		boundingRadius = 40;
		setVelocityX(-0.01f);
		setMaxSpeedX(-0.01f);

		setScore(25);
	}

	@Override
	public float getY()
	{
		if (health.isAlive())
		{
			return y + (float) (3 * Math.sin((float) (0.05 * x)));
		}

		return y;
	}
}
