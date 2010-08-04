package com.spacerats.graphics;

import com.spacerats.GameContext;

import java.awt.*;

/**
 * @author Staffan Wennberg
 */
public class Transition
{
	private static Transition instance;
	private static boolean active = false, toBlack;
	private int transMode = 0;
	private int var1, var2, var3;
	private int[] array1;

	private final static int VERT_SQUARES = 20;
	private final static int HOR_SQUARES = 30;


	public Transition()
	{
		
	}

	public static Transition getInstance()
	{
		if (instance == null) instance = new Transition();
		return instance;
	}

	public static boolean active()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public void updateAndDraw(Graphics2D g)
	{
		switch (transMode)
		{
			case 0:
				if (toBlack)
				{
					var1 -= 50;
					if (var1 <= 0) setActive(false);
				}
				else
				{
					var1 += 30;
					if (var1 >= var3) setActive(false);
				}
				int[] xsLeft = {0, 0, var2 - var1};
				int[] ysLeft = {var3, var1, var3};

				int[] xsRight = {var2, var2, var1};
				int[] ysRight = {0, var3 - var1, 0};

				g.setColor(Color.BLACK);
				g.fillPolygon(xsLeft,ysLeft,3);
				g.fillPolygon(xsRight,ysRight,3);
		}
	}

	public void resetTransition(GameContext gameContext, boolean toBlack)
	{
		this.toBlack = toBlack;
		transMode = (int)(Math.random() * 10000) % 1;

		switch (transMode)
		{
			case 0:
				var2 = gameContext.getEngine().getWorldWidth();
				var3 = gameContext.getEngine().getWorldHeight();
				if (toBlack)
				{
					var1 = var3;
				}
				else
				{
					var1 = 0;
				}
		}
		setActive(true);
	}
}


// Storage för gamla kasserade transitions:
/*
updateAndDraw:

				int x = 0;
				for (int i : array1) x += array1[i];
				if (x == HOR_SQUARES * VERT_SQUARES)
				{
					setProgress(false);
				}
				else
				{
					int square;
					while(true) {
						square = (int)(Math.random() * 10000) % (HOR_SQUARES * VERT_SQUARES);
						if (array1[square] == 0) break;
					}
					array1[square] = 1;
				}
				g.setColor(Color.BLACK);
				for (int a = 0; a < VERT_SQUARES; a++)
				{
					for (int b = 0; b < HOR_SQUARES; b++)
					{
						if (array1[a * HOR_SQUARES + b] == 0)
						g.fillRect(a * var1, b * var2, var1, var2);
					}

				}
				break;

resetTransition:

				var1 = gameContext.getEngine().getWorldWidth() / HOR_SQUARES;
				var2 = gameContext.getEngine().getWorldHeight() / VERT_SQUARES;
				array1 = new int[HOR_SQUARES * VERT_SQUARES];
				for (int i : array1) array1[i] = 0;
				break;
*/