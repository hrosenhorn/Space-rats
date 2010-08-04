package com.spacerats.menu;

import com.spacerats.config.ConfigManager;
import com.spacerats.Engine;
import com.spacerats.ScoreManager;
import com.spacerats.GameEngine;
import com.spacerats.cache.CacheManager;

import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * @author Staffan Wennberg
 */
public class SRMenu implements KeyListener {
    ConfigManager config;
    private Engine engine;
	private GameEngine gameE;
	
	private int main_selection = 0, opt_selection = 0, cont_selection = 0, hs_selection = 1;
	private boolean setting = false;

	private String state;

	private String[] controllers = {"right","left","up","down","fire","pause","mute","exit"};

	// general menu parameters
	private int middle_x, arrow_width, main_vert_spacing = 50; 

	// main menu parameters
	private int logo_x, logo_bottom, main_topmargin = 80;
	
	// options menu parameters
	int opt_topmargin = 280, opt_vert_spacing = 40, opt_left_right_spacing = 200, opt_left = 70;

	// controls menu parameters
	int cont_topmargin = 280, cont_vert_spacing = 35, cont_left_right_spacing = 150, cont_left = 70;

	// highscore menu parameters
	int hs_vert_spacing = 30; int caret_counter = 0; int highlight = -1;
	int hs_topmargin = 580; int hs_menu_vert_spacing = 40;
	String input = "";

	private StarScroller starscroller;
	private LightBulbs bulbs;

	
	// Constructor
	public SRMenu(Engine engine, String state) {
        this.engine = engine;
		gameE = (GameEngine)engine;
		config = ConfigManager.getInstance();
		this.state = state;

		middle_x = engine.getWorldWidth() / 2;
		logo_x = (middle_x - (CacheManager.getInstance().getPreloadedImage("logo").getWidth(null) / 2)) + 50;
		logo_bottom = CacheManager.getInstance().getPreloadedImage("logo").getHeight(null) + main_topmargin;
		arrow_width = CacheManager.getInstance().getPreloadedImage("arrow").getWidth(null);

		starscroller = StarScroller.getInstance(engine);
		bulbs = new LightBulbs(logo_x, main_topmargin);
	}
	
	// The forever loop
	public String run() {
		// remain in loop as long as state = main, options, credits or controls
		while(!state.equals("quit") && !state.equals("play") && !state.equals("restart")) {
			Graphics2D g = engine.getDrawingSurface();
			draw(g);
			g.dispose();
			engine.updateScreen();
		}
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	// Some keyboard methods
	public void keyPressed(KeyEvent e) { 
		if (state.equals("main")) {
			int extra = 0;
			if (!gameE.gameover()) extra++;
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				playSfx("nav");
				main_selection = (main_selection + 4 + extra) % (5 + extra);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				playSfx("nav");
				main_selection = (main_selection + 1) % (5 + extra); 
			}
			else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				playSfx("select");
				if (main_selection == 0) state = "play";
				else if (main_selection == 1 + extra) state = "highscore";
				else if (main_selection == 2 + extra) state = "options";
				else if (main_selection == 3 + extra) state = "credits";
				else if (main_selection == 4 + extra) state = "quit";
				else state = "restart";
			}
		}

