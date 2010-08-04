package com.spacerats.graphics;

import com.spacerats.cache.CacheManager;

/**
 * @author Staffan Wennberg
 */
public class PredefinedAnimations
{
	public static Animation explosion() {
		Animation animation = new Animation();

		long dur = 80;
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp4"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp3"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp2"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp1"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp2"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp3"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp4"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp5"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp6"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp7"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp8"), dur);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp9"), dur/2);
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("exp10"), dur/2);	
		animation.addFrame(CacheManager.getInstance().getPreloadedImage("empty"), 10000000);

		return animation;
	}

}
