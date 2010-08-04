package com.spacerats.entity;

import com.spacerats.graphics.Animation;
import com.spacerats.graphics.Sprite;
import com.spacerats.utils.ResourceHelper;
import com.spacerats.entity.Obstacle;
import com.spacerats.GameContext;
import com.spacerats.Health;
import com.spacerats.weapon.ammo.Ammo;

import java.awt.geom.AffineTransform;

/**
 * @author Håkan Larsson
 */
public class Planet extends Sprite implements Obstacle
{
	public Planet(String image)
	{
		// add the obstacle to the map
		animation = new Animation();

		int animSpeed = 50 + (int)(Math.random() * 10000) % 60;

		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_01.png"), animSpeed * 2);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_02.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_03.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_04.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_05.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_06.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_07.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_08.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_09.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_10.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_11.png"), animSpeed);
		animation.addFrame(ResourceHelper.loadImagePath("images/cheese_anim/ch_anim_12.png"), animSpeed);

		boundingRadius = animation.getImage().getHeight(null) / 2;
	}

	public void handleCollision(GameContext gameContext, Obstacle otherObstacle)
	{
		if (!(otherObstacle instanceof Ammo)) //Prevent bullets/lasers/etc from  bouncing off planets
			gameContext.getEngine().collideObstacles(this, otherObstacle);
		
		otherObstacle.delegateImpact(gameContext, 10);
	}

	public boolean delegateImpact(GameContext gameContext, int amount)
	{
		return true;
	}

	public AffineTransform getTransform(int x, int y)
	{
		transform.setToTranslation(x,y);

		return transform;
	}	
}
