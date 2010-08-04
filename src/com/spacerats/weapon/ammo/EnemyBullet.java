package com.spacerats.weapon.ammo;

import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Explosion;
import com.spacerats.entity.SoundEffect;
import com.spacerats.entity.enemy.Enemy;
import com.spacerats.GameContext;

/**
 * @author Staffan Wennberg
 */
public class EnemyBullet extends AbstractAmmo
{
	public EnemyBullet()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("bullet"), 1000);

		boundingRadius = 5;

		setVelocityX(0.75f);
		damage = 25;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (gunman.equals(otherObstacle))
		{
			return;
		}

		if (otherObstacle instanceof Enemy) return;

		//earnScore(otherObstacle, 100);
		if (otherObstacle.delegateImpact(gameContext, damage))
		{
			gameContext.getEngine().removeObstacle(this);
			gameContext.getEngine().getMap().addObstacle(new Explosion(getX(),getY()));
			gameContext.getEngine().getMap().addObstacle(new SoundEffect(getX(),getY()));
		}

	}
}
