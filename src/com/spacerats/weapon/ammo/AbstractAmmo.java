package com.spacerats.weapon.ammo;

import com.spacerats.graphics.Sprite;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.enemy.AbstractEnemy;
import com.spacerats.entity.Player;
import com.spacerats.entity.enemy.Enemy;
import com.spacerats.entity.SoundEffect;
import com.spacerats.GameContext;
import com.spacerats.ScoreManager;

import java.awt.geom.AffineTransform;
import java.awt.*;

/**
 * @author Håkan Larsson
 */
abstract public class AbstractAmmo extends Sprite implements Ammo, Obstacle
{
	protected Obstacle gunman;
	protected int damage = 100;

	public void setGunman(Obstacle gunman)
	{
		this.gunman = gunman;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (gunman.equals(otherObstacle))
		{
			return;
		}

		earnScore(otherObstacle, 100);
		if (otherObstacle.delegateImpact(gameContext, damage))
		{
			gameContext.getEngine().removeObstacle(this);
		}

	}

	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);	//To change body of overridden methods use File | Settings | File Templates.

		// If the bullet goes outside the screen remove it
		if (getX() > gameContext.getEngine().getMap().getWorldSprite().getX() + gameContext.getEngine().getWorldWidth() / 2 + 30)
		{
			gameContext.getEngine().removeObstacle(this);
		}
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		return true;
	}

	public AffineTransform getTransform(int x, int y)
	{
		transform.setToTranslation(x,y);

		if (getVelocityX() < 0)
		{
			transform.scale(-1, 1);
			transform.translate(-getWidth(), 0);
		}

		return transform;
	}

	public void earnScore(Obstacle otherObstacle, int impactPower) {
		if (otherObstacle instanceof AbstractEnemy)
		{
			AbstractEnemy enemy = (AbstractEnemy)otherObstacle;
			if (enemy.getHealth().isAlive() &&
					(enemy.getHealth().getCurrentHealth() - impactPower <= 0))
			{
				ScoreManager.addScore(enemy.getScore());
			}
		}
	}

	public void draw(Graphics2D g, int x, int y)
	{
		g.drawImage(getImage(), getTransform(x,y), null);
	}
}


