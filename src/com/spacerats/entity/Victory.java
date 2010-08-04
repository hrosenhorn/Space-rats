package com.spacerats.entity;

import com.spacerats.graphics.Sprite;
import com.spacerats.graphics.Animation;
import com.spacerats.graphics.Transition;
import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.cache.CacheManager;
import com.spacerats.utils.ResourceHelper;

import java.awt.geom.AffineTransform;
import java.awt.*;

/**
 * @author Håkan Larsson
 */
public class Victory extends Sprite implements Obstacle
{

	public Victory()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exit1"), 200);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exit2"), 200);

		boundingRadius = 50;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (otherObstacle instanceof Player)
		{
			// Väntar med Transition mellan banor till levelindikatorn är klar

			//Transition.getInstance().resetTransition(gameContext,true);

			gameContext.getEngine().loadNextMap();
		}
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		return true;
	}

	public AffineTransform getTransform(int x, int y)
	{
		transform.setToTranslation(x,y);

		return transform;
	}

	public void draw(Graphics2D g, int x, int y)
	{
		g.drawImage(getImage(), x - 20, y - 40, null);
	}
}
