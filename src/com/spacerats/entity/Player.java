package com.spacerats.entity;

import com.spacerats.graphics.Sprite;
import com.spacerats.graphics.Animation;
import com.spacerats.graphics.PredefinedAnimations;
import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.weapon.*;
import com.spacerats.cache.CacheManager;

import java.awt.geom.AffineTransform;
import java.awt.*;

/**
 * @author Håkan Larsson
 */
public class Player extends Sprite implements Obstacle
{
	/**
	 * Creates a new Sprite object with the specified Animation.
	 */
	private float inertia = 0.075f;

	private Health health;
	private int lives;

	private Weapon weapon;
	private Weapon oldWeapon;

	private int invincible = 0, iCounter = 80;

	public Player(float startingX, float startingY)
	{
		// add the player to the map
		animation = new Animation();

		animation.addFrame(CacheManager.getInstance().getPreloadedImage("player-small"), 1000);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("player-small2"), 1000);

		boundingRadius = getImage().getHeight(null) / 2;
		setX( startingX);
		setY( startingY);

		setMaxSpeedX(0.75f);
		setMaxSpeedY(1.0f);

		health = new Health(this);
		health.setMaxHealth(100);
		lives = 2;

		weapon = new LameShot(this);
		oldWeapon = new LameShot(this);
	}

	public void reset(int worldHeight)
	{
		setCoordinates(150f, worldHeight / 2);
		getHealth().resetHealth();
		weapon = new LameShot(this);
		oldWeapon = new LameShot(this);
		
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("player-small"), 1000);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("player-small2"), 1000);
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		if (!(isInvincible()))
		{
			if (health.decreaseHealth(amount)) //decreases health, and returns 'true' if dying
			{
				gameContext.getEngine().getSoundManager().playSFX("explode");
				kill();
			}

			if (health.isAlive())
			{
				gameContext.getEngine().getSoundManager().playSFX("bump1");
				startInvincible();
			}
		}
		return true;
	}

	//TODO: fix tick and captain cat will say tack
	public void tick()
	{
		if ( getVelocityX() > 0 && Math.ceil(getVelocityX()) != 0 ) setVelocityX(getVelocityX()*0.95f);
		if ( getVelocityX() < 0 && Math.floor(getVelocityX()) != 0 ) setVelocityX(-Math.abs(getVelocityX()*0.9f));
		if ( getVelocityY() > 0 && Math.ceil(getVelocityY()) != 0 ) setVelocityY(getVelocityY()*0.9f);
		if ( getVelocityY() < 0 && Math.floor(getVelocityY()) != 0 ) setVelocityY(-Math.abs(getVelocityY()*0.9f));
	}

	
	// I felt inertia*2 resulted in uncontrollably fast acceleration, but feel free to change it back - AXEL
	public void moveRight()
	{
		if (getVelocityX() < getMaxSpeedX()) setVelocityX(getVelocityX() + inertia*(float)1.2);
	}

	public void moveLeft()
	{
		if (getVelocityX() > -getMaxSpeedX()) setVelocityX(getVelocityX() - inertia*(float)1.2);
	}

	public void moveUp()
	{
		if (getVelocityY() > -getMaxSpeedY()) setVelocityY(getVelocityY() - inertia*(float)1.2);
	}

	public void moveDown()
	{
		if (getVelocityY() < getMaxSpeedY()) setVelocityY(getVelocityY() + inertia*(float)1.2);
	}

	public void setCoordinates(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public void kill()
	{
		health.kill();
		animation = PredefinedAnimations.explosion();

		setVelocityX(0);
		setVelocityY(0);
	}

	public boolean isAlive ()
	{
		return health.isAlive();
	}

	public Health getHealth()
	{
		return health;
	}

	public Weapon getWeapon()
	{
		return weapon;
	}

	public Weapon getOldWeapon()
	{
		return oldWeapon;
	}
	
	public void setWeapon(Weapon weapon)
	{
		if (!(this.weapon.getType().equals(weapon.getType())))
		{
			this.weapon = weapon;
			oldWeapon = weapon;
		}
		else
			this.weapon.setAmmocount(20, false);
	}

	public int getLives()
	{
		return lives;
	}

	// if absolute is true, then 'lives' is set to 'value'
	// otherwise 'value' is added to 'lives'
	public void setLives(int value, boolean absolute)
	{
		if (absolute) lives = value;
		else lives += value;
	}

	public AffineTransform getTransform(int x, int y)
	{
		transform.setToTranslation(x,y);

		return transform;
	}

	public boolean isInvincible()
	{
		return (invincible > 0);
	}

	public void startInvincible()
	{
		invincible = 1000;
		iCounter = 80;
	}

	public void draw(Graphics2D g, int x, int y)
	{
		//g.setColor(Color.YELLOW);
		//g.drawString(Integer.toString(iCounter),x,y+80);
		if (!isInvincible() || (isInvincible() && iCounter % 2 == 1))
		{
			g.drawImage(getImage(),x,y,null);
		}
	}

	public void update(GameContext gameContext, long elapsedTime)
	{
		super.update(gameContext, elapsedTime);
		
		if ( weapon.getAmmoCount() == 0) 
			weapon = new LameShot(this);
		
		if (isInvincible())
		{
			invincible -= elapsedTime;
			iCounter -= 1;
			if (iCounter < 0) 
				iCounter = 80;
		}
	}
}

