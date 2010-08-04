package com.spacerats.level;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.net.URL;

import com.spacerats.TileMap;
import com.spacerats.TileMapRenderer;
import com.spacerats.entity.*;
import com.spacerats.entity.enemy.*;
import com.spacerats.entity.powerup.*;
import com.spacerats.utils.ResourceHelper;


/**
 * The MapManager class loads and manages tile Images and "host" Sprites used in the game.
 */
public class MapManager
{

	private ArrayList<Image> tiles;
	private int currentMap;

	/**
	 * Creates a new MapManager
	 */
	public MapManager()
	{
		tiles = new ArrayList<Image>();
		loadTileImages();
	}


	public TileMap loadNextMap()
	{
		TileMap map = null;
		while (map == null)
		{
			currentMap++;
			try
			{
				map = loadMap("maps/map" + currentMap + ".txt");
			}
			catch (IOException ex)
			{
				if (currentMap == 1) return null;
				currentMap = 0;
				map = null;
			}
		}
		return map;
	}

	public void resetGame()
	{
		currentMap = 0;
	}


	public TileMap reloadMap()
	{
		try
		{
			return loadMap("maps/map" + currentMap + ".txt");
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public int getCurrentLevel()
	{
		return currentMap;
	}


	private TileMap loadMap(String filename) throws IOException
	{
		ArrayList lines = new ArrayList();
		int width = 0;
		int height = 0;
		String music = null;

		// read every line in the text file into the list

		BufferedReader reader = null;

		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);

		if (inputStream != null)
		{
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);
		}
		else
		{
			reader = new BufferedReader(new FileReader(filename));
		}

		//BufferedReader reader = new BufferedReader(new FileReader(filename));
		while (true)
		{
			String line = reader.readLine();
			// no more lines to read
			if (line == null)
			{
				reader.close();
				break;
			}

			// add every line except for comments
			if (!line.startsWith("#"))
			{
				lines.add(line);
				width = Math.max(width, line.length());
			}

			if (line.startsWith("#MUSIC: "))
			{
				music = line.substring(8);
			}
		}

		// parse the lines to create a TileEngine
		height = lines.size();
		TileMap newMap = new TileMap(width, height);
		newMap.setMusic(music);
		for (int y = 0; y < height; y++)
		{
			String line = (String) lines.get(y);
			for (int x = 0; x < line.length(); x++)
			{
				char ch = line.charAt(x);

				// check if the char represents tile A, B, C etc.
				int tile = ch - 'A';
				if (tile >= 0 && tile < tiles.size())
				{
					newMap.setTile(x, y, tiles.get(tile));
				}
				else
				{
					switch (ch)
					{ 	// Ideally, more common items like enemies and obstacles should appear higher, but the difference
						// in time taken to generate the level is probably neglible.
						case 'x':
							addSprite(newMap, new Planet("AnimalPlanet.gif"), x, y);
							break;
						case 'e':
							addSprite(newMap, new catSlayer(), x, y);
							break;
						case 'f':
							addSprite(newMap, new snakeRat(), x, y);
							break;
						case 'p':
							addSprite(newMap, new propellerRat(),x,y);
							break;
						case 's':
							addSprite(newMap, new BlackHole(), x, y);
							break;
						case 'h':
							addSprite(newMap, new Heart(), x, y);
							break;
						case 'j':
							addSprite(newMap, new HalfHeart(), x, y);
							break;
						case 'i':
							addSprite(newMap, new Blast(),x,y);
							break;
						case '1':
							addSprite(newMap, new Extralife(),x,y);
							break;
						case 'M':
							addSprite(newMap, new Minigun(),x,y);
							break;
						case 'L':
							addSprite(newMap, new Lasergun(),x,y);
							break;
						case 'R':
							addSprite(newMap, new Missilelauncher(),x,y);
							break;
						case 'a':
							addSprite(newMap, new Ammobox(),x,y);
							break;
						case 'b':
							addSprite(newMap, new CowboyRat(), x, y);
							break;
						case 'm':
							addSprite(newMap, new MamaRat(), x, y);
							break;
						case 'g':
							addSprite(newMap, new Victory(), x, y);
							break;
						default:
							break;
					}
				}
			}
		}

		return newMap;
	}

	private void addSprite(TileMap map, Obstacle obstacle, int tileX, int tileY)
	{
		if (obstacle != null)
		{
			// center the sprite
			obstacle.setX(TileMapRenderer.tilesToPixels(tileX) + (TileMapRenderer.tilesToPixels(1) - obstacle.getWidth()) / 2);

			// bottom-justify the sprite
			obstacle.setY(TileMapRenderer.tilesToPixels(tileY + 1) - obstacle.getHeight());

			// add it to the map
			map.addObstacle(obstacle);
		}
	}

	// -----------------------------------------------------------
	// code for loading sprites and images
	// -----------------------------------------------------------

	public void loadTileImages()
	{
		// keep looking for tile A,B,C, etc. this makes it
		// easy to drop new tiles in the images/ directory
		char ch = 'A';
		while (true)
		{
			String name = "images/tile_" + ch + ".png";
			File file = new File(name);
			if (!file.exists()) break;
			tiles.add(ResourceHelper.loadImagePath(name));
			ch++;
		}
	}
}
