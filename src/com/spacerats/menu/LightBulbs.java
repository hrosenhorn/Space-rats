package com.spacerats.menu;

import java.awt.*;

/**
 * @author Staffan Wennberg
 */
public class LightBulbs
{
	private boolean schemeActive = false;
	private int state = 1;
	private boolean doReset = true;
	private int lightScheme;

	private int rel_hor;
	private int rel_vert;

	private int[] coordinates =
	{
		42,120, // R
		50,98,
		67,81,
		89,72,
		113,74,
		122,95,
		113,117,
		98,135,
		79,153,
		59,169,
		80,181,
		102,191,
		148,180, // A
		151,156,
		156,132,
		164,108,
		177,84,
		196,64,
		211,84,
		214,107,
		216,131,
		216,154,
		217,178,
		258,68, // T
		282,66,
		306,64,
		330,62,
		286,90,
		284,117,
		284,143,
		284,170,
		452,45, // S
		428,49,
		405,55,
		383,65,
		369,85,
		387,102,
		412,107,
		436,113,
		455,130,
		440,151,
		413,157,
		385,156, // 43 altogether
	};

	private int numBulbs;
	private int counter, counter2; // used for various light schemes
	private long time, span;

	private final Color COLOR_1 = Color.green;
	private final Color COLOR_2 = new Color(40,100,180); // 40,100,180
	private int bulbSize = 19;

	int[] scheme;

	public LightBulbs(int rel_hor, int rel_vert) {
		numBulbs = coordinates.length / 2;
		scheme = new int[getBulbs()];
		time = System.currentTimeMillis();


		this.rel_hor = rel_hor;
		this.rel_vert = rel_vert;

		span = 10;
	}

	public int getBulbs() {
		return numBulbs;
	}

	public int getX(int bulb) {
		return (rel_hor + coordinates[bulb * 2]);
	}

	public int getY(int bulb) {
		return (rel_vert + coordinates[(bulb * 2) + 1]);
	}

	public int getBulbSize() {
		return bulbSize;
	}

	public Color getColor(int choice) {
		if (choice == 1) return COLOR_1;
		else return COLOR_2;
	}



	public int[] getLightScheme() {
		if ((System.currentTimeMillis() - time) >= span) {
			time = System.currentTimeMillis();


			// State 1: Starting with unlit bulbs
			// State 2: Starting with lit bulbs
			
			if (!schemeActive) {
				int[] state1options = {0,1,2,4,5,6,7};
				int[] state2options = {3,4,5,8};
				if (state == 1)
					lightScheme = state1options[(int)((Math.random() * 10000) % state1options.length)];
				else
					lightScheme = state2options[(int)((Math.random() * 10000) % state2options.length)];

				//lightScheme = 2;
				schemeActive = true;
				counter = 0;
				counter2 = 0;
			}

			// Resetting the array
			if (doReset) for (int bulb = 0; bulb < getBulbs(); bulb++) { scheme[bulb] = 0; }
			doReset = true;

			
			// All different light schemas
			// 0 = traversing clockwise
			if (lightScheme == 0)
			{
				span = 15;
				if (counter >= getBulbs())
				{
					schemeActive = false;
					span = 100;
				}
				else
				{
					scheme[counter] = 1;
					counter++;
				}
			}

			// 1 = traversing counter-clockwise
			else if (lightScheme == 1)
			{
				span = 15;
				if (counter >= getBulbs())
				{
					schemeActive = false;
					span = 100;
				}
				else
				{
					scheme[getBulbs() - 1 - counter] = 1;
					counter++;
				}
			}

			// 2 = filling all bulbs clockwise
			else if (lightScheme == 2)
			{
				span = 5;
				if (counter >= getBulbs() -1)
				{
					schemeActive = false;
					state = 2;
					doReset = false;
					span = 100;
				}
				else
				{
					for (int a = 0; a < getBulbs(); a++)
					{
						if (a <= counter) scheme[a] = 1;
						else scheme[a] = 0;
					}
					counter++;
				}
			}

			// 3 = un-filling all bulbs clockwise
			else if (lightScheme == 3)
			{
				span = 5;
				if (counter >= getBulbs() -1)
				{
					schemeActive = false;
					state = 1;
					for (int a = 0; a < getBulbs(); a++) { scheme[a] = 0; }
					span = 100;
				}
				else
				{
					for (int a = 0; a < getBulbs(); a++)
					{
						if (a <= counter) scheme[a] = 0;
						else scheme[a] = 1;
					}
					counter++;
				}
			}

			// 4 = blinking all bulbs
			else if (lightScheme == 4)
			{
				span = 200;
				if (counter == 15)
				{
					schemeActive = false;
					state = ((int)(Math.random() * 10000) % 2) + 1;
					for (int a = 0; a < getBulbs(); a++) { scheme[a] = state - 1; }
				}
				else
				{
					for (int a = 0; a < getBulbs(); a++) { if (counter % 2 == 1) scheme[a] = 1; else scheme[a] = 0; }
					counter++;
				}
			}

			// 5 = blink every second bulb
			else if (lightScheme == 5)
			{
				span = 150;
				if (counter == 19)
				{
					schemeActive = false;
					state = ((int)(Math.random() * 10000) % 2) + 1;
					for (int a = 0; a < getBulbs(); a++) { scheme[a] = state - 1; }
				}
				else
				{
					for (int a = 0; a < getBulbs(); a++) { if ((a + counter) % 2 == 0) scheme[a] = 1; else scheme[a] = 0; }
					counter++;
				}
			}

			// 6 = fill all bulbs in random order
			else if (lightScheme == 6)
			{
				span = 20;
				if (counter == getBulbs())
				{
					span = 800;
					schemeActive = false;
					state = 2;
				}
				else
				{
					while(true) {
						int number = (int)(Math.random() * 10000) % (getBulbs());
						if (scheme[number] == 0) {
							scheme[number] = 1;
							break;
						}
					}
					doReset = false;
					counter++;
				}
			}

			// 7 = traverse, and fill one at a time
			else if (lightScheme == 7)
			{
				span = 0;
				int goal = getBulbs() - counter;
				if (counter == getBulbs())
				{
					span = 800;
					schemeActive = false;
					state = 2;
				}
				else
				{

					for (int a = 0; a < getBulbs(); a++) {
						if (a == counter2) scheme[a] = 1;
						if (a >= goal) scheme[a] = 1;
					}

					counter2++;
					if (counter2 >= goal) {
						counter++;
						counter2 = 0;
						if (counter == getBulbs()) doReset = false;
					}
				}
			}

			// 8 = all bulbs are lit except one traversing bulb
			else if (lightScheme == 8)
			{
				span = 40;
				if (counter == getBulbs())
				{
					schemeActive = false;
				}
				else
				{
					for (int a = 0; a < getBulbs(); a++)
					{
						if (a == counter) scheme[a] = 0;
						else scheme[a] = 1;
					}
					counter++;
					if (counter == getBulbs()) doReset = false;
				}
			}

			else
			{
				// nothing...
			}
		}			
		return scheme;
	}
}
