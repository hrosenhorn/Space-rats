 package com.spacerats;

 import com.spacerats.entity.Obstacle;

 /**
 * @author Håkan Larsson
 */
public class Health
{
	private int maxHealth;
	
	public static final long DYING_TIME = 1100;
	private int currentHealth;
	private boolean godmode = false;

	private State lifeState;
	private long dyingStartTime = 0;

	private Obstacle owner;

	public enum State
	{
		ALIVE,
		DYING,
		DEAD;
	}

	public Health(Obstacle owner)
	{
		this.owner = owner;

		lifeState = State.ALIVE;
		currentHealth = maxHealth = 100;
	}

	public void setMaxHealth(int max)
	{
		currentHealth = maxHealth = max;
	}

	public boolean decreaseHealth (int amount)
	{
		if (!godmode)
		{
			currentHealth -= amount;

			if (currentHealth <= 0)
			{
				if (!isDying())
				{
					currentHealth = 0;
					setDying();
					
					return true;
				}
			}
		}

		return false;
	}

	public void increaseHealth (int amount)
	{
		if (!lifeState.equals(State.ALIVE) && currentHealth == 0)
		{
			setAlive();
		}

		currentHealth += amount;

		if (currentHealth > maxHealth)
		{
			resetHealth();
		}
	}

	public void resetHealth ()
	{
		currentHealth = maxHealth;
		setAlive();
	}

	public void setGodmode(boolean input)
	{
		godmode = input;
	}
	
	public boolean isGodmode()
	{
		return godmode;
	}
	
	public boolean isEmpty()
	{
		if (currentHealth == 0)
		{
			return true;
		}

		return false;
	}

	public int getCurrentHealth()
	{
		return currentHealth;
	}

	public void kill ()
	{
		currentHealth = 0;

		setDying();
	}

	public float healthPercentage()
	{
		return (float)currentHealth / (float)maxHealth;
	}

	private void setDying ()
	{
		lifeState = State.DYING;
		dyingStartTime = System.currentTimeMillis();
	}

	private void setDead ()
	{
		lifeState = State.DEAD;
	}

	private void setAlive ()
	{
		lifeState = State.ALIVE;
		dyingStartTime = 0;
	}

	public boolean isAlive ()
	{
		if (lifeState.equals(State.ALIVE))
		{
			return true;
		}

		return false;
	}

	public boolean isDying ()
	{
		if (lifeState.equals(State.DYING))
		{
			return true;
		}

		return false;
	}

	public boolean isDead ()
	{
		if (lifeState.equals(State.DEAD))
		{
			return true;
		}

		if (lifeState.equals(State.DYING) && System.currentTimeMillis() - dyingStartTime >= DYING_TIME)
		{
			setDead();
			return true;
		}

		return false;
	}
}
