package com.spacerats.entity;

import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.graphics.Sprite;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Staffan Wennberg
 */
public class SoundEffect extends Sprite implements Obstacle
{
	String[] effects = {"smack","zap","splat","dink","boom", "pow", "schplork"};

	long durationTime = 0;

	public SoundEffect(float x, float y)
	{

		setX(x);
		setY(y);
		animation = new Animation();
		String fx = effects[(int)(Math.random() * 10000f) % effects.length];
		animation.addFrame(CacheManager.getInstance().getPreloadedImage(fx),1000);
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		// DO NOTHING
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		return false;
	}

	@Override
	public void update(GameContext gameContext, long elapsedTime) {
		durationTime += elapsedTime;
		if (durationTime > 500) gameContext.getEngine().getMap().removeObstacle(this);
	}

	public AffineTransform getTransform(int x, int y)
	{
		transform.setToTranslation(x,y);

		return transform;
	}	
}
