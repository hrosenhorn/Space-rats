package com.spacerats.weapon.ammo;

import com.spacerats.GameContext;
import com.spacerats.Engine;
import com.spacerats.weapon.MissileLauncher;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Explosion;
import com.spacerats.entity.enemy.AbstractEnemy;
import com.spacerats.Health;

import java.lang.Math;

/**
 * @author Axel Andrén
 */
public class Missile extends AbstractAmmo
{
	private AbstractEnemy enemy = null;
	private float enemyXPos;
	
	public Missile()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("missile1"), 300);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("missile2"), 300);

		boundingRadius = 5;
		damage = 100;
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
			if (otherObstacle instanceof AbstractEnemy)
			{ //ka-buum!
				gameContext.getEngine().getMap().addObstacle(new Explosion(getX(), getY()));
			}
		}

	}

	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);	//To change body of overridden methods use File | Settings | File Templates.
		
		if (enemy == null) 
		{//the following loop will only run once per missile - as long as any legit target is still alive, at least. And if everything is already dead, efficiency matters less
			AbstractEnemy newEnemy;
			for (Obstacle obstacle : gameContext.getEngine().getMap().getObstacles())
			{
				if (obstacle instanceof AbstractEnemy)
				{
					if (((AbstractEnemy)obstacle).getHealth().isAlive() && obstacle.getX() > getX())
					{ //select a live target that is 'in front of' the missile
						newEnemy = ((AbstractEnemy)obstacle);
						if (!(enemy == null))
						{ //The "lock-on target" is done in the ELSE case below - here we cycle through targets to find the closest one
							float newXPos = newEnemy.getX();
							if (newXPos < enemyXPos)
							{
								enemy = newEnemy;
								enemyXPos = enemy.getX();
							}
						}
						else
						{ //I know the order these if/else cases are done in is unintuitive, but the loop should only arrive here once, so it's saving N checks
							enemy = newEnemy;
							enemyXPos = enemy.getX();
						}
					}
				}
			}
			// thanks to Mark "Fam" James, though I probably should have been able to do this on my own
			float xdist=(enemyXPos-getX());
			float ydist=(enemy.getY()-getY());
			float distance = (float)Math.sqrt((xdist*xdist)+(ydist*ydist));
			setVelocityX((xdist / distance)*0.6f);
			setVelocityY((ydist / distance)*0.6f);
		}
		// else
		// {
			// if (enemy.getHealth().isAlive() || enemy.getX() <= getX())
			// { //if target was eliminated or missed, goto else
								
				// old homing code
				// if (enemy.getX() > getX())
					// setVelocityX(getVelocityX() + 0.03f);
				// if (enemy.getY() > getY())
					// setVelocityY(getVelocityY() + 0.04f);
				// else if (enemy.getY() < getY())
					// setVelocityY(getVelocityY() - 0.04f);
			// }
			// else
				// enemy = null; //select new target
		// }
	}
}
