package com.spacerats;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.spacerats.graphics.Sprite;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * The TileMap class contains the data for a tile-based
 * map, including Sprites. Each tile is a reference to an
 * Image. Of course, Images are used multiple times in the tile
 * map.
 */
public class TileMap
{
	private Image[][] tiles;
	private List<Obstacle> obstacles;
	private Player player;
	private Sprite worldSprite;
	private String music;

	/**
	 * Creates a new TileMap with the specified width and
	 * height (in number of tiles) of the map.
	 */
	public TileMap(int width, int height)
	{
		tiles = new Image[width][height];
		obstacles = new LinkedList();

		worldSprite = new Sprite(null, 20);
		worldSprite.setX(400);
		worldSprite.setVelocityX(0.1f);
	}

	/**
	 * Gets the width of this TileMap (number of tiles across).
	 */
	public int getWidth()
	{
		return tiles.length;
	}

	/**
	 * Gets the height of this TileMap (number of tiles down).
	 */
	public int getHeight()
	{
		return tiles[0].length;
	}


	/**
	 * Gets the tile at the specified location. Returns null if
	 * no tile is at the location or if the location is out of
	 * bounds.
	 */
	public Image getTile(int x, int y)
	{
		if (x < 0 || x >= getWidth() ||
				y < 0 || y >= getHeight())
		{
			return null;
		}
		else
		{
			return tiles[x][y];
		}
	}

	/**
	 * Sets the tile at the specified location.
	 */
	public void setTile(int x, int y, Image tile)
	{
		tiles[x][y] = tile;
	}

	/**
	 * Gets the player Sprite.
	 */
	public Player getPlayer()
	{
		return player;
	}

	public Sprite getWorldSprite()
	{
		return worldSprite;
	}

	/**
	 * Sets the player Sprite.
	 */
	public void setPlayer(Player player)
	{
		this.player = player;

		for (Iterator iterator = obstacles.iterator(); iterator.hasNext();)
		{
			Sprite sprite = (Sprite) iterator.next();
			if (sprite instanceof Player)
			{
				iterator.remove();
			}
		}

		obstacles.add((Obstacle)player);
	}


	public void addObstacle(Obstacle obstacle)
	{
		obstacles.add(obstacle);
	}

	public void removeObstacle(Obstacle obstacle)
	{
		obstacles.remove(obstacle);
	}

	public String getMusic()
	{
		return music;
	}

	public void setMusic(String music)
	{
		this.music = music;
	}

	/**
	 * Returns a new copy of the obstacle list
	 * @return A copy of all obstacles
	 */
	public List<Obstacle> getObstacles()
	{
		List<Obstacle> newObstacles = new ArrayList<Obstacle> ();
		newObstacles.addAll(obstacles);

		return newObstacles;
	}
}
