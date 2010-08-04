package com.spacerats;

import com.google.inject.ImplementedBy;
import com.spacerats.graphics.ScreenManagerImpl;
import com.spacerats.entity.Obstacle;
import com.spacerats.weapon.Weapon;
import com.spacerats.sound.SoundManager;

import java.awt.event.KeyListener;
import java.awt.*;

/**
 * @author Håkan Larsson
 */
@ImplementedBy (GameEngine.class)
public interface Engine
{
	/**
	 * Set the velocity the world will move in
	 *
	 * @param velocity Speed, default is 0.1
	 */
	void setWorldVelocity(float velocity);

	/**
	 * Returns the velocity the world is moving in
	 *
	 * @return the speed the world is moving in
	 */
	float getWorldVelocity();

	void init();

	void shutdown();

	void gameLoop();

	public int getWorldWidth();

	public int getWorldHeight();

	public void addKeyListener(KeyListener listener);

	public void removeKeyListener(KeyListener listener);

	public Graphics2D getDrawingSurface();

	public void updateScreen();

	String runMenu(String initialState);

	public void killPlayer();
	public void updatePlayerWeapon (Weapon weapon);

	public void toggleGodmode();
	public void increaseHealth (int amount);
	public void decreaseHealth (int amount);


	public void toggleShowBounding();
	
	public void removeObstacle (Obstacle obstacle);
	public void collideObstacles(Obstacle one, Obstacle two);


	public void loadNextMap();

	public TileMap getMap();
	public SoundManager getSoundManager();
	public void resetUserKeyMaps();
	public boolean isVisible(Obstacle obstacle);
}
