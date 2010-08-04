package com.spacerats.wii;

import wiiremotej.AccelerometerMouse;
import wiiremotej.event.WRAccelerationEvent;

import java.awt.*;

import com.spacerats.input.InputManager;
import com.spacerats.input.GameAction;

/**
 * @author Håkan Larsson
 */
public class RatsAccelerometerMouse extends AccelerometerMouse
{
	private int sensitivity;
	private InputManager inputManager;
	private boolean upPressed = false;
	private boolean downPressed = false;
	private boolean leftPressed = false;
	private boolean rightPressed = false;
	private boolean firePressed = false;

	public RatsAccelerometerMouse(double xSensitivity, double ySensitivity, int device, InputManager inputManager) throws IllegalArgumentException, AWTException, SecurityException
	{
		super(xSensitivity, ySensitivity, device);
		this.inputManager = inputManager;

		sensitivity = 1;
	}

	private GameAction getActionByAlias (String alias)
	{
		for (GameAction gameAction : inputManager.getKeyActions())
		{
			if (gameAction == null)
			{
				continue;
			}

			if (gameAction.getName().equals (alias))
			{
				return gameAction;
			}
		}

		return null;
	}

	public void processMouseEvent(WRAccelerationEvent wrAccelerationEvent)
	{
		double roll = wrAccelerationEvent.getRoll();
		double pitch = wrAccelerationEvent.getPitch();

		//System.out.println("Roll: " + roll + ", pitch: " + pitch);
		try
		{
			if (roll > 0)
			{
				if (leftPressed)
				{
					leftPressed = false;
					getActionByAlias("moveLeft").release();

				}

				getActionByAlias("moveRight").press();
				rightPressed = true;
			}
			else
			{
				if (rightPressed)
				{
					rightPressed = false;
					getActionByAlias("moveRight").release();
				}
				
				getActionByAlias("moveLeft").press();
				leftPressed = true;
			}

			if (pitch > 0)
			{
				if (downPressed)
				{
					getActionByAlias("moveDown").release();
					downPressed = false;
				}

				getActionByAlias("moveUp").press();
				upPressed = true;
			}
			else
			{
				if (upPressed)
				{
					getActionByAlias("moveUp").release();
					upPressed = false;
				}

				getActionByAlias("moveDown").press();
				downPressed = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public int getSensitivity()
	{
		return sensitivity;
	}

	public void setSensitivity(int sensitivity)
	{
		if (sensitivity < 0)
		{
			return;
		}

		this.sensitivity = sensitivity;
	}

	public void increaseSensitivity()
	{
		sensitivity++;

		if (sensitivity < 0)
		{
			sensitivity = 1;
		}
	}

	public void decreaseSensitivity()
	{
		sensitivity--;
	}
}
