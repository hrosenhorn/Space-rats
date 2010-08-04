package com.spacerats;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import com.google.inject.*;
import com.spacerats.graphics.ScreenManager;
import com.spacerats.entity.Player;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Planet;
import com.spacerats.level.MapManager;
import com.spacerats.cache.CacheManager;
import com.spacerats.cache.CachePreloadException;
import com.spacerats.sound.SoundManager;
import javax.swing.*;
import java.awt.*;
import static org.junit.Assert.*;

/**
 * @author Håkan Larsson
 */
public class PlayerTest
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
	public void testPlayerLoosingCondition() throws CachePreloadException
	{
		JFrame frame = new JFrame();
		screenManager.expects(jmockTestcase.once()).method("findFirstCompatibleMode").will(jmockTestcase.returnValue(null));
		screenManager.expects(jmockTestcase.once()).method("setFullScreen").withAnyArguments();
		screenManager.expects(jmockTestcase.atLeastOnce()).method("getFullScreenWindow").will(jmockTestcase.returnValue((Window) frame));
		screenManager.expects(jmockTestcase.atLeastOnce()).method("getHeight").will(jmockTestcase.returnValue(400));

		soundManager.expects(jmockTestcase.once()).method("playMusic").withAnyArguments();
		soundManager.expects(jmockTestcase.once()).method("muteMusicStart").withAnyArguments();
		soundManager.expects(jmockTestcase.once()).method("muteSFX").withAnyArguments();
		soundManager.expects(jmockTestcase.atLeastOnce()).method("playSFX").withAnyArguments();


		engine.init();
		Player player = engine.getMap().getPlayer();

		assertTrue ("Player is alive", player.isAlive());

		for (Obstacle obstacle : engine.getMap().getObstacles())
		{
			if (obstacle instanceof Planet)
			{
				obstacle.handleCollision(new GameContext(engine), player);
				break;
			}
		}

		//assertFalse ("Player didn't take damage", player.getHealth().getCurrentHealth() < Health.MAX_HEALTH); 
	}
}
