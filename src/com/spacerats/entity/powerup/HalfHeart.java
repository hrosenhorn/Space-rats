package com.spacerats.entity.powerup;

import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;

import java.awt.*;

/**
 * @author Staffan Wennberg
 */
public class HalfHeart extends AbstractPowerup
{
	public HalfHeart()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("halfheart"), 1000);

		boundingRadius = 20;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (otherObstacle instanceof Player)
		{
			Player player = (Player) otherObstacle;

			if (player.isAlive())
			{
				player.getHealth().increaseHealth(50);
				discardPowerup(gameContext);
			}
		}
	}

	public void draw(Graphics2D g, int x, int y)
	{
		drawStar(g,x,y,rotation += 0.05);
		g.drawImage(CacheManager.getInstance().getPreloadedImage("halfheart"),x+12,y+10,null);
	}
}
