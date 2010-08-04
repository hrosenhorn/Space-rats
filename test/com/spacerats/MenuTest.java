package com.spacerats;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.jmock.MockObjectTestCase;
import com.spacerats.menu.SRMenu;
import com.spacerats.config.ConfigManager;
import com.spacerats.cache.CacheManager;
import com.spacerats.cache.CachePreloadException;
import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.*;
import java.util.Properties;

/**
 * @author Håkan Larsson
 */
public class MenuTest
{
	Engine engine;

	@Before
	public void prepare() throws CachePreloadException
	{
		engine = new GameEngine();

		CacheManager.getInstance().purgeCache();
		CacheManager.getInstance().preloadContent();
	}

	@After
	public void tearDown()
	{
	}

	@Test
	public void testMenuStates ()
	{
		ConfigManager configManager = ConfigManager.getInstance();
		configManager.setMusic(false);
		configManager.setSound(false);
		configManager.setFullscreen(false);
		
		configManager.setProperty("options.up",1);
		configManager.setProperty("options.down",2);
		configManager.setProperty("options.right",3);
		configManager.setProperty("options.left",4);
		configManager.setProperty("options.fire",5);
		configManager.setProperty("options.pause",6);
		configManager.setProperty("options.exit",7);
		configManager.setProperty("options.mute",8);

		((GameEngine)engine).initInput();
		SRMenu menu = new SRMenu(engine, "main");

		assertTrue ("State is main", "main".equals(menu.getState()));

		// This is our fake component that sends keyEvents
		Button button = new Button ();

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));

		assertTrue ("State is options", "options".equals(menu.getState()));

		// Test the sound option
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		assertEquals (true, configManager.getSound());

		// Test the music option
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		assertEquals (true, configManager.getMusic());

		// Test the controles
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		assertTrue ("State is controls", "controls".equals(menu.getState()));

		// Move right
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_RIGHT));
		assertEquals (KeyEvent.VK_RIGHT, Integer.parseInt(configManager.getProperty("options.right")));

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));

		// Move left
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_LEFT));
		assertEquals (KeyEvent.VK_LEFT, Integer.parseInt(configManager.getProperty("options.left")));

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));

		// Move up
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_UP));
		assertEquals (KeyEvent.VK_UP, Integer.parseInt(configManager.getProperty("options.up")));

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));

		// Move down
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		assertEquals (KeyEvent.VK_DOWN, Integer.parseInt(configManager.getProperty("options.down")));

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));

		// Fire
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_SPACE));
		assertEquals (KeyEvent.VK_SPACE, Integer.parseInt(configManager.getProperty("options.fire")));

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));

		// Pause
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_PAUSE));
		assertEquals (KeyEvent.VK_PAUSE, Integer.parseInt(configManager.getProperty("options.pause")));
		
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));

		// Mute
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_M));
		assertEquals (KeyEvent.VK_M, Integer.parseInt(configManager.getProperty("options.mute")));

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));

		// Exit
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ESCAPE));
		assertEquals (KeyEvent.VK_ESCAPE, Integer.parseInt(configManager.getProperty("options.exit")));
		
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));

		// Back to options
		assertTrue ("State is options", "options".equals(menu.getState()));

		// Test fullscreen
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));
		assertEquals (true, configManager.getFullscreen());

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));

		// Back to main
		assertTrue ("State is main", "main".equals(menu.getState()));

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));

		// Credits
		assertTrue ("State is credits", "credits".equals(menu.getState()));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ESCAPE));

		// Back to main
		assertTrue ("State is main", "main".equals(menu.getState()));

		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN));
		menu.keyPressed(new KeyEvent(button, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER));

		assertTrue ("State is quit", "quit".equals(menu.getState()));
	}
}