		else if (state.equals("highscore")) {
			if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				playSfx("nav");
				hs_selection = (hs_selection + 1) % 2;
			}
			else if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				playSfx("select");
				if (hs_selection == 0)
				{
					ScoreManager.resetHighscores();
					config.save();
				}
				else if (hs_selection == 1)
				{
					state = "main";
					highlight = -1;
				}
			}
		}

		else if (state.equals("highscoreenter")) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (input.equals("") || input == null) {
					// error message
				}
				else
				{
					playSfx("select");
					highlight = ScoreManager.updateHighScores(input);
					config.save();
					ScoreManager.reset(); // To avoid getting into the same menu again
					state = "highscore";
				}
			}
			else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (input.length() > 0) input = input.substring(0,input.length()-1);
				playSfx("input");
			}
			else
			{
				if (e.getKeyCode() != KeyEvent.VK_SHIFT) {
					playSfx("input");
					if (input.length() < 21) input = input +
							Character.toString(Character.toUpperCase(e.getKeyChar()));
			}
				e.consume();
			}
		}

		else if (state.equals("options")) {
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				playSfx("nav");
				opt_selection = (opt_selection + 4) % 5;
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				playSfx("nav");
				opt_selection = (opt_selection + 1) % 5;
			}
			else if (e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				if (opt_selection == 0)
				{
					config.setSound(!config.getSound());
					engine.getSoundManager().muteSFX(!config.getSound());
				}
				else if (opt_selection == 1)
				{
					config.setMusic(!config.getMusic());
					engine.getSoundManager().muteMusic(!config.getMusic());
				}
				else if (opt_selection == 2)
				{
					state = "controls";
					cont_selection = 0;
				}
				else if (opt_selection == 3) {
					config.setFullscreen(!config.getFullscreen());
				}
				else if (opt_selection == 4) {
					config.save();
					state = "main";
				}
				playSfx("select");
			
			}	
		}

		else if (state.equals("controls")) {

			if (setting)
			{
				if (config.keyInUse(e.getKeyCode())) config.reset(e.getKeyCode(),controllers);
				config.setProperty("options." + controllers[cont_selection],e.getKeyCode());
				setting = false;
				playSfx("input");
				gameE.resetUserKeyMaps();
			}
			else
			{
				if (e.getKeyCode() == KeyEvent.VK_UP)
				{
					playSfx("nav");
					cont_selection = (cont_selection + 8) % 9;
				}
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					playSfx("nav");
					cont_selection = (cont_selection + 1) % 9;
				}
				else if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (cont_selection == 8)
					{
						playSfx("select");
						state = "options";
						config.save();
					}
					else
					{
						playSfx("select");
						setting = true;
						config.setProperty("options." + controllers[cont_selection],0);
					}
				}
			}
		}
		
		else if (state.equals("credits")) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				playSfx("select");
				state = "main";
			}
		}
	}

	public void keyReleased(KeyEvent e) { /* Do nothing */ }

    public void keyTyped(KeyEvent e) { /* Do nothing */ }
    
 // Graphics
	private void draw(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	 	// Background
		g.setColor(Color.BLACK);
		g.fillRect(0,0,engine.getWorldWidth(),engine.getWorldHeight());

		// Starscrolling
		g.setColor(Color.WHITE);
		ArrayList<ArrayList<Point>> stars = starscroller.updatedStars(0.8f);
	 	for (ArrayList<Point> star : stars)
	 	{
		 	for (Point aStar : star)
		 	{
				g.fillOval((int) aStar.getX(), (int) aStar.getY(), 1, 1);
		 	}
	 	}

		 // Possible planets and other space debris
		ArrayList<StarScroller.SmallObject> planets = starscroller.updatedPlanets();
	 	for (StarScroller.SmallObject so : planets)
	 	{
			g.drawImage(so.getImage(), so.getTransform(), null);
	 	}

		g.setColor(Color.YELLOW);

		if (state.equals("main")) {
			// Logo and bulbs
			g.drawImage(CacheManager.getInstance().getPreloadedImage("logo"), logo_x, main_topmargin, null);

			// Lights
			int[] scheme = bulbs.getLightScheme();

			for (int bulb = 0; bulb < scheme.length; bulb++)
			{
				g.setColor(Color.DARK_GRAY);
				g.fillOval(bulbs.getX(bulb)-2,bulbs.getY(bulb)+2,bulbs.getBulbSize(),bulbs.getBulbSize());
				g.setColor(bulbs.getColor(scheme[bulb]));
				g.fillOval(bulbs.getX(bulb),bulbs.getY(bulb),bulbs.getBulbSize(),bulbs.getBulbSize());
				g.setColor(new Color(30,30,30));
				g.drawOval(bulbs.getX(bulb),bulbs.getY(bulb),bulbs.getBulbSize(),bulbs.getBulbSize());
				g.setColor(Color.WHITE);
				g.fillOval(bulbs.getX(bulb)+4,bulbs.getY(bulb)+5,4,4);
			}

			// Menu
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Century Gothic",Font.BOLD,36));
			int extra = 0;
			if (gameE.gameover()) g.drawString("Start", middle_x, logo_bottom + 50);
			else
			{
				g.drawString("Resume", middle_x, logo_bottom + 50);
				g.drawString("Restart", middle_x, logo_bottom + 50 + main_vert_spacing);
				extra = 1;
			}

			g.drawString("Highscore", middle_x, logo_bottom + 50 + (1 + extra) * main_vert_spacing);
			g.drawString("Options", middle_x, logo_bottom + 50 + (2 + extra) * main_vert_spacing);
			g.drawString("Credits", middle_x, logo_bottom + 50 + (3 + extra) * main_vert_spacing);
			g.drawString("Quit", middle_x, logo_bottom + 50 + (4 + extra) * main_vert_spacing);
		
			g.drawImage(CacheManager.getInstance().getPreloadedImage("arrow"),
					middle_x - arrow_width - 20, getArrowVertical(), null);
		}

		else if (state.equals("highscore")) {
			// Highscore title
			g.drawLine(middle_x-210, 190, middle_x+210, 190);
			g.setFont(new Font("Century Gothic",Font.BOLD,27));
			g.drawString("S    P    A    C    E      R    A    T    S", middle_x-206, 122);
			g.setFont(new Font("Century Gothic",Font.BOLD,68));
			g.drawString("HIGHSCORES", middle_x - 210,179);
			g.setFont(new Font("Century Gothic", Font.PLAIN, 24));

			String[] highscores = ScoreManager.getHighscores();

			caret_counter++;
			if (caret_counter > 20) caret_counter = 0;

			for (int a = 0; a < 10; a++) {
				if (caret_counter > 10 && a == highlight) g.setColor(Color.WHITE);
				else g.setColor(Color.YELLOW);
				g.drawString(Integer.toString(a + 1) + ".",middle_x - 200, 230 + a * hs_vert_spacing);
				g.drawString(highscores[a * 2],middle_x - 140, 230 + a * hs_vert_spacing);
				g.drawString(highscores[a * 2 + 1], middle_x + 140, 230 + a * hs_vert_spacing);
			}

			g.setColor(Color.YELLOW);
			g.setFont(new Font("Century Gothic", Font.BOLD, 30));
			g.drawString("Reset",middle_x - 50,hs_topmargin);
			g.drawString("Exit highscores",middle_x - 50,hs_topmargin + hs_menu_vert_spacing);
			g.drawImage(CacheManager.getInstance().getPreloadedImage("arrow"),
					middle_x - arrow_width - 70, getArrowVertical(), null);
		}

		else if (state.equals("highscoreenter")) {
			g.drawLine(middle_x-210, 190, middle_x+210, 190);
			g.setFont(new Font("Century Gothic",Font.BOLD,27));
			g.drawString("S    P    A    C    E      R    A    T    S", middle_x-206, 122);
			g.setFont(new Font("Century Gothic",Font.BOLD,68));
			g.drawString("HIGHSCORES", middle_x - 210,179);

			g.setFont(new Font("Century Gothic",Font.PLAIN,24));
			g.drawString("Well done! You made it into the",middle_x - 190,230);
			g.drawString("hall of fame.",middle_x - 190, 260);
			g.drawString("Please enter your name:",middle_x - 145, 330);

			g.setColor(new Color(20,20,20));
			g.fillRect(middle_x - 150, 340, 300, 30);
			g.setColor(Color.YELLOW);
			g.drawRect(middle_x - 150, 340, 300, 30);

			g.setFont(new Font("Courier", Font.PLAIN, 15));

			for (int a = 0; a < input.length(); a++) {
				g.drawString(input.substring(a,a+1),middle_x - 140 + a * 13, 361);
			}

			caret_counter++;
			if (caret_counter > 10) g.drawLine(middle_x - 140 + (input.length() * 13),
					363, middle_x - 130 + (input.length() * 13), 363);
			if (caret_counter > 20) caret_counter = 0;
		}
		
		else if (state.equals("options")) {
			// Options title
			g.drawLine(middle_x-210, 210, middle_x+210, 210);
			g.setFont(new Font("Century Gothic",Font.BOLD,27));
			g.drawString("S    P    A    C    E      R    A    T    S", middle_x-206, 122);
			g.setFont(new Font("Century Gothic",Font.BOLD,100));
			g.drawString("OPTIONS", middle_x-210, 200);

			// Menu options
			g.setFont(new Font("Century Gothic",Font.BOLD,30));
			g.drawString("Sound fx", middle_x - opt_left, opt_topmargin);
			g.drawString(convertBoolean(config.getSound()),
					middle_x - opt_left + opt_left_right_spacing, opt_topmargin);
			
			g.drawString("Music", middle_x - opt_left, opt_topmargin + opt_vert_spacing);
			g.drawString(convertBoolean(config.getMusic()),
					middle_x - opt_left + opt_left_right_spacing, opt_topmargin + opt_vert_spacing);
			g.drawString("Controls", middle_x - opt_left, opt_topmargin + 2 * opt_vert_spacing);
			g.drawString("Fullscreen", middle_x - opt_left, opt_topmargin + 3 * opt_vert_spacing);
			g.drawString(convertBoolean(config.getFullscreen()),
					middle_x + opt_left_right_spacing - opt_left, opt_topmargin + 3 * opt_vert_spacing);
			
			g.drawString("Exit options", middle_x - opt_left, opt_topmargin + 4 * opt_vert_spacing);
			g.drawImage(CacheManager.getInstance().getPreloadedImage("arrow"),
					middle_x - arrow_width - 20 - opt_left, getArrowVertical(), null);
			
		}

		else if (state.equals("controls")) {
			// Options title

			g.drawLine(middle_x-210, 210, middle_x+210, 210);
			g.setFont(new Font("Century Gothic",Font.BOLD,27));
			g.drawString("S    P    A    C    E      R    A    T    S", middle_x-206, 122);
			g.setFont(new Font("Century Gothic",Font.BOLD,82));
			g.drawString("CONTROLS", middle_x-210, 200);

			g.setFont(new Font("Century Gothic", Font.PLAIN, 36));
			g.drawString("Right:",middle_x - cont_left, cont_topmargin);
			g.drawString("Left:",middle_x - cont_left, cont_topmargin + cont_vert_spacing);
			g.drawString("Up:",middle_x - cont_left, cont_topmargin + 2 * cont_vert_spacing);
			g.drawString("Down:",middle_x - cont_left, cont_topmargin + 3 * cont_vert_spacing);
			g.drawString("Fire:",middle_x - cont_left, cont_topmargin + 4 * cont_vert_spacing);
			g.drawString("Pause:",middle_x - cont_left, cont_topmargin + 6 * cont_vert_spacing);
			g.drawString("Mute:",middle_x - cont_left, cont_topmargin + 7 * cont_vert_spacing);
			g.drawString("Exit:",middle_x - cont_left, cont_topmargin + 8 * cont_vert_spacing);
			g.drawString("Exit controls",middle_x - cont_left, cont_topmargin + 10 * cont_vert_spacing);

			String[] strings = new String[8];
			for (int counter = 0; counter < 8; counter++) {
				if (setting && cont_selection == counter) strings[counter] = "<push button to use>";
				else {
					String prop = "options." + controllers[counter];
					int keyCode = Integer.parseInt(config.getProperty(prop));
					if (keyCode == 0) strings[counter] = "<not assigned>";
					else strings[counter] = KeyEvent.getKeyText(keyCode);
				}
			}

			g.setFont(new Font("Century Gothic",Font.BOLD,24));

			g.drawString(strings[0],middle_x - cont_left + cont_left_right_spacing, cont_topmargin);
			g.drawString(strings[1],middle_x - cont_left + cont_left_right_spacing, cont_topmargin + cont_vert_spacing);
			g.drawString(strings[2],middle_x - cont_left + cont_left_right_spacing, cont_topmargin + 2 * cont_vert_spacing);
			g.drawString(strings[3],middle_x - cont_left + cont_left_right_spacing, cont_topmargin + 3 * cont_vert_spacing);
			g.drawString(strings[4],middle_x - cont_left + cont_left_right_spacing, cont_topmargin + 4 * cont_vert_spacing);
			g.drawString(strings[5],middle_x - cont_left + cont_left_right_spacing, cont_topmargin + 6 * cont_vert_spacing);
			g.drawString(strings[6],middle_x - cont_left + cont_left_right_spacing, cont_topmargin + 7 * cont_vert_spacing);
			g.drawString(strings[7],middle_x - cont_left + cont_left_right_spacing, cont_topmargin + 8 * cont_vert_spacing);			

			g.drawImage(CacheManager.getInstance().getPreloadedImage("arrow"),
					middle_x - arrow_width - 20 - cont_left, getArrowVertical(), null);
		}
		
		else if (state.equals("credits")) {
			g.setFont(new Font("Century Gothic",Font.BOLD,120));
			g.drawString("CREDITS",middle_x - 240,200);
			g.setFont(new Font("Centuru Gothic",Font.PLAIN, 24));
			g.drawString("Space Rats was created by",middle_x - 220,250);
			g.drawString("    Axel Andrén",middle_x - 220,290);
			g.drawString("    Håkan Larsson",middle_x - 220,320);
			g.drawString("    Michael Wahlnäs",middle_x - 220,350);
			g.drawString("    Staffan Wennberg",middle_x - 220,380);
			g.drawString("Hell yeah!",middle_x - 220,440);
		}
	 try
	 {
		 Thread.sleep(20);
	 }
	 catch (InterruptedException e)
	 {
		 e.printStackTrace();
	 }
 }
	
    // Calculates the y position of the arrow
	private int getArrowVertical() { 
		if (state.equals("main")) return logo_bottom + 50 - 45 + main_selection * main_vert_spacing;
		else if (state.equals("options")) return opt_topmargin + (opt_selection - 1) * opt_vert_spacing;
		else if (state.equals("controls"))
		{ // 9 alternativ
			int result = cont_topmargin  - 8 + (cont_selection - 1) * cont_vert_spacing;
			if (cont_selection > 4) result += cont_vert_spacing;
			if (cont_selection > 7) result += cont_vert_spacing;
			return result;
		}
		else if (state.equals("highscore")) return hs_topmargin - 40 + hs_selection * hs_menu_vert_spacing;
		else return 0;
	}
	
	private String convertBoolean(boolean value) {
		if (value) return "ON";
		else return "OFF";
	}

	public String getState()
	{
		return state;
	}

	private void playSfx(String what)
	{
		if (what.equals("nav")) gameE.getSoundManager().playSFX("menu");
		else if (what.equals("select")) gameE.getSoundManager().playSFX("select");
		else if (what.equals("input")) gameE.getSoundManager().playSFX("input");
	}
}
