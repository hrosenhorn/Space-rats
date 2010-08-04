package com.spacerats.entity.enemy;

import com.spacerats.graphics.Animation;
import com.spacerats.GameContext;
import com.spacerats.cache.CacheManager;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.powerup.Ammobox;

/**
 * @author Staffan Wennberg
 */
public class snakeRat extends AbstractEnemy
{
	public snakeRat() {
		animation = new Animation();
		long duration = 70;
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat1"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat2"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat3"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat4"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat5"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat6"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat7"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat6"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat5"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat4"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat3"),duration);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("snakeRat2"),duration);

		boundingRadius = 20;
		setVelocityX(-0.08f);
		setMaxSpeedX(-0.08f);

		setScore(20);
	}

	public void update(GameContext gameContext, long elapsedTime) {
		super.update(gameContext, elapsedTime);

		if (getVelocityX() > 0 && getVelocityY() == 0) {
			setVelocityY(-0.05f);
		}

		if (getVelocityY() < 0) setVelocityY(getVelocityY() + 0.005f);
		if (getVelocityY() > 0) setVelocityY(getVelocityY() - 0.005f);

		if (getVelocityY() >= -0.04 && getVelocityY() <= 0.04) setVelocityY(0f);
		
		if (health.isDead()) //drop smallish ammobox
		{
			if (randGen.nextDouble() > 0.9)
			{
				Ammobox ammobox = new Ammobox(5);
				ammobox.setX(getX());
				ammobox.setY(getY());
				gameContext.getEngine().getMap().addObstacle((Obstacle)ammobox);
			}

			gameContext.getEngine().removeObstacle(this);
		}
	}
}
