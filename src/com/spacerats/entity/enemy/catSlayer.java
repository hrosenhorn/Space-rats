package com.spacerats.entity.enemy;

import com.spacerats.graphics.Animation;
import com.spacerats.cache.CacheManager;
import com.spacerats.weapon.Weapon;
import com.spacerats.weapon.EnemyGun;
import com.spacerats.GameContext;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.powerup.Ammobox;

/**
 * @author Staffan Wennberg
 */

/**
 * @description Enemy type 1, moves along a small sinus curve
 */
public class catSlayer extends AbstractEnemy
{
	public catSlayer()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("catSlayer1"), 150);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("catSlayer2"), 150);

		boundingRadius = 40;
		setVelocityX(-0.05f);
		setMaxSpeedX(-0.05f);

		setScore(25);

		Weapon gun = new EnemyGun(this);
		gun.setFireDirectionLeft();
		gun.setFireInterval((long)(Math.random() * 10000 % 4000) + 1000);
		setWeapon(gun);
	}

	public float getFireX()
	{
		return getX() + 21;
	}

	public float getFireY()
	{
		return getY() + 22;
	}

	@Override
	public float getY()
	{
		if (health.isAlive())
		{
			return y + (float) (60 * Math.sin((float) (0.05 * x)));
		}

		return y;
	}
	
	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);	//To change body of overridden methods use File | Settings | File Templates.

		if (health.isDead())
		{
			if (randGen.nextDouble() > 0.9)
			{
				Ammobox ammobox = new Ammobox();
				ammobox.setX(getX());
				ammobox.setY(getY());
				gameContext.getEngine().getMap().addObstacle((Obstacle)ammobox);
			} 
			
			gameContext.getEngine().removeObstacle(this);
		}
	}
}
