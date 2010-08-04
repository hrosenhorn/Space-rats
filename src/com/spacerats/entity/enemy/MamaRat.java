package com.spacerats.entity.enemy;

import com.spacerats.graphics.Animation;
import com.spacerats.graphics.PredefinedAnimations;
import com.spacerats.cache.CacheManager;
import com.spacerats.GameContext;
import com.spacerats.weapon.ammo.BabyBomb;
import com.spacerats.entity.Victory;
import com.spacerats.entity.SoundEffect;
import com.spacerats.entity.Explosion;

import java.awt.*;

/**
 * @author Staffan Wennberg
 */

public class MamaRat extends AbstractEnemy
{
	private float speed = 0.08f;// 0.019f;

	public MamaRat()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("boss2"), 100);

		boundingRadius = getHeight()/2;

		setVelocityX(0.0f);
		setVelocityY(-speed);

		setMaxSpeedX(0f);
		setMaxSpeedY(speed);

		health.setMaxHealth(5000);

		setScore(700);
	}

	public void draw(Graphics2D g, int x, int y)
	{
		g.drawImage(getImage(), x + 55, y - 10, null);
	}

	public float randomDistance()
	{
		return (float)(Math.random() * 10000 % 50) - 25;
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

	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);

		float worldWidth = gameContext.getEngine().getMap().getWidth() * 64;


		if (health.isDead())
		{
			gameContext.getEngine().removeObstacle(this);
		}

		if (getVelocityX() == 0 && (int)(Math.random() * 10000) % 300 == 0)
		{
			setVelocityY(0f);
			setMaxSpeedX(-(2 * speed));
			setVelocityX(-(2 * speed));

		}

		if (getVelocityX() < 0 && getX() < worldWidth - (gameContext.getEngine().getWorldWidth() * 0.85))
		{
			setMaxSpeedX(2 * speed);
			setVelocityX(2 * speed);
		}

		if (getVelocityX() > 0 && getX() >= worldWidth - 234)
		{
			setMaxSpeedX(0f);
			setVelocityX(0f);
			if ((int)(Math.random() * 10000) % 2 == 0) setVelocityY(speed);
			else setVelocityY(-speed);
		}

		if (getVelocityX() == 0)
		{
			if (getY() < 140) setVelocityY(speed);
			if (getY() > gameContext.getEngine().getWorldHeight() - 140) setVelocityY(-speed);
		}

		// Create and fire a Mickey bullet
		if (health.isAlive() && (int)(Math.random() * 10000) % 7 == 0)
		{
			float[] xs = { getX() - 16, getX() + 48, getX() + 109, getX() + 130, getX() + 96, getX() + 21, getX() - 15 };
			float[] ys = { getY() - 43, getY() - 82, getY() - 56, getY() + 9, getY() + 57, getY() + 63, getY() + 27 };
			float[] velXs = { -0.15f, -0.001f, 0.065f, 0.065f, 0.05f, -0.035f, -0.15f };
			float[] velYs = { -0.05f, -0.1f, -0.1f, 0.03f, 0.1f, 0.08f, 0.05f };

			int which = (int)(Math.random() * 10000) % xs.length;
			//int which = 6;

			BabyBomb mb = new BabyBomb(xs[which],ys[which],velXs[which],velYs[which]);
			gameContext.getEngine().getMap().addObstacle(mb);

		}

		// Preventing enemies from putting in the reverse gear
		if (getVelocityX() > getMaxSpeedX()) setVelocityX(getVelocityX() - 0.003f);
		if (getVelocityX() < getMaxSpeedX()) setVelocityX(getMaxSpeedX());
	}
}
