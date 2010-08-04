package com.spacerats.entity.enemy;

import com.spacerats.graphics.Animation;
import com.spacerats.graphics.PredefinedAnimations;
import com.spacerats.cache.CacheManager;
import com.spacerats.GameContext;
import com.spacerats.entity.Victory;
import com.spacerats.entity.SoundEffect;
import com.spacerats.entity.Explosion;
import com.spacerats.weapon.Weapon;
import com.spacerats.weapon.EnemyGun;

import java.awt.*;

/**
 * @author Axel Andren
 */
public class CowboyRat extends AbstractEnemy
{
	public CowboyRat()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("boss1a"), 100);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("boss1b"), 100);
		
		boundingRadius = getHeight()/2;
		setVelocityX(-0.01f);

		setVelocityY(0.1f);

		health.setMaxHealth(3500);

		Weapon gun = new EnemyGun(this);
		gun.setFireInterval(1000l);
		gun.setFireDirectionLeft();
		setWeapon(gun);

		setScore(500);
	}

	public void draw(Graphics2D g, int x, int y)
	{
		g.drawImage(getImage(), x, y, null);
	}

	public float randomDistance()
	{
		return (float)(Math.random() * 10000 % 100) - 50;
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		if (health.isAlive())
		{
			health.decreaseHealth(amount);
			float distX = randomDistance();
			float distY = randomDistance();
			gameContext.getEngine().getMap().addObstacle(new SoundEffect(getX() + distX, getY() + distY - 50));
			gameContext.getEngine().getMap().addObstacle(new Explosion(getX() + distX, getY() + distY - 50));
			gameContext.getEngine().getSoundManager().playSFX("explode");

			if (health.isEmpty())
			{
				gameContext.getEngine().getSoundManager().playSFX("explode");

				animation = PredefinedAnimations.explosion();

				fireworks(gameContext);


				setVelocityX(0);
				setVelocityY(0);
			}
			return true;
		}

		if (health.isDying()) return false;

		return false;
	}

	public void fireworks(GameContext gameContext)
	{
		for (int a = 20; a > 0; a--)
		{
			float distX = randomDistance();
			float distY = randomDistance();
			gameContext.getEngine().getMap().addObstacle(new Explosion(getX() + distX, getY() + distY - 50));
		}
		spawnVictory(gameContext);
	}

	private void spawnVictory(GameContext gameContext)
	{
		Victory v = new Victory();
		v.setX(getX() + 50);
		v.setY(getY());
		gameContext.getEngine().getMap().addObstacle(v);
	}

	public float getFireX()
	{
		return getX() - 98;
	}

	public float getFireY()
	{
		return getY() - 20;
	}

	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);	//To change body of overridden methods use File | Settings | File Templates.

		if (getY() < 160) setVelocityY(0.1f);
		if (getY() > gameContext.getEngine().getWorldHeight() - 150) setVelocityY(-0.1f);

		if (health.isDead())
		{
			gameContext.getEngine().removeObstacle(this);
		}

		// Preventing enemies from putting in the reverse gear
		if (getVelocityX() > getMaxSpeedX()) setVelocityX(getVelocityX() - 0.003f);
		if (getVelocityX() < getMaxSpeedX()) setVelocityX(getMaxSpeedX());

		if (System.currentTimeMillis() - weapon.getLastFireTime() > weapon.getFireInterval())
		{
			weapon.fire(gameContext);
		}
	}
}
