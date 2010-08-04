package com.spacerats;

import com.spacerats.graphics.ScreenManager;

/**
 * @author Håkan Larsson
 */
public class SpaceRats
{
	Engine engine;
    ScreenManager screenManager;

    public SpaceRats()
	{
         engine = new GameEngine();
    }

	public void run ()
	{
		try
		{
			engine.init();
           	engine.gameLoop();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			engine.shutdown();
		}
	}
}
