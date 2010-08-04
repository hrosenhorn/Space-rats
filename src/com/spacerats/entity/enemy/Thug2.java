package com.spacerats.entity.enemy;

import com.spacerats.graphics.Animation;
import com.spacerats.graphics.Sprite;
import com.spacerats.entity.Obstacle;
import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.cache.CacheManager;

import java.awt.geom.AffineTransform;

/**
 * @author Staffan Wennberg
 */
public class Thug2 extends AbstractEnemy
{
	public Thug2() {
		animation = new Animation();
		long duration = 120;
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB1"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB2"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB3"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB4"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB5"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB6"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB7"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB6"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB5"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB4"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB3"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("enemyB2"),duration);

		boundingRadius = getHeight()/2;
		setVelocityX(-0.02f);
		setMaxSpeedX(-0.02f);

		setScore(20);
	}

	public void update(GameContext gameContext, long elapsedTime) {
		super.update(gameContext, elapsedTime);

		if (getVelocityX() > 0 && getVelocityY() == 0) {
			setVelocityY(-0.05f);
		}

		if (getVelocityY() < 0) setVelocityY(getVelocityY() + 0.001f);
		if (getVelocityY() > 0) setVelocityY(getVelocityY() - 0.001f);

		if (getVelocityY() >= -0.02 && getVelocityY() <= 0.02) setVelocityY(0f);
	}



}
