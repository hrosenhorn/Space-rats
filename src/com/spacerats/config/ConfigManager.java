package com.spacerats.config;

import java.util.Properties;
import java.io.*;

/**
 * @author Håkan Larsson
 */
public class ConfigManager
{
	//public static final String CONFIG_FILENAME = "src/com/spacerats/spacerats.properties";
	public static final String CONFIG_FILENAME = "spacerats.properties";

	private static ConfigManager configManagerSingleton;

	private Properties properties = null;
	private Boolean sound, music, fullscreen, wiimote;

	private ConfigManager()
	{
		try
		{
			loadConfig(CONFIG_FILENAME);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not load settings file " + CONFIG_FILENAME, e);
		}
	}

	protected ConfigManager(Properties properties)
	{
		readProperties(properties);
	}

	public static synchronized ConfigManager getInstance()
	{
		if (configManagerSingleton == null)
		{
			configManagerSingleton = new ConfigManager();
		}
		return configManagerSingleton;
	}

	public void reloadConfiguration(String filename)
	{
		try
		{
			loadConfig(filename);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not load settings file " + filename, e);
		}
	}

	public static synchronized void setConfig(Properties props)
	{
		configManagerSingleton = new ConfigManager(props);
	}

	public synchronized void save()
	{
		try
		{
			saveConfig(CONFIG_FILENAME);
		}
		catch (Exception e)
		{

		}
	}

