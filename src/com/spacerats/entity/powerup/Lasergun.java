package com.spacerats.entity.powerup;

import com.spacerats.GameContext;
import com.spacerats.weapon.LaserGun;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;

import java.awt.*;

/**
 * @author Håkan Larsson
 */
public class Lasergun extends AbstractPowerup
{
	public Lasergun()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("lasergun"), 1000);
		boundingRadius = 20;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (otherObstacle instanceof Player)
		{
			Player player = (Player)otherObstacle;
			if (player.isAlive())
			{
				gameContext.getEngine().updatePlayerWeapon(new LaserGun(gameContext.getEngine().getMap().getPlayer(), 20));
				discardPowerup(gameContext);
			}
		}
	}

	public void draw(Graphics2D g, int x, int y)
	{
		drawStar(g,x,y,rotation+=0.05);
		g.drawImage(CacheManager.getInstance().getPreloadedImage("lasergun"),x+4,y+5,null);
	}
}
