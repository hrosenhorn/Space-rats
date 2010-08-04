package com.spacerats;

import com.spacerats.graphics.*;
import com.spacerats.level.*;
import com.spacerats.menu.*;
import com.spacerats.entity.*;
import com.spacerats.entity.powerup.LonglivedPowerup;
import com.spacerats.entity.enemy.*;
import com.spacerats.sound.*;
import com.spacerats.input.*;
import com.spacerats.cache.CacheManager;
import com.spacerats.cache.CachePreloadException;
import com.spacerats.weapon.Weapon;
import com.spacerats.config.ConfigManager;
import com.google.inject.Inject;


import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Map;

/**
 * @author Håkan Larsson
 */
public class GameEngine implements Engine
{
	@Inject
	private ScreenManager screen;

	@Inject
	private TileMap map;
	private TileMapRenderer renderer;
	private MapManager mapmanager;
	private SpaceMenu menu;
	private GameContext gameContext;

	@Inject
	private SoundManager soundManager;
	private InputManager inputManager;
	private ConfigManager configManager;

	private GameAction moveLeft;
	private GameAction moveRight;
	private GameAction moveUp;
	private GameAction moveDown;
	private GameAction fire;

	private GameAction exit;
	private GameAction restartLevel;

	private GameAction cheat_ff;
	private GameAction cheat_rew;
	private GameAction cheat_god;
	private GameAction cheat_showBounding;
	private GameAction cheat_giveAmmo;
	private GameAction windowmode;

	public boolean showMenu = true, running = true, isPaused = false,
			gameover = true, dying = false;
	public long restartTime;

	private long renderLoopTime;

	private int tmpCollCount;

	public GameEngine()
	{
		screen = new ScreenManagerImpl();
		renderer = new TileMapRenderer();
		mapmanager = new MapManager();
		soundManager = new SoundManagerImpl();
		gameContext = new GameContext(this);

		configManager = ConfigManager.getInstance();
	}

	public void setWorldVelocity(float velocity)
	{
		map.getWorldSprite().setVelocityX(velocity);
	}

	public float getWorldVelocity()
	{
		return map.getWorldSprite().getVelocityX();
	}

	public TileMap getMap()
	{
		return map;
	}

	public TileMapRenderer getRenderer()
	{
		return renderer;
	}

	public SoundManager getSoundManager()
	{
		return soundManager;
	}

	/**
	 * Sets full screen mode and initiates and objects.
	 */
	public void init()
	{
		// Preload all images and sounds
		try
		{
			CacheManager.getInstance().preloadContent();
		}
		catch (CachePreloadException e)
		{
			e.printStackTrace();

			// Dont start the game if we are unable to preload our images.
			return;
		}

		map = mapmanager.loadNextMap();

		DisplayMode displayMode = screen.findFirstCompatibleMode(DisplayModes.POSSIBLE_MODES);
		screen.setFullScreen(displayMode);
		//screen.setWindowedMode();

		Window window = screen.getFullScreenWindow();
		window.setFont(new Font("Dialog", Font.PLAIN, 24));
		window.setBackground(Color.black);
		window.setForeground(Color.black);

		soundManager.muteSFX(!configManager.getSound());
		soundManager.muteMusicStart(!configManager.getMusic());
		initInput();

		/**
		 * The player
		 */
		createNewPlayer();
	}

