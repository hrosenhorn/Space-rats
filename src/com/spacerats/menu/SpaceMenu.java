package com.spacerats.menu;

import com.spacerats.Engine;

/**
 * @author Staffan Wennberg
 */
public class SpaceMenu
{
	private Engine engine;

	public SpaceMenu(Engine engine)
	{
		this.engine = engine;
	}

	public String run(String initialState)
	{
		SRMenu menu;
		if (initialState == null || initialState.equals("")) menu = new SRMenu(engine,"main");
		else menu = new SRMenu(engine,initialState);
		engine.addKeyListener(menu);
		String action = menu.run();
		engine.removeKeyListener(menu);
		//return (action.equals("play"));
		return action;
	}

}
