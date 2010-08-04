package com.spacerats.menu;

import com.spacerats.Engine;
import com.spacerats.cache.CacheManager;

import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Staffan Wennberg
 */

// TODO: Booleans for rotation & sinus
// TODO: Make SRMenu use the input manager?
// TODO: Create base cases for all settings in config manager

public class StarScroller
{
	private static StarScroller starscroller;
	private Engine engine;

	private int starlevels = 10;
	private int starcount = 15; // per level, that is!
	private int basicspeed = 5;
	private int speedinterval = 1;
	private ArrayList<ArrayList<Point>> stars = new ArrayList<ArrayList<Point>>();

	private ArrayList<SmallObject> planets = new ArrayList<SmallObject>();

	private final String[] planetImages = {
			"cheese1"
	};

	// Constructor
	private StarScroller(Engine engine) {
		this.engine = engine;
		for (int counter = 0; counter < starlevels; counter++) {
			stars.add(createHeaven());
		}
	}


	public static synchronized StarScroller getInstance(Engine engine) {
		if (starscroller == null) starscroller = new StarScroller(engine);
		return starscroller;
	}

	public static synchronized StarScroller getInstance() {
		return starscroller;
	}


	public ArrayList<ArrayList<Point>> updatedStars(float mode) {
		for (int a = 0; a < starlevels; a++) {
			for (int b = 0; b < stars.get(a).size(); b++) {
				Point point = stars.get(a).get(b);
				double new_x = point.getX() - ((basicspeed + a * speedinterval) * mode);
				if (new_x < 0) {
					stars.get(a).remove(b);
				}
				else
				{
					point.setLocation(new_x,point.getY());
					stars.get(a).set(b,point);
				}
				// Generate new stars...
				if (stars.get(a).size() < starcount && Math.random() < (0.002 * (a + 1)))
				{
						int x = engine.getWorldWidth();
						int y = (int)(Math.random()*10000 % engine.getWorldHeight());
						stars.get(a).add(new Point(x,y));
				}
			}
		}
		return stars;
	}

	public ArrayList<SmallObject> updatedPlanets() {

		if (Math.random() < 0.006) planets.add(
				new SmallObject(planetImages[(int)(Math.random() * 1000 % planetImages.length)]));

		for (int planet = 0; planet < planets.size(); planet++) {
			planets.get(planet).update();
			if (planets.get(planet).getX() < -200) planets.remove(planet);
		}

		return planets;
	}

	// Create all stars
	private ArrayList<Point> createHeaven() {
		ArrayList<Point> stars = new ArrayList<Point>();
		int x,y;
		for(int c = 0; c < (starcount); c++) {
			x = (int)(Math.random()*10000 % engine.getWorldWidth());
			y = (int)(Math.random()*10000 % engine.getWorldHeight());
			stars.add(new Point(x,y));
		}
		return stars;
	}


	// An inner class for the different planets and cheeses that traverses the sky
	public class SmallObject {

		private int move_type;
			// type 0: linear movement
			// type 1: sinus curve
			// type 2: rotating
		private int x;
		private int y;
		private int speed;
		private double scale;
		private double flip;
		private double rotation, rotationspeed;
		private Image image;
		private AffineTransform transform;

		private SmallObject(String image) {
			this.image = CacheManager.getInstance().getPreloadedImage(image);
			x = engine.getWorldWidth() + 200;
			y = (int)(Math.random() * 10000 % engine.getWorldHeight());

			move_type = (int)(Math.random() * 10000) % 3;

			speed = (int)(Math.random() * 100 % 8);

			while (scale < 0.4) scale = Math.random();

			switch(move_type) {
				case 0:
					if (Math.random() <= 0.5) flip = -1;
				case 1:

				case 2:
					rotation = Math.random() * 10000 % 360;
					while (rotationspeed < 0.05) rotationspeed = Math.random() * 10000 % 0.35;
				default:
			}
			if (speed == 0) speed = 1;

			transform = new AffineTransform();


		}

		public void update() {
			switch(move_type) {
				case 2:
					rotation += rotationspeed % 360;
				default:
					x = x - speed;
			}
		}

		public int getX() {
			return x;
		}

		public int getY() {
			switch (move_type) {
			case 1:
				return y + (int)(15*(Math.sin((float)x / (float)25)));
			default:
				return y;
			}
		}

		public Image getImage() {
			return image;
		}

		public AffineTransform getTransform() {
			transform.setToTranslation(getX(), getY());
			switch(move_type) {
				case 0:
					transform.scale(flip * scale,scale);
				case 1:
					transform.scale(scale,scale);
				case 2:
					transform.scale(scale,scale);
					transform.rotate(rotation);
				default:
			}
			return transform;
		}

	}

}
