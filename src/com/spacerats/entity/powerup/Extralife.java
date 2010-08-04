package com.spacerats.entity.powerup;

import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Staffan Wennberg
 */
public class Extralife extends AbstractPowerup
{
	public Extralife()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("player-small"),1000);

		boundingRadius = 20;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (otherObstacle instanceof Player)
		{
			Player player = (Player)otherObstacle;
			if (player.isAlive())
			{
				player.setLives(1,false);
				gameContext.getEngine().getSoundManager().playSFX("extralife");
				gameContext.getEngine().removeObstacle(this);
			}
		}
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		return false;
	}

	public void draw(Graphics2D g, int x, int y)
	{
		drawStar(g,x,y,rotation += 0.05);
		g.drawImage(CacheManager.getInstance().getPreloadedImage("life"),x+8,y+11,null);

	}
}