	public void initInput()
	{
		moveLeft = new GameAction("moveLeft");
		moveRight = new GameAction("moveRight");
		moveUp = new GameAction("moveUp");
		moveDown = new GameAction("moveDown");
		exit = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
		restartLevel = new GameAction("restartLevel", GameAction.DETECT_INITAL_PRESS_ONLY);

		cheat_ff = new GameAction("cheat_ff", GameAction.DETECT_INITAL_PRESS_ONLY);
		cheat_rew = new GameAction("cheat_rew", GameAction.DETECT_INITAL_PRESS_ONLY);
		cheat_god = new GameAction("cheat_god", GameAction.DETECT_INITAL_PRESS_ONLY);
		cheat_showBounding = new GameAction("cheat_showBounding", GameAction.DETECT_INITAL_PRESS_ONLY);
		cheat_giveAmmo = new GameAction("cheat_giveAmmo", GameAction.DETECT_INITAL_PRESS_ONLY);
		windowmode = new GameAction("windowmode");

		fire = new GameAction("fire", GameAction.DETECT_INITAL_PRESS_ONLY);

		inputManager = new InputManager(screen.getFullScreenWindow());
		inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

		inputManager.mapToKey(restartLevel, KeyEvent.VK_R);
		inputManager.mapToKey(cheat_ff, KeyEvent.VK_O);
		inputManager.mapToKey(cheat_rew, KeyEvent.VK_I);
		inputManager.mapToKey(cheat_god, KeyEvent.VK_G);
		inputManager.mapToKey(cheat_showBounding, KeyEvent.VK_B);
		inputManager.mapToKey(cheat_giveAmmo, KeyEvent.VK_A);
		inputManager.mapToKey(windowmode, KeyEvent.VK_F);
		resetUserKeyMaps();
	}

	public void resetUserKeyMaps()
	{
		inputManager.clearMap(moveLeft);
		inputManager.clearMap(moveRight);
		inputManager.clearMap(moveUp);
		inputManager.clearMap(moveDown);
		inputManager.clearMap(exit);
		inputManager.clearMap(fire);
		inputManager.mapToKey(moveLeft, Integer.parseInt(configManager.getProperty("options.left")));
		inputManager.mapToKey(moveRight, Integer.parseInt(configManager.getProperty("options.right")));
		inputManager.mapToKey(moveUp, Integer.parseInt(configManager.getProperty("options.up")));
		inputManager.mapToKey(moveDown, Integer.parseInt(configManager.getProperty("options.down")));
		inputManager.mapToKey(exit, Integer.parseInt(configManager.getProperty("options.exit")));
		inputManager.mapToKey(fire, Integer.parseInt(configManager.getProperty("options.fire")));
	}

	private void checkInput(long elapsedTime)
	{
		if (exit.isPressed() && !Transition.active())
		{
			Transition.getInstance().resetTransition(gameContext, true);
			showMenu = true;
		}

		Player player = map.getPlayer();
		if (player.isAlive())
		{
			if (moveLeft.isPressed()) player.moveLeft();
			if (moveRight.isPressed()) player.moveRight();
			if (moveUp.isPressed()) player.moveUp();
			if (moveDown.isPressed()) player.moveDown();
			if (fire.isPressed()) player.getWeapon().fire(gameContext);
		}

		if (cheat_ff.isPressed()) setWorldVelocity(getWorldVelocity() + 0.1f);
		if (cheat_rew.isPressed()) setWorldVelocity(getWorldVelocity() - 0.1f);
		if (cheat_god.isPressed()) toggleGodmode();
		if (cheat_showBounding.isPressed()) toggleShowBounding();
		if (cheat_giveAmmo.isPressed()) map.getPlayer().getWeapon().setAmmocount(-1, true);
		if (windowmode.isPressed()) screen.setWindowedMode();
		if (restartLevel.isPressed()) restartGame();
	}

	public void shutdown()
	{
		soundManager.close();
		screen.restoreScreen();
		inputManager.shutdown();

		System.exit(0);
	}

