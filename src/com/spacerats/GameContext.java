package com.spacerats;

/**
 * @author Håkan Larsson
 */
public class GameContext
{
	private Engine engine;

	public GameContext(Engine engine)
	{
		this.engine = engine;
	}

	public Engine getEngine()
	{
		return engine;
	}
}
