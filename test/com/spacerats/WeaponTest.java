package com.spacerats;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import com.spacerats.cache.CachePreloadException;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.ScreenManager;
import com.spacerats.level.MapManager;
import com.spacerats.sound.SoundManager;
import com.spacerats.entity.Player;
import com.spacerats.entity.Obstacle;
import com.spacerats.weapon.ammo.Ammo;
import com.spacerats.weapon.MiniGun;
import com.spacerats.weapon.LaserGun;
import com.google.inject.Module;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import static org.junit.Assert.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author Håkan Larsson
 */
public class WeaponTest
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
	public void testFireWeapon ()
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
		player.setWeapon(new MiniGun(player));

		GameContext gameContext = new GameContext(engine);

		player.getWeapon().fire(gameContext);
		
		player.setWeapon(new LaserGun(player));

		player.getWeapon().fire(gameContext);

		boolean found = false;
		for (Obstacle obstacle : engine.getMap().getObstacles())
		{
			if (obstacle instanceof Ammo)
			{
				assertEquals ((int)player.getX(), (int)obstacle.getX());
				assertEquals ((int)player.getY(), (int)obstacle.getY());
				found = true;
				break;
			}
		}

		assertTrue ("The weapon was fired. ", found);
	}
}
