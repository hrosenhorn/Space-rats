package com.spacerats.entity.powerup;

import com.spacerats.graphics.Sprite;
import com.spacerats.entity.Obstacle;
import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.cache.CacheManager;

import java.awt.geom.AffineTransform;
import java.awt.*;

/**
 * @author Håkan Larsson
 */
abstract public class AbstractPowerup extends Sprite implements Powerup, Obstacle
{
	double rotation = 0;

	public void discardPowerup (GameContext gameContext)
	{
		// The heart has been used, remove it from the map
		gameContext.getEngine().removeObstacle(this);
		gameContext.getEngine().getSoundManager().playSFX("powerup");
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		return false;
	}

	public void drawStar(Graphics2D g, int x, int y, double rotation) {
		AffineTransform transform = getTransform(x,y);
		transform.rotate(rotation,24,24);
		g.drawImage(CacheManager.getInstance().getPreloadedImage("lifestar"),transform,null);
	}

	public AffineTransform getTransform(int x, int y)
	{
		transform.setToTranslation(x,y);

		return transform;
	}	
}