	public void gameLoop()
	{
		renderLoopTime = System.currentTimeMillis();

		while (running)
		{
			tmpCollCount = 0;

			long presentTime = System.currentTimeMillis();
			if (!dying) restartTime = presentTime;
			if (!map.getPlayer().isAlive())
			{
				map.getWorldSprite().setVelocityX(0);
				if (map.getPlayer().getLives() == 0)
				{
					setGameover(true);
				}
				else
				{
					dying = true;
				}
			}

			if (presentTime - restartTime >= 2000) // 2000 msecs delay before restarting
			{
				restartMap(map.getPlayer());

				dying = false;
			}


			isPaused = false; // the whole point of this variable is to set elapsedTime to 0 when leaving the startup menu
			// If the screen manager is changing display or similar this will return false.
			if (!screen.isDrawable()) continue;

			String nextState = "";
			if (!map.getPlayer().isAlive() && ScoreManager.highscoreOrNot()) nextState = "highscoreenter";

			if (LevelIndicator.getInstance().active())
			{
				LevelIndicator.getInstance().run(this);
			}

			if (showMenu)
			{
				if (!Transition.active())
				{
					if (nextState == "highscoreenter")
						soundManager.playMusic("highscore");
					else
						soundManager.playMusic("menu");

					String state = runMenu(nextState);
					if (state != "play" && state != "restart") running = false;
					else
					{
						if (!(state.equals("play") && !gameover())) // = !resume
							LevelIndicator.getInstance().run(this);
						if (state.equals("restart")) restartGame();
						if (state.equals("play") && gameover()) restartGame();
						Transition.getInstance().resetTransition(gameContext, false);
					}
					soundManager.playMusic("sounds/" + map.getMusic());
					showMenu = false;
					isPaused = true;
					if (!map.getPlayer().isAlive()) restartGame();
					inputManager.resetAllGameActions();
				}
			}

			if (!Transition.active()) map.getPlayer().tick();

			long elapsedTime = System.currentTimeMillis() - renderLoopTime;
			renderLoopTime += elapsedTime;
			if (isPaused) elapsedTime = 0;

			// draw and update iScreen
			Graphics2D graphics2D = screen.getGraphics();

			// check controlls
			//if (!Transition.active())
			checkInput(elapsedTime);

			// Update all sprites
			if (!Transition.active() && !LevelIndicator.getInstance().active())
			{
				updateSprites(elapsedTime);
			}

			if (!LevelIndicator.getInstance().active())
				renderer.draw(graphics2D, map, screen.getWidth(), screen.getHeight(), this);

			if (Transition.active() && !LevelIndicator.getInstance().active())
				Transition.getInstance().updateAndDraw(graphics2D);


			graphics2D.dispose();
			screen.update();  // <----- NOTE


			try
			{
				if (Transition.active()) Thread.sleep(10);
				else Thread.sleep(20);
			}
			catch (InterruptedException ex)
			{

			}
		}
	}

	//TODO: the menu is using this keylisteners, shall we have it this way or some other?
	public void addKeyListener(KeyListener listener)
	{
		screen.getFullScreenWindow().addKeyListener(listener);
	}

	public void removeKeyListener(KeyListener listener)
	{
		screen.getFullScreenWindow().removeKeyListener(listener);
	}

	public TileMap restartMap(Player player)
	{
		map = mapmanager.reloadMap();
		map.setPlayer(player);

		renderer.setOffsetx(0);
		renderer.setOffsety(0);
		renderLoopTime = System.currentTimeMillis();

		player.reset(getWorldHeight());
		player.setLives(-1, false);
		
		return map;
	}

	public void restartGame()
	{
		ScoreManager.reset();
		mapmanager.resetGame();
		createNewPlayer();
		loadNextMap();
		setGameover(false);
	}

	public void loadNextMap()
	{
		Player player = map.getPlayer();
		player.setCoordinates(150, getWorldHeight() / 2);
		map = mapmanager.loadNextMap();
		renderer.setOffsetx(0);
		renderer.setOffsety(0);
		renderLoopTime = System.currentTimeMillis();

		soundManager.playMusic("sounds/" + map.getMusic());
		map.setPlayer(player);
		LevelIndicator.getInstance().run(this);
	}

	private void createNewPlayer()
	{
		map.setPlayer(new Player(150, getWorldHeight() / 2));
	}

