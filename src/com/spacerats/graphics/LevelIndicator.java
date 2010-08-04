package com.spacerats.graphics;

import com.spacerats.GameEngine;
import com.spacerats.menu.StarScroller;
import com.spacerats.cache.CacheManager;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Staffan Wennberg
 */
public class LevelIndicator
{
	//private int level;
	private long time;
	private boolean active = false;
	private static LevelIndicator instance;

	public static LevelIndicator getInstance()
	{
		if (instance == null) instance = new LevelIndicator();
		return instance;
	}

	public boolean active()
	{
		return active;
	}

	public void run(GameEngine engine)
	{
		if (!active)
		{
			active = true;
			time = System.currentTimeMillis();
		}

		Graphics2D g = engine.getScreen().getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0,0,engine.getWorldWidth(),engine.getWorldHeight());

		StarScroller scroller = StarScroller.getInstance();
		if (scroller != null)
		{
			g.setColor(Color.WHITE);
			ArrayList<ArrayList<Point>> stars = scroller.updatedStars(0.25f);
			for (ArrayList<Point> star : stars)
			{
				for (Point aStar : star)
				{
					g.fillOval((int) aStar.getX(), (int) aStar.getY(), 2, 2);
				}
			}
		}

		g.setColor(Color.YELLOW);
		int level = engine.getCurrentLevel();

		// TODO: This is only working up to level 9, since there is no image "score_10.png" etc

		g.drawImage(CacheManager.getInstance().getPreloadedImage("level_bg"),
				engine.getWorldWidth() / 2 - 57, engine.getWorldHeight() / 2 - 6, null);
		g.drawImage(CacheManager.getInstance().getPreloadedImage("level"),
				engine.getWorldWidth() / 2 - 30,engine.getWorldHeight()/2, null);
		g.drawImage(CacheManager.getInstance().getPreloadedImage("score_" + Integer.toString(level)),
				engine.getWorldWidth() / 2 + 40,engine.getWorldHeight()/2, null);

		long currTime = System.currentTimeMillis();
		if (currTime - time > 1500)
		{
			active = false;
		}
	}
}
