package com.spacerats;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.spacerats.cache.CachePreloadException;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.ScreenManager;
import com.spacerats.level.MapManager;
import com.spacerats.sound.SoundManager;
import com.spacerats.entity.Player;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Planet;
import com.spacerats.entity.powerup.Heart;


import javax.swing.*;
import java.awt.*;

/**
 * @author Håkan Larsson
 */
public class HealthTest
{
	private MockObjectTestCase jmockTestcase;
	private Mock screenManager;
	private TileMap tileMap;
	private Mock soundManager;

	Injector anotherInjector;
	private Engine engine;

	@Before
	public void prepare() throws CachePreloadException
	{
		CacheManager.getInstance().purgeCache();
		CacheManager.getInstance().preloadContent();
		
		jmockTestcase = new MockObjectTestCase()
		{
		};

		CacheManager.getInstance().preloadContent();

		screenManager = new Mock(ScreenManager.class);
		tileMap = new MapManager().loadNextMap();
		soundManager = new Mock(SoundManager.class);

		Module module = new AbstractModule()
		{
			protected void configure()
			{
				bind(ScreenManager.class).toInstance((ScreenManager) screenManager.proxy());
				bind(TileMap.class).toInstance(tileMap);
				bind(SoundManager.class).toInstance((SoundManager) soundManager.proxy());
			}
		};

   		anotherInjector = Guice.createInjector(module);

		engine = anotherInjector.getInstance(Engine.class);
	}

	@After
	public void tearDown()
	{
	}
	
	@Test
	public void testHeartCollitionWithPlayer()
	{
		JFrame frame = new JFrame();
		screenManager.expects(jmockTestcase.once()).method("findFirstCompatibleMode").will(jmockTestcase.returnValue(null));
		screenManager.expects(jmockTestcase.once()).method("setFullScreen").withAnyArguments();
		screenManager.expects(jmockTestcase.atLeastOnce()).method("getFullScreenWindow").will(jmockTestcase.returnValue((Window) frame));
		screenManager.expects(jmockTestcase.atLeastOnce()).method("getHeight").will(jmockTestcase.returnValue(400));

		soundManager.expects(jmockTestcase.once()).method("playMusic").withAnyArguments();
		soundManager.expects(jmockTestcase.atLeastOnce()).method("playSFX").withAnyArguments();
		soundManager.expects(jmockTestcase.once()).method("muteMusicStart").withAnyArguments();
		soundManager.expects(jmockTestcase.once()).method("muteSFX").withAnyArguments();

		engine.init();

		Player player = engine.getMap().getPlayer();


		player.getHealth().decreaseHealth(50);
		
		assertEquals (50, player.getHealth().getCurrentHealth());

		Heart heart = new Heart ();

		engine.getMap().addObstacle((Obstacle) heart);

		boolean found = false;
		for (Obstacle obstacle : engine.getMap().getObstacles())
		{
			if (obstacle.equals(heart))
			{
				found = true;
				break;
			}
		}

		assertTrue ("Heart obstacle was found in the map.", found);

		heart.handleCollision(new GameContext(engine), player);

		assertEquals (100, player.getHealth().getCurrentHealth());

		for (Obstacle obstacle : engine.getMap().getObstacles())
		{
			if (obstacle.equals(heart))
			{
				fail ("Heart test failed");
			}
		}
	}

	@Test
	public void testHealthBundaries ()
	{
		Health health = new Health(new Player(0,0));

		health.decreaseHealth(30);

		assertEquals (70, health.getCurrentHealth());

		health.kill();

		assertEquals (0, health.getCurrentHealth());

		assertTrue ("Health is empty.", health.isEmpty());

		health.resetHealth();

		assertEquals (100, health.getCurrentHealth());

		assertFalse ("Health is not empty.", health.isEmpty());
	}

	@Test
	public void testDeadAliveState () throws InterruptedException
	{
		Health health = new Health(new Player(0,0));

		assertTrue ("It is alive", health.isAlive());

		health.decreaseHealth(100);
		assertTrue ("It is dying", health.isDying());

		Thread.sleep(Health.DYING_TIME + 10);
		assertTrue ("It is dead", health.isDead());

		health.resetHealth();
		assertTrue ("It is alive", health.isAlive());

		health.kill();
		assertTrue ("It is dying", health.isDying());

		Thread.sleep(Health.DYING_TIME + 10);
		assertTrue ("It is dead", health.isDead());

		health.increaseHealth(40);
		assertTrue ("It is alive", health.isAlive());
	}
}
