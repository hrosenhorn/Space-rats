package com.spacerats;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import com.spacerats.graphics.Sprite;
import com.spacerats.utils.ResourceHelper;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;
import com.spacerats.entity.powerup.LonglivedPowerup;
import com.spacerats.cache.CacheManager;
import com.spacerats.menu.StarScroller;
import com.spacerats.weapon.Weapon;
import com.spacerats.weapon.Dummy;
import com.spacerats.weapon.AbstractWeapon;

/**
 * The TileMapRenderer class draws a TileMap on the screen.
 * It draws all tiles, sprites, and an optional background image
 * centered around the position of the player.
 * <p/>
 * <p>If the width of background image is smaller the width of
 * the tile map, the background image will appear to move
 * slowly, creating a parallax background effect.
 * <p/>
 * <p>Also, three static methods are provided to convert pixels
 * to tile positions, and vice-versa.
 * <p/>
 * <p>This TileMapRender uses a tile size of 64.
 */
public class TileMapRenderer
{
	public static final int TILE_SIZE = 64;
	// the size in bits of the tile
	// Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
	private static final int TILE_SIZE_BITS = 6;

	private Image background;
	private int offsetx = 0;
	private int offsety = 0;
	private boolean showBoundingRadius = false;

	private float health = 1.0f; // Used for nice animations in the health bar


	public TileMapRenderer()
	{
		background = ResourceHelper.loadImagePath("images/background.jpg");
	}

	public void setOffsetx(int offsetx)
	{
		this.offsetx = offsetx;
	}

	public void setOffsety(int offsety)
	{
		this.offsety = offsety;
	}

	public int getOffsetX()
	{
		return offsetx;
	}

	public int getOffsetY()
	{
		return offsety;
	}

	/**
	 * Converts a pixel position to a tile position.
	 */
	public static int pixelsToTiles(float pixels)
	{
		return pixelsToTiles(Math.round(pixels));
	}


	/**
	 * Converts a pixel position to a tile position.
	 */
	public static int pixelsToTiles(int pixels)
	{
		// use shifting to get correct values for negative pixels
		return pixels >> TILE_SIZE_BITS;

		// or, for tile sizes that aren't a power of two,
		// use the floor function:
		//return (int)Math.floor((float)pixels / TILE_SIZE);
	}


	/**
	 * Converts a tile position to a pixel position.
	 */
	public static int tilesToPixels(int numTiles)
	{
		// no real reason to use shifting here.
		// it's slighty faster, but doesn't add up to much
		// on modern processors.
		return numTiles << TILE_SIZE_BITS;

		// use this if the tile size isn't a power of 2:
		//return numTiles * TILE_SIZE;
	}


	/**
	 * Sets the background to draw.
	 */
	public void setBackground(Image background)
	{
		this.background = background;
	}

	public void setShowBounding(boolean input)
	{
		showBoundingRadius = input;
	}

	public boolean isShowingBounding()
	{
		return showBoundingRadius;
	}

