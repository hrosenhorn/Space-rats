package com.spacerats.weapon.ammo;

import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;
import com.spacerats.entity.enemy.Enemy;
import com.spacerats.GameContext;
import com.spacerats.Health;

import java.awt.*;

/**
 * @author Axel Andrén
 */
public class LaserWave extends AbstractAmmo
{
	public LaserWave()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("laserwave"), 1000);

		boundingRadius = getHeight() / 2;
		setVelocityX(1.5f);
		damage = 50;
	}
	
	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		earnScore(otherObstacle, 100);

		if (!gunman.equals(otherObstacle))
		{
			otherObstacle.delegateImpact(gameContext, damage);
		}

		//if (!(otherObstacle instanceof Player))
		//	otherObstacle.delegateImpact(gameContext, 100);

		// Let the laserwave move on
	}

	public void draw(Graphics2D g, int x, int y)
	{
		g.setColor(colorRandom());
		g.drawOval(x+50,y+6,20,60);
		g.drawOval(x+51,y+7,18,58);

		g.setColor(colorRandom());
		g.drawOval(x+29,y+15,14,42);
		g.drawOval(x+30,y+16,12,40);

		g.setColor(colorRandom());
		g.drawOval(x+14,y+24,8,24);
		g.drawOval(x+15,y+25,6,22);
	}

	public Color colorRandom()
	{
		return new Color(
				(int)(Math.random() * 10000) % 256,
				(int)(Math.random() * 10000) % 256,
				(int)(Math.random() * 10000) % 256);
	}
}
