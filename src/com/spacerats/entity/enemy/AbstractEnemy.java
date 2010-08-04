package com.spacerats.entity.enemy;

import java.util.Random;
import com.spacerats.graphics.Sprite;
import com.spacerats.graphics.PredefinedAnimations;
import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.weapon.Weapon;
import com.spacerats.weapon.Dummy;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;
import com.spacerats.entity.SoundEffect;

import java.awt.geom.AffineTransform;

/**
 * @author Staffan Wennberg
 */
public class AbstractEnemy extends Sprite implements Obstacle, Enemy
{
	protected Health health;
	protected Random randGen = new Random();
	private int score;

	protected Weapon weapon = new Dummy(this);
	
	public AbstractEnemy()
	{
		health = new Health(this);
	}

	public Weapon getWeapon()
	{
		return weapon;
	}

	public void setWeapon(Weapon weapon)
	{
		this.weapon = weapon;
	}	

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (health.isAlive())
		{
			if (otherObstacle instanceof Player)
			{
				gameContext.getEngine().collideObstacles(this, otherObstacle);
				
				otherObstacle.delegateImpact(gameContext, 10);
			}
		}
	}

	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);	//To change body of overridden methods use File | Settings | File Templates.

		if (health.isDead())
		{
			// if (randGen.nextDouble() > 0.95)
			// {
				// Ammobox ammobox = new Ammobox();
				// ammobox.setX(getX());
				// ammobox.setY(getY());
				// gameContext.getEngine().getMap().addObstacle((Obstacle)ammobox);
			// } 
			//Commented because otherwise every enemy that uses super.update (all of them) will have a chance to drop the default-size ammobox
			gameContext.getEngine().removeObstacle(this);
		}

		// Preventing enemies from putting in the reverse gear
		if (getVelocityX() > getMaxSpeedX()) setVelocityX(getVelocityX() - 0.003f);
		if (getVelocityX() < getMaxSpeedX()) setVelocityX(getMaxSpeedX());

		if (System.currentTimeMillis() - weapon.getLastFireTime() > weapon.getFireInterval() &&
				health.isAlive())
		{
			weapon.fire(gameContext);
		}
	}

	public Health getHealth()
	{
		return health;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}
	
	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		if (health.isAlive())
		{
			// Kill this enemy
			health.decreaseHealth(amount);

			//ZAP! BIFF! POW! on hitting the enemy
			SoundEffect sfx = new SoundEffect(getX(), getY());
			gameContext.getEngine().getMap().addObstacle((Obstacle)sfx);
			
			if (health.isEmpty())
			{
				//gameContext.getEngine().getMap().addObstacle(new SoundEffect(getX(),getY()));
				gameContext.getEngine().getSoundManager().playSFX("explode");

				animation = PredefinedAnimations.explosion();
				if (this instanceof CowboyRat)
				{
					CowboyRat boss = (CowboyRat) this;
					boss.fireworks(gameContext);
				}

				setVelocityX(0);
				setVelocityY(0);
			}
			return true;
		}

		if (health.isDying()) return false;

		return false;
	}

	public AffineTransform getTransform(int x, int y)
	{
		transform.setToTranslation(x,y);

		return transform;
	}	
}
