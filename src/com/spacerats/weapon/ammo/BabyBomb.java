package com.spacerats.weapon.ammo;

import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;
import com.spacerats.entity.Explosion;
import com.spacerats.entity.SoundEffect;
import com.spacerats.entity.enemy.AbstractEnemy;
import com.spacerats.graphics.Animation;
import com.spacerats.GameContext;
import com.spacerats.cache.CacheManager;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Staffan Wennberg
 */
public class BabyBomb extends AbstractAmmo
{
	private float rotation = 0;

	public BabyBomb(float x, float y, float velX, float velY)
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("baby"),1000);

		this.x = x;
		this.y = y;

		setMaxSpeedX(-0.25f);
		setMaxSpeedY(-0.3f);

		setVelocityX(velX);
		setVelocityY(velY);

		boundingRadius = 10;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (otherObstacle instanceof Player)
		{
			otherObstacle.delegateImpact(gameContext, 10);
			gameContext.getEngine().getMap().removeObstacle(this);
			//gameContext.getEngine().getMap().addObstacle(new Explosion(getX(),getY())); 
			gameContext.getEngine().getMap().addObstacle(new SoundEffect(getX(),getY()));
			gameContext.getEngine().getSoundManager().playSFX("explode");
		}
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		gameContext.getEngine().getMap().removeObstacle(this);
		gameContext.getEngine().getMap().addObstacle(new Explosion(getX() - 50,getY() - 30));
		gameContext.getEngine().getMap().addObstacle(new SoundEffect(getX() - 50,getY() - 30));
		gameContext.getEngine().getSoundManager().playSFX("explode");
		return true;
	}

	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);
		if (getVelocityX() > getMaxSpeedX()) setVelocityX(getVelocityX() - 0.001f);
		if (getVelocityY() > 0) setVelocityY(getVelocityY() - 0.001f);
		if (getVelocityY() < 0) setVelocityY(getVelocityY() + 0.001f);

		rotation -= 0.06f;
	}

	public void draw(Graphics2D g, int x, int y)
	{
		AffineTransform transform = getTransform(x,y);
		transform.rotate(rotation,10,10);
		g.drawImage(getImage(),transform,null);
	}
}
