package com.spacerats.entity.powerup;

import com.spacerats.GameContext;
import com.spacerats.weapon.MissileLauncher;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;

import java.awt.*;

/**
 * @author Axel Andrén
 */
public class Missilelauncher extends AbstractPowerup
{
	public Missilelauncher()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("missilelauncher"), 1000);
		boundingRadius = 20;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (otherObstacle instanceof Player)
		{
			Player player = (Player)otherObstacle;
			if (player.isAlive())
			{
				gameContext.getEngine().updatePlayerWeapon(new MissileLauncher(gameContext.getEngine().getMap().getPlayer(), 30));
				discardPowerup(gameContext);
			}
		}
	}

	public void draw(Graphics2D g, int x, int y)
	{
		drawStar(g,x,y,rotation+=0.05);
		g.drawImage(CacheManager.getInstance().getPreloadedImage("missilelauncher"),x-12,y+8,null);
	}
}
