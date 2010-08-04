package com.spacerats.wii;

import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import com.spacerats.input.InputManager;

import java.io.IOException;

/**
 * @author Håkan Larsson
 */
public class SpaceMote extends Thread
{
	private WiiRemote wiiRemote;
	private Thread ledThread;
	private InputManager inputManager;
	private RatsWiiRemoteListener ratsWiiRemoteListener;

	public SpaceMote(InputManager inputManager)
	{
		wiiRemote = null;
		this.inputManager = inputManager;

	}

	public void run ()
	{
		try
		{
			wiiRemote = WiiRemoteJ.findRemote();

			if (wiiRemote != null)
			{
				System.out.println("Found a remote.");
				ratsWiiRemoteListener = new RatsWiiRemoteListener(wiiRemote, inputManager);
				wiiRemote.addWiiRemoteListener(ratsWiiRemoteListener);

				try
				{
					wiiRemote.requestStatus();
				}
				catch (IOException e)
				{
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}

				WiiRemoteJ.stopFind();

				ledThread = new Thread(new LedRunner(wiiRemote));
				ledThread.start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		while (true)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				ledThread.interrupt();
				System.out.println("SpaceMote Thread was interrupted. Returning home.");
				wiiRemote.removeWiiRemoteListener(ratsWiiRemoteListener);
				wiiRemote.disconnect();
				return;
			}
		}
	}
}
