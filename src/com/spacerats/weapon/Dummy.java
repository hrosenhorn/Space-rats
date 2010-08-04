package com.spacerats.weapon;

import com.spacerats.GameContext;
import com.spacerats.entity.Obstacle;

/**
 * @author Håkan Larsson
 */
public class Dummy extends AbstractWeapon
{
	public Dummy(Obstacle gunman)
	{
		super(gunman);
	}

	public void fire(GameContext gameContext)
	{
		// Dont fire anything with this weapon
	}
}
