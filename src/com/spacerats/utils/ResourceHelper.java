package com.spacerats.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * @author Håkan Larsson
 */
public class ResourceHelper
{
	public static Image loadImagePath (String fileName)
	{
		URL location = Thread.currentThread().getContextClassLoader().getResource(fileName);
		if (location == null)
		{
			return new ImageIcon(fileName).getImage();
		}

		return new ImageIcon(location).getImage();
	}
}