	/**
	 * Draws the specified TileMap.
	 */
	public void draw(Graphics2D graphics2D, TileMap map, int screenWidth, int screenHeight, GameEngine engine)
	{
		Sprite player = map.getPlayer();
		int mapWidth = tilesToPixels(map.getWidth());

		// get the scrolling position of the map
		int offsetX = screenWidth / 2 - Math.round(map.getWorldSprite().getX()) - TILE_SIZE;
		offsetX = Math.min(offsetX, 0);
		offsetX = Math.max(offsetX, screenWidth - mapWidth);

		// get the y offset to draw all sprites and tiles
		int offsetY = screenHeight - tilesToPixels(map.getHeight());

		this.offsetx = offsetX;
		this.offsety = offsetY;

		// draw black background, if needed
		if (background == null || screenHeight > background.getHeight(null))
		{
			graphics2D.setColor(Color.black);
			graphics2D.fillRect(0, 0, screenWidth, screenHeight);
		}

		// draw parallax background image
		if (background != null)
		{
			int x = offsetX * (screenWidth - background.getWidth(null)) / (screenWidth - mapWidth);

			int y = screenHeight - background.getHeight(null);

			graphics2D.drawImage(background, x, y, null);
		}

		// Scrolling stars
		StarScroller scroller = StarScroller.getInstance();
		if (scroller != null)
		{
			graphics2D.setColor(Color.WHITE);
			ArrayList<ArrayList<Point>> stars = scroller.updatedStars(0.25f);
			for (ArrayList<Point> star : stars)
			{
				for (Point aStar : star)
				{
					graphics2D.fillOval((int) aStar.getX(), (int) aStar.getY(), 2, 2);
				}
			}
		}

		graphics2D.setColor(Color.black);

		// draw the visible tiles
		int firstTileX = pixelsToTiles(-offsetX);
		int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
		for (int y = 0; y < map.getHeight(); y++)
		{
			for (int x = firstTileX; x <= lastTileX; x++)
			{
				Image image = map.getTile(x, y);
				if (image != null)
				{
					graphics2D.drawImage(image, tilesToPixels(x) + offsetX, tilesToPixels(y) + offsetY, null);
				}
			}
		}

		// draw obstacles
		int obstacles = 0;
		for (Obstacle obstacle : map.getObstacles())
		{
			if (!engine.isVisible(obstacle) && !(obstacle instanceof LonglivedPowerup))
			{
				continue;
			}

			int x = Math.round(obstacle.getX()) + offsetX - obstacle.getBoundingRadius();
			int y = Math.round(obstacle.getY()) + offsetY - obstacle.getBoundingRadius();

			obstacle.draw(graphics2D, x, y);

			graphics2D.setColor(Color.RED);
			if (showBoundingRadius)
				graphics2D.drawOval(x, y, obstacle.getBoundingRadius() * 2, obstacle.getBoundingRadius() * 2);

			//graphics2D.drawLine((int)obstacle.getX() + offsetX, (int)obstacle.getY() + offsetY, (int)obstacle.getX() + offsetX, (int)obstacle.getY() + offsetY + 25);

			obstacles++;
		}
		
		//Player is drawn above enemies and powerups...
		player.draw(graphics2D,
			Math.round(player.getX()) + offsetX - player.getBoundingRadius(),
			Math.round(player.getY()) + offsetY - player.getBoundingRadius());

		// ...and the score...
		int sy = 17;
		int sx = 65;
		int sx2 = 103;

		graphics2D.drawImage(CacheManager.getInstance().getPreloadedImage("score_background"), 10, 10, null);
		graphics2D.drawImage(CacheManager.getInstance().getPreloadedImage("score_title"), sx, sy, null);
		graphics2D.drawImage(ScoreManager.getScoreImage(1), sx + sx2, sy + 1, null);
		graphics2D.drawImage(ScoreManager.getScoreImage(2), sx + sx2 + 21, sy + 1, null);
		graphics2D.drawImage(ScoreManager.getScoreImage(3), sx + sx2 + 2 * 21, sy + 1, null);
		graphics2D.drawImage(ScoreManager.getScoreImage(4), sx + sx2 + 3 * 21, sy + 1, null);
		graphics2D.drawImage(ScoreManager.getScoreImage(5), sx + sx2 + 4 * 21, sy + 1, null);
		graphics2D.drawImage(ScoreManager.getScoreImage(6), sx + sx2 + 5 * 21, sy + 1, null);

		graphics2D.drawImage(CacheManager.getInstance().getPreloadedImage("lives_title"), sx, sy + 44, null);

		AffineTransform transform = map.getPlayer().getTransform(sx + 95, sy + 42);

		// variable for actual number of lives, change this later
		Player p = (Player) player;
		int lives = p.getLives();

		/*
		if (lives > 4)
		{
			// Do something nice, only 4 lives fit into the bar...
		}
		else
		{
		*/
			for (int x = 0; x < lives; x++)
			{
				if (x == 3 && lives > 4)
				{
					graphics2D.drawImage(CacheManager.getInstance().getPreloadedImage("dots"),
							sx + 204, sy + 55, null);
					break;
				}
				graphics2D.drawImage(CacheManager.getInstance().getPreloadedImage("life"),
						sx + 95 + x * 35, sy + 42, null);
			}
		//}

		// ammo bar
		graphics2D.drawImage(CacheManager.getInstance().getPreloadedImage("ammo_title"), sx + 53, sy + 88, null);
		AbstractWeapon w = (AbstractWeapon) p.getWeapon();
		int currAmmo = w.getAmmoCount();
		if (currAmmo < 0)
			graphics2D.drawImage(CacheManager.getInstance().getPreloadedImage("infinite"), sx + 172, sy + 89, null);
		else
		{
			char[] ammoString = Integer.toString(currAmmo).toCharArray();
			for (int c = 0; c < ammoString.length; c++)
			{
				graphics2D.drawImage(CacheManager.getInstance().getPreloadedImage(
						"score_" + String.valueOf(ammoString[c])),
						sx + 172 + c * 21, sy + 89, null);
			}
		}



		// health bar
		float barheight = 193;
		float healthTmp = map.getPlayer().getHealth().healthPercentage();
		if (health < healthTmp) health += 0.01;
		if (health > healthTmp) health -= 0.01;

		int red = Math.abs((int) ((1f - health) * 255) * 3);
		if (red > 255) red = 255;
		int green = Math.abs((int) (health * 255) % 256);
		graphics2D.setColor(new Color(red, green, 0));

		if (health > 0.0)
		{
			int perc = (int) (health * 193f);
			boolean drawn = false;
			for (int c = 0; c < 192; c++)
			{
				if (!map.getPlayer().isAlive()) break;

				if (c > perc) break;
				int x = -1;
				int length = 0;
				int y = sy + 337 - c;
				switch (c)
				{
					case 0:
						x = sx - 44;
						length = 10;
						break;
					case 1:
						x = sx - 45;
						length = 12;
						break;
					case 2:
						x = sx - 46;
						length = 14;
						break;
					case 3:
						x = sx - 47;
						length = 16;
						break;
					case 4:
						x = sx - 48;
						length = 18;
						break;
					case 5:
						x = sx - 48;
						length = 18;
						break;
					case 6:
						x = sx - 48;
						length = 18;
						break;
						// And then all in the middle...
					case 185:
						x = sx - 48;
						length = 18;
						break;
					case 186:
						x = sx - 48;
						length = 18;
						break;
					case 187:
						x = sx - 48;
						length = 18;
						break;
					case 188:
						x = sx - 47;
						length = 16;
						break;
					case 189:
						x = sx - 46;
						length = 14;
						break;
					case 190:
						x = sx - 45;
						length = 12;
						break;
					case 191:
						x = sx - 44;
						length = 10;
						break;
						// The middle default value...
					default: // x = sx - 49;length = 20;
				}
				if (x != -1) graphics2D.drawLine(x, y, x + length, y);
				else
					if (perc > 6 && !drawn)
					{
						drawn = true;
						int visibleBar = Math.min(perc, 184) - 6;
						graphics2D.fillRect(sx - 49, sy + 337 - visibleBar - 6, 21, visibleBar);
					}
			}
		}
		else
		{
			if (map.getPlayer().isAlive()) graphics2D.drawLine(sx - 44, sy + 337, sx - 34, sy + 337);
		}

		// Current weapon
		Weapon current = map.getPlayer().getWeapon();
		if (current != null && !(current instanceof Dummy))
		{
			Image image = CacheManager.getInstance().getPreloadedImage(
					current.getClass().getSimpleName().toString().toLowerCase());

			graphics2D.drawImage(image, sx + 3 - (image.getWidth(null) / 2), sy + 93, null);
		}

		// contours
		graphics2D.setColor(Color.BLACK);
		graphics2D.drawArc(sx - 49, sy + 145, 20, 14, 0, 180);
		graphics2D.drawLine(sx - 50, sy + 153, sx - 50, sy + 330);
		graphics2D.drawArc(sx - 49, sy + 324, 20, 14, 180, 180);
		graphics2D.drawLine(sx - 28, sy + 153, sx - 28, sy + 330);

		float numOfDelimiters = 25;
		float dy_delimiter = barheight / numOfDelimiters;

		for (int d = 1; d < numOfDelimiters; d++)
		{
			int delimiter_y = (int) (sy + 339 - d * dy_delimiter);
			graphics2D.drawLine(sx - 49, delimiter_y, sx - 29, delimiter_y);
		}

		//GAME OVER is drawn above everything else
		if (engine.gameover())
		{
			graphics2D.drawImage(CacheManager.getInstance().getPreloadedImage("gameover"),
					screenWidth / 2 - 322,
					300, null);
		}


	}
}
