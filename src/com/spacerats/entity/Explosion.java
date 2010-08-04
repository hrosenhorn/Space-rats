package com.spacerats.entity;

import com.spacerats.graphics.Sprite;
import com.spacerats.graphics.Animation;
import com.spacerats.graphics.PredefinedAnimations;
import com.spacerats.GameContext;

/**
 * @author Staffan Wennberg
 */
public class Explosion extends Sprite implements Obstacle
{
	long durationTime = 0;

	public Explosion(float x, float y)
	{
		setX(x);
		setY(y);
		animation = PredefinedAnimations.explosion();
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{

	}

	public void update(GameContext gameContext, long elapsedTime) {
		super.update(gameContext, elapsedTime);
		durationTime += elapsedTime;
		if (durationTime > 1000) gameContext.getEngine().getMap().removeObstacle(this);
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		return false;
	}

}