	public int getWorldWidth()
	{
		return screen.getWidth();
	}

	public int getWorldHeight()
	{
		return screen.getHeight();
	}

	public Graphics2D getDrawingSurface()
	{
		return screen.getGraphics();
	}

	public void updateScreen()
	{
		screen.update();
	}

	public void updateSprites(long elapsedTime)
	{
		for (Obstacle obstacle : map.getObstacles())
		{
			// only update yet visible sprites - STAFFAN
			float limitX = map.getWorldSprite().getX() +
					(gameContext.getEngine().getWorldWidth() / 2) +
					obstacle.getWidth();

			// When objects moves outside the left side of the screen remove them.
			if (obstacle.getX() + obstacle.getWidth() < gameContext.getEngine().getMap().getWorldSprite().getX() -
					gameContext.getEngine().getWorldWidth() / 2
					&& !(obstacle instanceof LonglivedPowerup))
			{
				removeObstacle(obstacle);
				continue;
			}

			Obstacle collidingObstacle = getObstacleCollision(obstacle);
			if (collidingObstacle != null)
			{
				obstacle.handleCollision(gameContext, collidingObstacle);
			}

			if (obstacle.getX() < limitX)
			{
				((Sprite) obstacle).update(gameContext, elapsedTime);
			}
		}

		Sprite scrollSprite = map.getWorldSprite();

		// Prevent the game from freezing in the end
		int tile = TileMapRenderer.pixelsToTiles(scrollSprite.getX() + screen.getWidth() / 2);
		if (scrollSprite.getVelocityX() > 0)
		{
			if (tile == map.getWidth())
			{
				scrollSprite.setVelocityX(0);
				scrollSprite.setVelocityY(0);
			}
			else
			{
				// The world sprite is not included in the sprite lite
				float scrollX = scrollSprite.getX();
				float scrollY = scrollSprite.getY();
				scrollSprite.setX(scrollX + scrollSprite.getVelocityX() * elapsedTime);
				scrollSprite.setY(scrollY + scrollSprite.getVelocityY() * elapsedTime);
			}
		}

		//TODO: in this offscreen code below or somewhere else we should take in to account the tiles and makes player "bump" ordinary tiles

		// if player is to far left, make X-speed positive and move him right
		if (map.getPlayer().getX() < Math.abs(renderer.getOffsetX()))
		{
			map.getPlayer().setVelocityX(Math.abs(map.getPlayer().getVelocityX()));
			map.getPlayer().setX(Math.abs(renderer.getOffsetX()) + 2);
			map.getPlayer().moveRight();
		}

		// if player is to far right, make the X-speed negative and move him left
		if (map.getPlayer().getX() > Math.abs(getWorldWidth() - renderer.getOffsetX()) - map.getPlayer().getWidth())
		{
			map.getPlayer().setVelocityX(-Math.abs(map.getPlayer().getVelocityX()));
			map.getPlayer().moveLeft();
		}

		// if player is to far up, make the Y-speed positive and move player down
		if (map.getPlayer().getY() < renderer.getOffsetY())
		{
			map.getPlayer().setVelocityY(Math.abs(map.getPlayer().getVelocityY()));
			map.getPlayer().moveDown();
		}

		// if player is to low on the map, make the Y-speed negative and move him up
		if (map.getPlayer().getY() > Math.abs(getWorldHeight() - renderer.getOffsetY()) - map.getPlayer().getHeight())
		{
			map.getPlayer().setVelocityY(-Math.abs(map.getPlayer().getVelocityY()));
			map.getPlayer().moveUp();
		}
	}

	public Obstacle getObstacleCollision(Obstacle obstacle)
	{
		// Just ignore it if the obstacle of its outside the screen

		if (!isVisible(obstacle))
		{
			return null;
		}


		for (Obstacle otherObstacle : map.getObstacles())
		{
			if (!isVisible(otherObstacle))
			{
				continue;
			}

			if (isCollision(obstacle, otherObstacle))
			{
				return otherObstacle;
			}
		}

		return null;
	}

