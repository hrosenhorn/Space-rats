package com.spacerats.graphics;

import com.spacerats.GameContext;
import com.spacerats.entity.Obstacle;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Sprite
{
	protected Animation animation;
	protected AffineTransform transform = new AffineTransform();
	// position (pixels)
	protected float x;
	protected float y;
	// velocity (pixels per millisecond)
	protected float dx;
	protected float dy;

	// how fast can we move
	protected float maxSpeedX = 0;
	protected float maxSpeedY = 0;

	protected int boundingRadius;

	/**
	 * Creates a new Sprite object with the specified Animation.
	 */
	public Sprite(Animation animation, int boundingRadius)
	{
		this.animation = animation;
		this.boundingRadius = boundingRadius;
	}

	protected Sprite ()
	{
		
	}

	/**
	 * Updates this Sprite's Animation and its position based
	 * on the velocity.
	 */
	public void update(GameContext gameContext, long elapsedTime)
	{
		x += dx * elapsedTime;
		y += dy * elapsedTime;
		animation.update(elapsedTime);
	}

	/**
	 * Gets this Sprite's current x position.
	 */
	public float getX()
	{
		return x;
	}

	/**
	 * Gets this Sprite's current y position.
	 */
	public float getY()
	{
		return y;
	}

	/**
	 * Sets this Sprite's current x position.
	 */
	public void setX(float x)
	{
		this.x = x;
	}

	/**
	 * Sets this Sprite's current y position.
	 */
	public void setY(float y)
	{
		this.y = y;
	}

	public float getFireX()
	{
		return getX();
	}

	public float getFireY()
	{
		return getY();
	}

	/**
	 * Gets this Sprite's width, based on the size of the
	 * current image.
	 */
	public int getWidth()
	{
		return animation.getImage().getWidth(null);
	}

	/**
	 * Gets this Sprite's height, based on the size of the
	 * current image.
	 */
	public int getHeight()
	{
		return animation.getImage().getHeight(null);
	}

	/**
	 * Gets the horizontal velocity of this Sprite in pixels
	 * per millisecond.
	 */
	public float getVelocityX()
	{
		return dx;
	}

	/**
	 * Gets the vertical velocity of this Sprite in pixels
	 * per millisecond.
	 */
	public float getVelocityY()
	{
		return dy;
	}

	/**
	 * Sets the horizontal velocity of this Sprite in pixels
	 * per millisecond.
	 */
	public void setVelocityX(float dx)
	{
		this.dx = dx;
	}

	/**
	 * Sets the vertical velocity of this Sprite in pixels
	 * per millisecond.
	 */
	public void setVelocityY(float dy)
	{
		this.dy = dy;
	}

	public float getMaxSpeedX()
	{
		return maxSpeedX;
	}

	public void setMaxSpeedX(float maxSpeedX)
	{
		this.maxSpeedX = maxSpeedX;
	}

	public float getMaxSpeedY()
	{
		return maxSpeedY;
	}

	public void setMaxSpeedY(float maxSpeedY)
	{
		this.maxSpeedY = maxSpeedY;
	}



	/**
	 * Gets this Sprite's current image.
	 */
	public Image getImage()
	{
		return animation.getImage();
	}

	public int getBoundingRadius()
	{
		return boundingRadius;
	}

	public AffineTransform getTransform(int x, int y)
	{
		transform.setToTranslation(x,y);

		return transform;
	}	

	public void draw(Graphics2D g, int x, int y)
	{
		g.drawImage(getImage(), x, y, null);
	}

	public boolean invincible()
	{
		return false;
	}
}
