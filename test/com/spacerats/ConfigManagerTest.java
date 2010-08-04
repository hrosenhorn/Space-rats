package com.spacerats;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import com.spacerats.config.ConfigManager;
import static org.junit.Assert.*;

/**
 * @author Håkan Larsson
 */
public class ConfigManagerTest
{
	@Before
	public void prepare()
	{
	}

	@After
	public void tearDown()
	{
	}

	//TODO: Add tests here to verify a working configuration manager
	@Test
	public void testConfigurationProperties ()
	{
		ConfigManager configManager = ConfigManager.getInstance();
		assertNotNull(configManager.getMusic());
		assertNotNull(configManager.getSound());
	}
}
