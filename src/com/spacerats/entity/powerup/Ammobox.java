package com.spacerats.entity.powerup;

import com.spacerats.GameContext;
import com.spacerats.cache.CacheManager;
import com.spacerats.graphics.Animation;
import com.spacerats.entity.Obstacle;
import com.spacerats.entity.Player;
import com.spacerats.weapon.LameShot;

import java.awt.*;

/**
 * @author Axel Andrén
 */
public class Ammobox extends AbstractPowerup
{
	private int ammoamount;

	public Ammobox()
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("ammobox"), 1000);
		boundingRadius = 20;
		ammoamount = 20;
	}
	public Ammobox(int ammoamount)
	{
		animation = new Animation();
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("ammobox"), 1000);
		boundingRadius = 20;
		this.ammoamount = ammoamount;
	}

	public void discardPowerup(GameContext gameContext)
	{
		gameContext.getEngine().removeObstacle(this);
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (otherObstacle instanceof Player)
		{
			Player player = (Player)otherObstacle;
			if (player.isAlive())
			{
				gameContext.getEngine().getSoundManager().playSFX("reload");
				if (player.getWeapon() instanceof LameShot && !(player.getOldWeapon() instanceof LameShot))
					player.setWeapon(player.getOldWeapon());
				player.getWeapon().setAmmocount(ammoamount, false);
				discardPowerup(gameContext);
			}
		}
	}

	public void draw(Graphics2D g, int x, int y)
	{
		drawStar(g,x,y,rotation+=0.05);
		g.drawImage(CacheManager.getInstance().getPreloadedImage("ammobox"),x,y+8,null);
	}
}
