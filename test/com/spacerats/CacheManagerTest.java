package com.spacerats;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import com.spacerats.cache.CacheManager;
import com.spacerats.cache.CachePreloadException;
import com.spacerats.utils.ResourceHelper;
import static org.junit.Assert.*;

import java.awt.*;


/**
 * @author Håkan Larsson
 */
public class CacheManagerTest
{
	private CacheManager cacheManager;

	@Before
	public void prepare() throws CachePreloadException
	{
		cacheManager = CacheManager.getInstance();		

		CacheManager.getInstance().purgeCache();
		CacheManager.getInstance().preloadContent();		
	}

	@After
	public void tearDown()
	{
	}

	@Test (expected = CachePreloadException.class)
	public void testPreloadNonExistingImage () throws CachePreloadException
	{
		cacheManager.preloadImage("bogus", ResourceHelper.loadImagePath("images/non-existing-image.png"));
	}

	@Test
	public void testPreloadImages() throws CachePreloadException
	{
		cacheManager.preloadImage("player-small", ResourceHelper.loadImagePath("images/player-small.png"));
		assertTrue("Image is not null", !cacheManager.getPreloadedImage("player-small").equals(null));
	}
}
