package com.spacerats.entity;

import com.spacerats.graphics.Sprite;
import com.spacerats.graphics.Animation;
import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.cache.CacheManager;

import java.awt.geom.AffineTransform;
import java.awt.*;

/**
 * @author Håkan Larsson
 */
public class BlackHole extends Sprite implements Obstacle
{
	private double rotation = 0;

	public BlackHole()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("blackhole"), 1000);
		boundingRadius = 60;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		otherObstacle.delegateImpact(gameContext, 1000);

		if ( gameContext.getEngine().getMap().getPlayer() != otherObstacle) gameContext.getEngine().removeObstacle(otherObstacle);
	}

	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);	//To change body of overridden methods use File | Settings | File Templates.

		Player player = gameContext.getEngine().getMap().getPlayer();

		// The black hole should only affect the player when he is alive
		if (player.isAlive())
		{
			if (player.getX() < getX())
			{
				player.setX(player.getX() + 4);
			}
			else
			{
				player.setX(player.getX() - 4);
			}

			if (player.getY() < getY())
			{
				player.setY(player.getY() + 1);
			}
			else
			{
				player.setY(player.getY() - 1);
			}
		}
	}

	public AffineTransform getTransform(int x, int y) {
		transform.setToTranslation(x,y);
		rotation += 0.05 % 360;
		transform.rotate(rotation,
				getImage().getWidth(null) / 2,
				getImage().getHeight(null) / 2);

		return transform;
	}

	public void draw(Graphics2D g, int x, int y)
	{
		g.drawImage(getImage(), getTransform(x,y), null);
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		return true;
	}
}
