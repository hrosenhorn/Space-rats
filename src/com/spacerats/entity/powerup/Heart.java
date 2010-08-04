package com.spacerats.entity.powerup;

import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;

import java.awt.*;

/**
 * @author Håkan Larsson
 */
public class Heart extends AbstractPowerup
{
	public Heart()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("heart"), 1000);

		boundingRadius = 20;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (otherObstacle instanceof Player)
		{
			Player player = (Player) otherObstacle;

			if (player.isAlive())
			{
				player.getHealth().resetHealth();
				discardPowerup(gameContext);
			}
		}
	}

	public void draw(Graphics2D g, int x, int y)
	{
		drawStar(g,x,y,rotation += 0.05);
		g.drawImage(CacheManager.getInstance().getPreloadedImage("heart"),x+10,y+11,null);
	}
}
