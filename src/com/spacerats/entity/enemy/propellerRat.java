package com.spacerats.entity.enemy;

import com.spacerats.graphics.Animation;
import com.spacerats.cache.CacheManager;
import com.spacerats.GameContext;
import com.spacerats.weapon.Weapon;
import com.spacerats.weapon.EnemyGun;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.SoundEffect;
import com.spacerats.entity.powerup.Ammobox;

/**
 * @author Staffan Wennberg
 */
public class propellerRat extends AbstractEnemy
{

	public propellerRat()
	{
		int dur = 50;
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("propRat1"),dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("propRat2"),dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("propRat3"),dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("propRat4"),dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("propRat5"),dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("propRat6"),dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("propRat7"),dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("propRat8"),dur);

		boundingRadius = 20;
		setVelocityX(-0.03f);
		setMaxSpeedX(-0.035f);

		setScore(20);

		Weapon gun = new EnemyGun(this);
		gun.setFireInterval((long)(Math.random() * 10000 % 1000) + 1000);
		gun.setFireDirectionLeft();
		setWeapon(gun);

	}

	public float getFireX()
	{
		return getX() - 26;
	}

	public float getFireY()
	{
		return getY() + 14;
	}

	//all this copypasted from AbstractEnemy just to adjust the ammobox size...
	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);	//To change body of overridden methods use File | Settings | File Templates.

		if (health.isDead())
		{
			if (randGen.nextDouble() > 0.9)
			{
				Ammobox ammobox = new Ammobox(10);
				ammobox.setX(getX());
				ammobox.setY(getY());
				gameContext.getEngine().getMap().addObstacle((Obstacle)ammobox);
			}

			gameContext.getEngine().removeObstacle(this);
		}
	}
}
