package com.spacerats.wii;

import wiiremotej.event.*;
import wiiremotej.*;

import java.io.IOException;
import java.awt.*;

import com.spacerats.input.InputManager;
import com.spacerats.input.GameAction;

/**
 * @author Håkan Larsson
 */
public class RatsWiiRemoteListener implements WiiRemoteListener
{
	private WiiRemote wiiRemote;
	private InputManager inputManager;

	private RatsAccelerometerMouse accelerometer;
	private boolean accelerometerEnabled = false;

	public RatsWiiRemoteListener(WiiRemote wiiRemote, InputManager inputManager)
	{
		this.wiiRemote = wiiRemote;
		this.inputManager = inputManager;

		try
		{
			wiiRemote.setAccelerometerEnabled(true);
			accelerometer = new RatsAccelerometerMouse(1, 1, AccelerometerMouse.WII_REMOTE, inputManager);
		}
		catch (AWTException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void buttonInputReceived(WRButtonEvent wrButtonEvent)
	{
		if (wrButtonEvent.wasPressed(WRButtonEvent.HOME))
		{
			System.out.println("Home pressed, shutting down mote.");

			try
			{
				wiiRemote.vibrateFor(150);
				System.out.println("Vibrating...");
			}
			catch (IOException e)
			{
				System.out.println("Vibrating failed.");
			}

			inputManager.shutdown();
		}

		if (wrButtonEvent.wasPressed(WRButtonEvent.RIGHT))
		{
			getActionByAlias("moveUp").press();
		}
		if (wrButtonEvent.wasReleased(WRButtonEvent.RIGHT))
		{
			getActionByAlias("moveUp").release();
		}
		if (wrButtonEvent.wasPressed(WRButtonEvent.LEFT))
		{
			getActionByAlias("moveDown").press();
		}
		if (wrButtonEvent.wasReleased(WRButtonEvent.LEFT))
		{
			getActionByAlias("moveDown").release();
		}
		if (wrButtonEvent.wasPressed(WRButtonEvent.UP))
		{
			getActionByAlias("moveLeft").press();
		}
		if (wrButtonEvent.wasReleased(WRButtonEvent.UP))
		{
			getActionByAlias("moveLeft").release();
		}
		if (wrButtonEvent.wasPressed(WRButtonEvent.DOWN))
		{
			getActionByAlias("moveRight").press();
		}
		if (wrButtonEvent.wasReleased(WRButtonEvent.DOWN))
		{
			getActionByAlias("moveRight").release();
		}

		if (wrButtonEvent.wasPressed(WRButtonEvent.ONE))
		{
			getActionByAlias("fire").press();
		}
		if (wrButtonEvent.wasReleased(WRButtonEvent.ONE))
		{
			getActionByAlias("fire").release();
		}




		if (wrButtonEvent.wasReleased (WRButtonEvent.A))
		{
			/*
			if (accelerometerEnabled)
			{
				accelerometerEnabled = false;
				System.out.println("Disabling accelerometer as mouse input.");
			}
			else
			{
				accelerometerEnabled = true;
				System.out.println("Enabling accelerometer as mouse input.");
			}
			*/
		}
	}

	public void statusReported(WRStatusEvent wrStatusEvent)
	{
		double rawBatteryLevel = wrStatusEvent.getBatteryLevel();
		int batteryLevel = (int) (rawBatteryLevel * 100);

		System.out.println("Battery level is at " + batteryLevel + "%.");
	}

	public void accelerationInputReceived(WRAccelerationEvent wrAccelerationEvent)
	{
	}

	public void IRInputReceived(WRIREvent wrirEvent)
	{
	}

	public void extensionInputReceived(WRExtensionEvent wrExtensionEvent)
	{
	}

	public void extensionConnected(WiiRemoteExtension wiiRemoteExtension)
	{
		//System.out.println("Something was added with code: " + wiiRemoteExtension.getCode());
		//System.out.println("Payload: " + wiiRemoteExtension.getPayload());
		try
		{
			wiiRemote.setExtensionEnabled(true);
		}
		catch (IOException e)
		{
			System.out.println("Failed to enable extension.");
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public void extensionPartiallyInserted()
	{
	}

	public void extensionUnknown()
	{
	}

	public void extensionDisconnected(WiiRemoteExtension wiiRemoteExtension)
	{
	}

	public void combinedInputReceived(WRCombinedEvent wrCombinedEvent)
	{
		if (wrCombinedEvent.getAccelerationEvent() != null)
		{
			WRAccelerationEvent wEvent = wrCombinedEvent.getAccelerationEvent();


			if (accelerometerEnabled)
			{
				accelerometer.processMouseEvent(wEvent);
			}
		}
	}

	public void disconnected()
	{
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
}
