package com.spacerats.wii;

import wiiremotej.WiiRemote;

import java.io.IOException;

/**
 * @author Håkan Larsson
 */
public class LedRunner extends Thread
{
	WiiRemote wiiRemote = null;

	public LedRunner(WiiRemote wiiRemote)
	{
		this.wiiRemote = wiiRemote;
	}

	public void run()
	{
		int illuminatedLed = 0;

		System.out.println("Starting led illuminator on remote with address: " + wiiRemote.getBluetoothAddress());

		while (true)
		{
			try
			{
				for (int counter = 0; counter < 4; counter++)
				{
					if (illuminatedLed == counter)
					{
						try
						{
							wiiRemote.setLEDIlluminated(counter, true);
						}
						catch (IOException e)
						{
							System.out.println("Unable to illuminate led on remote, shutting down.");
							return;
						}
					}
					else
					{
						try
						{
							wiiRemote.setLEDIlluminated(counter, false);
						}
						catch (IOException e)
						{
							System.out.println("Unable to illuminate led on remote, shutting down.");
							return;
						}
					}
				}

				illuminatedLed++;

				if (illuminatedLed > 3)
				{
					illuminatedLed = 0;
				}

				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				System.out.println("LED Thread was interrupted. Returning home.");
				return;
			}
		}
	}
}