	/*
	public Obstacle getObstacleCollision(Obstacle obstacle)
	{
		for (Obstacle otherObstacle : map.getObstacles())
		{
			if (otherObstacle.getX() < map.getWorldSprite().getX()+getWorldWidth() && isCollision(obstacle, otherObstacle)) return otherObstacle;

			if (isCollision(obstacle, otherObstacle))
			{
				return otherObstacle;
			}
		}

		return null;
	}
	*/

	public boolean isVisible(Obstacle obstacle)
	{
		float maxX = map.getWorldSprite().getX() + getWorldWidth() / 2 + (obstacle.getWidth() * 2);
		float minX = map.getWorldSprite().getX() - getWorldWidth() / 2;

		//System.out.println("MaxX: " + maxX + " MinX: " + minX + " OBSX: " + obstacle.getX());

		if (obstacle.getX() <= maxX && obstacle.getX() >= minX)
		{

			return true;
		}

		return false;
	}

	public boolean isCollision(Obstacle s1, Obstacle s2)
	{
		tmpCollCount++;
		if (s1 == s2)
			return false; //if the game checks a creature with itself for collision something is wrong, maybe this should throw an exception?

		int dx = (int) (s1.getX() - s2.getX());
		int dy = (int) (s1.getY() - s2.getY());

		int minDistance = s1.getBoundingRadius() + s2.getBoundingRadius();

		if (dx * dx + dy * dy < minDistance * minDistance) return true;

		return false;
	}

	public String runMenu(String initialState)
	{
		if (menu == null) menu = new SpaceMenu(this);
		return menu.run(initialState);
	}

	public void killPlayer()
	{
		map.getPlayer().kill();
	}

	public void updatePlayerWeapon(Weapon weapon)
	{
		map.getPlayer().setWeapon(weapon);
	}

	public void toggleGodmode()
	{
		if (map.getPlayer().getHealth().isGodmode())
			map.getPlayer().getHealth().setGodmode(false);
		else
			map.getPlayer().getHealth().setGodmode(true);
	}

	public void increaseHealth(int amount)
	{
		map.getPlayer().getHealth().increaseHealth(amount);
	}

	public void decreaseHealth(int amount)
	{
		map.getPlayer().getHealth().decreaseHealth(amount);
	}

	public void toggleShowBounding()
	{
		if (renderer.isShowingBounding())
			renderer.setShowBounding(false);
		else
			renderer.setShowBounding(true);
	}

	public void removeObstacle(Obstacle obstacle)
	{
		map.removeObstacle(obstacle);
	}

	public boolean gameover()
	{
		return gameover;
	}

	public void setGameover(boolean gameover)
	{
		this.gameover = gameover;
	}

	public int getCurrentLevel()
	{
		return mapmanager.getCurrentLevel();
	}

	public void collideObstacles(Obstacle one, Obstacle two)
	{
		if (one instanceof Player)
			if (((Player)one).isInvincible())
				return;
		if (two instanceof Player)
			if (((Player)two).isInvincible())
				return;
		float oneX = one.getX();
		float oneY = one.getY();
		float oneVelY = one.getVelocityY();
		float oneVelX = one.getVelocityX();
		float twoX = two.getX();
		float twoY = two.getY();
		float twoVelX = two.getVelocityX();
		float twoVelY = two.getVelocityY();
		
		two.setX(twoX - twoVelX);
		two.setVelocityX(-twoVelX);
		one.setX(oneX - oneVelX);
		one.setVelocityX(-oneVelX);

		two.setY(twoY - twoVelY);
		two.setVelocityY(-twoVelY);
		one.setY(oneY - oneVelY);
		one.setVelocityY(-oneVelY);
	}

	public ScreenManager getScreen()
	{
		return screen;
	}
}
