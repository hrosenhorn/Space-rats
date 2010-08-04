package com.spacerats.entity.powerup;

import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.ScoreManager;
import com.spacerats.weapon.ammo.EnemyBullet;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;
import com.spacerats.entity.enemy.AbstractEnemy;

import java.awt.*;

/**
 * @author Staffan Wennberg
 */
public class Blast extends AbstractPowerup implements LonglivedPowerup
{
	private String state; // idle or explosion
	private int expDiameter = 0;

	public Blast() {
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("blast"), 1000);

		boundingRadius = 20;

		state = "idle";
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (otherObstacle instanceof Player)
		{
			Player player = (Player) otherObstacle;
			if (player.isAlive())
			{
				if (state.equals("idle"))
				{
					state = "explosion";
					gameContext.getEngine().getSoundManager().playSFX("laser");
				}
			}
		}
	}

	public void earnScore(AbstractEnemy enemy, int impactPower) {
		if (enemy.getHealth().isAlive() &&
			(enemy.getHealth().getCurrentHealth() - impactPower <= 0))
			{
				ScoreManager.addScore(enemy.getScore());
			}
	}

	public void update(GameContext gameContext, long elapsedTime)
	{
		if (state.equals("explosion"))
		{
			expDiameter += 100;
			for (int i = 0; i < gameContext.getEngine().getMap().getObstacles().size(); i++)
			{
				Obstacle o = gameContext.getEngine().getMap().getObstacles().get(i);

				if (o instanceof AbstractEnemy || o instanceof EnemyBullet)
				{
					float limitX = gameContext.getEngine().getMap().getWorldSprite().getX()
					+ (gameContext.getEngine().getWorldWidth() / 2) + o.getWidth();

					double d_y = Math.abs(o.getY() - getY());
					double d_x = Math.abs(o.getX() - getX());
					double distance = Math.sqrt(d_x * d_x + d_y * d_y);

					if (distance <= expDiameter/2 && (expDiameter/2) < (distance + 60) && o.getX() < limitX)
					{
						if (o instanceof AbstractEnemy)
						{
							AbstractEnemy enemy = (AbstractEnemy)o;
							earnScore(enemy, 100);
							enemy.delegateImpact(gameContext, 100);
						}
						else if (o instanceof EnemyBullet)
						{
							gameContext.getEngine().removeObstacle(o);
						}
					}
				}
			}
		}
		
		// Remove the blast
		if (expDiameter > gameContext.getEngine().getWorldWidth() * 2.5)
			gameContext.getEngine().removeObstacle(this);
	}

	public void draw(Graphics2D g, int x, int y)
	{
		if (state.equals("idle"))
		{
			drawStar(g,x,y,rotation += 0.05f);
			g.drawImage(CacheManager.getInstance().getPreloadedImage("blast"),x+12,y+1,null);
		}
		else if (state.equals("explosion"))
		{
			int red = (int)(Math.random() * 10000) % 256;
			int green = (int)(Math.random() * 10000) % 256;
			int blue = (int)(Math.random() * 10000) % 256;
			g.setColor(new Color(red,green,blue));
			g.drawOval((x + 45 - expDiameter / 2), (y + 20 - expDiameter / 2), expDiameter, expDiameter);

			red = (int)(Math.random() * 10000) % 256;
			green = (int)(Math.random() * 10000) % 256;
			blue = (int)(Math.random() * 10000) % 256;
			g.setColor(new Color(red,green,blue));
			g.drawOval((x + 44 - expDiameter / 2), (y + 19 - expDiameter / 2), expDiameter + 2, expDiameter + 2);

			red = (int)(Math.random() * 10000) % 256;
			green = (int)(Math.random() * 10000) % 256;
			blue = (int)(Math.random() * 10000) % 256;
			g.setColor(new Color(red,green,blue));
			g.drawOval((x + 43 - expDiameter / 2), (y + 18 - expDiameter / 2), expDiameter + 4, expDiameter + 4);
		}
	}
}
