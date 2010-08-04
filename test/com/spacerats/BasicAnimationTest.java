package com.spacerats;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import com.spacerats.graphics.Animation;
import com.spacerats.utils.ResourceHelper;
import static org.junit.Assert.*;
import java.awt.*;

import junit.framework.TestCase;

/**
 * @author Håkan Larsson
 */
public class BasicAnimationTest
{
	@Before
	public void prepare()
	{
		
	}

	@After
	public void tearDown()
	{
	}

	@Test
	public void testAnimationImage () throws InterruptedException
	{
		Image player = ResourceHelper.loadImagePath("images/player.png");
		Image scroll = ResourceHelper.loadImagePath("images/scroll.png");
		long elapsedTime = 110;

		Animation animation = new Animation();
		animation.addFrame(player, 100);
		animation.addFrame(scroll, 100);

		Thread.sleep(elapsedTime);
		animation.update(elapsedTime);

		assertEquals (scroll, animation.getImage());
	}
}