	public synchronized void saveConfig(String filename) throws ConfigManagerException, IOException
	{
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename))));

		if (writer == null)
		{
			File file = new File(filename);
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		}

		writer.println("options.fullscreen = " + getFullscreen().toString());
		writer.println("options.sound = " + getSound().toString());
		writer.println("options.music = " + getMusic().toString());
		writer.println("options.up = " + getProperty("options.up"));
		writer.println("options.down = " + getProperty("options.down"));
		writer.println("options.left = " + getProperty("options.left"));
		writer.println("options.right = " + getProperty("options.right"));
		writer.println("options.fire = " + getProperty("options.fire"));
		writer.println("options.pause = " + getProperty("options.pause"));
		writer.println("options.exit = " + getProperty("options.exit"));
		writer.println("options.mute = " + getProperty("options.mute"));
		writer.println("highscore.1.name = " + getProperty("highscore.1.name"));
		writer.println("highscore.1.score = " + getProperty("highscore.1.score"));
		writer.println("highscore.2.name = " + getProperty("highscore.2.name"));
		writer.println("highscore.2.score = " + getProperty("highscore.2.score"));
		writer.println("highscore.3.name = " + getProperty("highscore.3.name"));
		writer.println("highscore.3.score = " + getProperty("highscore.3.score"));
		writer.println("highscore.4.name = " + getProperty("highscore.4.name"));
		writer.println("highscore.4.score = " + getProperty("highscore.4.score"));
		writer.println("highscore.5.name = " + getProperty("highscore.5.name"));
		writer.println("highscore.5.score = " + getProperty("highscore.5.score"));
		writer.println("highscore.6.name = " + getProperty("highscore.6.name"));
		writer.println("highscore.6.score = " + getProperty("highscore.6.score"));
		writer.println("highscore.7.name = " + getProperty("highscore.7.name"));
		writer.println("highscore.7.score = " + getProperty("highscore.7.score"));
		writer.println("highscore.8.name = " + getProperty("highscore.8.name"));
		writer.println("highscore.8.score = " + getProperty("highscore.8.score"));
		writer.println("highscore.9.name = " + getProperty("highscore.9.name"));
		writer.println("highscore.9.score = " + getProperty("highscore.9.score"));
		writer.println("highscore.10.name = " + getProperty("highscore.10.name"));
		writer.println("highscore.10.score = " + getProperty("highscore.10.score"));

		writer.println("input.wiimote = " + getProperty("input.wiimote"));

		writer.close();

	}

	private synchronized void loadConfig(String filename) throws ConfigManagerException, IOException
	{
		// If the configuration file is packaged with the jar we might need our contextClassLoader, might work with just getClass().getResource
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		//this.getClass().getResource()
		if (stream == null)
		{
			System.out.println("Config file " + filename + " not found in class path. Trying user.home...");

			//String userHome = System.getProperty("user.home");
			//File file = new File(userHome + "/" + filename);

			File file = new File(filename);
			stream = new FileInputStream(file);
		}

		// create default properties

		Properties defaults = new Properties();
		defaults.setProperty("options.fullscreen", "true");
		defaults.setProperty("options.sound", "true");
		defaults.setProperty("options.music", "true");
		defaults.setProperty("options.up", "38");
		defaults.setProperty("options.down", "40");
		defaults.setProperty("options.right", "39");
		defaults.setProperty("options.left", "37");
		defaults.setProperty("options.fire", "32");
		defaults.setProperty("options.pause", "80");
		defaults.setProperty("options.exit", "81");
		defaults.setProperty("options.mute", "77");

		defaults.setProperty("highscore.1.name", "YODA");
		defaults.setProperty("highscore.1.score", "10000");

		defaults.setProperty("highscore.2.name", "BOB");
		defaults.setProperty("highscore.2.score", "9000");

		defaults.setProperty("highscore.3.name", "BILL");
		defaults.setProperty("highscore.3.score", "8000");

		defaults.setProperty("highscore.4.name", "TUDOR");
		defaults.setProperty("highscore.4.score", "7000");

		defaults.setProperty("highscore.5.name", "FRITZ");
		defaults.setProperty("highscore.5.score", "6000");

		defaults.setProperty("highscore.6.name", "FOZZIE");
		defaults.setProperty("highscore.6.score", "5000");

		defaults.setProperty("highscore.7.name", "ZAPHOD");
		defaults.setProperty("highscore.7.score", "4000");

		defaults.setProperty("highscore.8.name", "CLARK");
		defaults.setProperty("highscore.8.score", "3000");

		defaults.setProperty("highscore.9.name", "DONATELLO");
		defaults.setProperty("highscore.9.score", "2000");

		defaults.setProperty("highscore.10.name", "ELVIS");
		defaults.setProperty("highscore.10.score", "1000");

		defaults.setProperty("input.wiimote", "false");


		properties = new Properties(defaults);

		try
		{
			properties.load(stream);
		}
		catch (IOException e)
		{
			throw new ConfigManagerException("Config file " + filename + " was opened, but data couldn't be loaded from file", e);
		}

		stream.close();

		readProperties(properties);
	}

	public void readProperties(Properties properties)
	{
		verifyProperties(properties);

		if (getProperty("options.fullscreen") == null)
		{
			fullscreen = false;
		}
		else
		{
			fullscreen = getProperty("options.fullscreen").equalsIgnoreCase("true");
		}
		if (getProperty("options.sound") == null)
		{
			sound = false;
		}
		else
		{
			sound = getProperty("options.sound").equalsIgnoreCase("true");
		}

		if (getProperty("options.music") == null)
		{
			music = false;
		}
		else
		{
			music = getProperty("options.music").equalsIgnoreCase("true");
		}

		if (getProperty("input.wiimote") == null)
		{
			wiimote = false;
		}
		else
		{
			wiimote = getProperty("input.wiimote").equalsIgnoreCase("true");
		}		

	}

	public void verifyProperties(Properties properties)
	{
		if (getProperty("options.fullscreen") == null)
		{
			System.out.println("Missing boolean property [options.fullscreen].");
		}
		else
		{
			System.out.println("Property [options.fullscreen] is set to " + getProperty("options.fullscreen"));
		}

		if (getProperty("options.sound") == null)
		{
			System.out.println("Missing boolean property [options.sound].");
		}
		else
		{
			System.out.println("Property [options.sound] is set to " + getProperty("options.sound"));
		}

		if (getProperty("options.music") == null)
		{
			System.out.println("Missing boolean property [options.music].");
		}
		else
		{
			System.out.println("Property [options.music] is set to " + getProperty("options.music"));
		}

		if (getProperty("input.wiimote") == null)
		{
			System.out.println("Missing boolean property [input.wiimote].");
		}
		else
		{
			System.out.println("Property [input.wiimote] is set to " + getProperty("input.wiimote"));
		}

	}

	public boolean keyInUse(int code)
	{
		String str = Integer.toString(code);
		return properties.containsValue(str);
	}

	public void reset(int value, String[] controllers)
	{
		String value_str = Integer.toString(value);
		for (int a = 0; a < controllers.length; a++)
		{
			if (getProperty("options." + controllers[a]).equals(value_str))
				setProperty("options." + controllers[a], 0);
		}
	}

	public Boolean getFullscreen()
	{
		return fullscreen;
	}

	public void setFullscreen(Boolean fullscreen)
	{
		this.fullscreen = fullscreen;
	}

	public void setSound(Boolean sound)
	{
		this.sound = sound;
	}

	public Boolean getSound()
	{
		return sound;
	}

	public void setMusic(Boolean music)
	{
		this.music = music;
	}

	public Boolean getMusic()
	{
		return music;
	}

	public Boolean getWiimote()
	{
		return wiimote;
	}

	public void setProperty(String key, Integer value)
	{
		properties.put(key, value.toString());
	}

	public void setProperty(String key, String value)
	{
		properties.put(key, value);
	}

	public String getProperty(String key)
	{
		return (String) properties.get(key);
	}
}
