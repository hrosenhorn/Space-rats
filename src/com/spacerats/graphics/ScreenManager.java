package com.spacerats.graphics;

import com.google.inject.ImplementedBy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Håkan Larsson
 */
@ImplementedBy (ScreenManagerImpl.class)
public interface ScreenManager
{
	DisplayMode[] getCompatibleDisplayModes();

	DisplayMode getCurrentDisplayMode();

	void setFullScreen(DisplayMode displayMode);

	void setWindowedMode();

	boolean isDrawable();

	Graphics2D getGraphics();

	void update();

	JFrame getFullScreenWindow();

	int getWidth();

	int getHeight();

	void restoreScreen();

	BufferedImage createCompatibleImage(int w, int h, int transparancy);

	DisplayMode findFirstCompatibleMode(DisplayMode modes[]);

	boolean displayModesMatch(DisplayMode mode1, DisplayMode mode2);
}
