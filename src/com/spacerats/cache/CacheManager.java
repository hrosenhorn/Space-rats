package com.spacerats.cache;

import com.spacerats.utils.ResourceHelper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.awt.*;

/**
 * @author Håkan Larsson
 */
public class CacheManager
{
	private static CacheManager cacheManagerSingleton;

	private ConcurrentMap<CacheList, Cache> storage = new ConcurrentHashMap<CacheList, Cache>();

	private enum CacheList
	{
		SOUND,
		IMAGE;
	}

	private CacheManager()
	{
		//storage.put(CacheList.SOUND, new CacheImpl<String, ???>());
		purgeCache ();
	}

	public static synchronized CacheManager getInstance()
	{
		if (cacheManagerSingleton == null)
		{
			cacheManagerSingleton = new CacheManager();
		}
		return cacheManagerSingleton;
	}

	public void preloadImage(String name, Image image) throws CachePreloadException
	{
		if (image.getWidth(null) == -1 || image.getHeight(null) == -1)
		{
			throw new CachePreloadException("Unable to load non existing image ("+ name +"). Check your spelling");
		}

		storage.get(CacheList.IMAGE).addEntry(name, image);
	}

	public Image getPreloadedImage(String name)
	{
		Entry<Image> entry = storage.get(CacheList.IMAGE).getEntry(name);

		if (entry == null)
		{
			System.out.println("WARNING: preloaded image with name " + name + " was not found.");
			return null;
		}

		return entry.getData();
	}

	public void preloadContent() throws CachePreloadException
	{
		preloadImages();
	}

	private void preloadImages() throws CachePreloadException
	{
		// Preload player images
		preloadImage("player-small", ResourceHelper.loadImagePath("images/player-small.png"));
		preloadImage("player-small2", ResourceHelper.loadImagePath("images/player-small2.png"));

		// Power-ups, obstacles and other
		preloadImage("heart", ResourceHelper.loadImagePath("images/powerup/heart.png"));
		preloadImage("halfheart", ResourceHelper.loadImagePath("images/powerup/halfheart.png"));
		preloadImage("blast", ResourceHelper.loadImagePath("images/powerup/blast.png"));
		//preloadImage("bomb", ResourceHelper.loadImagePath("images/powerup/bomb2.png"));
		preloadImage("life", ResourceHelper.loadImagePath("images/life.png"));
		preloadImage("ammobox", ResourceHelper.loadImagePath("images/powerup/ammobox.png"));
		//preloadImage("1up", ResourceHelper.loadImagePath("images/powerup/1up.png"));
		preloadImage("lifestar", ResourceHelper.loadImagePath("images/lifestar.png"));
		preloadImage("exit1", ResourceHelper.loadImagePath("images/powerup/exit1.png"));
		preloadImage("exit2", ResourceHelper.loadImagePath("images/powerup/exit2.png"));

		// Sound effects
		preloadImage("smack", ResourceHelper.loadImagePath("images/soundfx/smack.png"));
		preloadImage("zap", ResourceHelper.loadImagePath("images/soundfx/zap.png"));
		preloadImage("dink", ResourceHelper.loadImagePath("images/soundfx/dink.png"));
		preloadImage("splat", ResourceHelper.loadImagePath("images/soundfx/splat.png"));
		preloadImage("boom", ResourceHelper.loadImagePath("images/soundfx/boom.png"));
		preloadImage("pow", ResourceHelper.loadImagePath("images/soundfx/pow.png"));
		preloadImage("schplork", ResourceHelper.loadImagePath("images/soundfx/schplork.png"));

		// Enemies images
		preloadImage("catSlayer1", ResourceHelper.loadImagePath("images/enemy/catSlayer/catSlayer1.png"));
		preloadImage("catSlayer2", ResourceHelper.loadImagePath("images/enemy/catSlayer/catSlayer2.png"));
		preloadImage("snakeRat1", ResourceHelper.loadImagePath("images/enemy/snakeRat/snakeRat_1.png"));
		preloadImage("snakeRat2", ResourceHelper.loadImagePath("images/enemy/snakeRat/snakeRat_2.png"));
		preloadImage("snakeRat3", ResourceHelper.loadImagePath("images/enemy/snakeRat/snakeRat_3.png"));
		preloadImage("snakeRat4", ResourceHelper.loadImagePath("images/enemy/snakeRat/snakeRat_4.png"));
		preloadImage("snakeRat5", ResourceHelper.loadImagePath("images/enemy/snakeRat/snakeRat_5.png"));
		preloadImage("snakeRat6", ResourceHelper.loadImagePath("images/enemy/snakeRat/snakeRat_6.png"));
		preloadImage("snakeRat7", ResourceHelper.loadImagePath("images/enemy/snakeRat/snakeRat_7.png"));

		preloadImage("propRat1", ResourceHelper.loadImagePath("images/enemy/propellerRat/propellerRat_1.png"));
		preloadImage("propRat2", ResourceHelper.loadImagePath("images/enemy/propellerRat/propellerRat_2.png"));
		preloadImage("propRat3", ResourceHelper.loadImagePath("images/enemy/propellerRat/propellerRat_3.png"));
		preloadImage("propRat4", ResourceHelper.loadImagePath("images/enemy/propellerRat/propellerRat_4.png"));
		preloadImage("propRat5", ResourceHelper.loadImagePath("images/enemy/propellerRat/propellerRat_5.png"));
		preloadImage("propRat6", ResourceHelper.loadImagePath("images/enemy/propellerRat/propellerRat_6.png"));
		preloadImage("propRat7", ResourceHelper.loadImagePath("images/enemy/propellerRat/propellerRat_7.png"));
		preloadImage("propRat8", ResourceHelper.loadImagePath("images/enemy/propellerRat/propellerRat_8.png"));

		preloadImage("boss1a", ResourceHelper.loadImagePath("images/enemy/boss/boss1a.png"));
		preloadImage("boss1b", ResourceHelper.loadImagePath("images/enemy/boss/boss1b.png"));

		preloadImage("boss2", ResourceHelper.loadImagePath("images/enemy/boss2/squidRat.png"));
		preloadImage("baby", ResourceHelper.loadImagePath("images/enemy/boss2/baby.png"));

		// The health bar
		//preloadImage("healthbar", ResourceHelper.loadImagePath("images/healthbar.png"));
		//preloadImage("healthpin", ResourceHelper.loadImagePath("images/healthpin.png"));

		// Menu graphics
		preloadImage("logo", ResourceHelper.loadImagePath("images/logo3.png"));
		preloadImage("arrow", ResourceHelper.loadImagePath("images/arrow1.gif"));
		preloadImage("cheese1", ResourceHelper.loadImagePath("images/small_cheese.png"));

		// Ammo graphics
		preloadImage("lameammo", ResourceHelper.loadImagePath("images/ammo/lameshot.png"));
		preloadImage("bullet", ResourceHelper.loadImagePath("images/ammo/bullet.png"));
		preloadImage("laserwave", ResourceHelper.loadImagePath("images/ammo/laserwave.png"));
		preloadImage("missile1", ResourceHelper.loadImagePath("images/ammo/missile1.png"));
		preloadImage("missile2", ResourceHelper.loadImagePath("images/ammo/missile2.png"));

		// Weapon graphics
		preloadImage("minigun", ResourceHelper.loadImagePath("images/weapons/minigun.png"));
		preloadImage("lasergun", ResourceHelper.loadImagePath("images/weapons/lasergun.png"));
		preloadImage("missilelauncher", ResourceHelper.loadImagePath("images/weapons/MissileLauncher.png"));
		preloadImage("lameshot", ResourceHelper.loadImagePath("images/weapons/slingshot.png"));

		// Explosion
		preloadImage("exp1", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_01.png"));
		preloadImage("exp2", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_02.png"));
		preloadImage("exp3", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_03.png"));
		preloadImage("exp4", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_04.png"));
		preloadImage("exp5", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_05.png"));
		preloadImage("exp6", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_06.png"));
		preloadImage("exp7", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_07.png"));
		preloadImage("exp8", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_08.png"));
		preloadImage("exp9", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_09.png"));
		preloadImage("exp10", ResourceHelper.loadImagePath("images/expl_anim/expl_anim_10.png"));

		// Score and other game info
		preloadImage("score_title", ResourceHelper.loadImagePath("images/score/score.png"));
		preloadImage("ammo_title", ResourceHelper.loadImagePath("images/score/ammo.png"));
		preloadImage("lives_title", ResourceHelper.loadImagePath("images/score/lives.png"));
		preloadImage("score_background", ResourceHelper.loadImagePath("images/score/background4.png"));
		preloadImage("score_0", ResourceHelper.loadImagePath("images/score/0.png"));
		preloadImage("score_1", ResourceHelper.loadImagePath("images/score/1.png"));
		preloadImage("score_2", ResourceHelper.loadImagePath("images/score/2.png"));
		preloadImage("score_3", ResourceHelper.loadImagePath("images/score/3.png"));
		preloadImage("score_4", ResourceHelper.loadImagePath("images/score/4.png"));
		preloadImage("score_5", ResourceHelper.loadImagePath("images/score/5.png"));
		preloadImage("score_6", ResourceHelper.loadImagePath("images/score/6.png"));
		preloadImage("score_7", ResourceHelper.loadImagePath("images/score/7.png"));
		preloadImage("score_8", ResourceHelper.loadImagePath("images/score/8.png"));
		preloadImage("score_9", ResourceHelper.loadImagePath("images/score/9.png"));
		preloadImage("infinite", ResourceHelper.loadImagePath("images/score/infinite.png"));
		preloadImage("level", ResourceHelper.loadImagePath("images/score/level.png"));
		preloadImage("level_bg", ResourceHelper.loadImagePath("images/score/level_bg.png"));
		preloadImage("dots", ResourceHelper.loadImagePath("images/score/dots.png"));

		// Other
		preloadImage("empty", ResourceHelper.loadImagePath("images/empty.png"));
		preloadImage("blackhole", ResourceHelper.loadImagePath("images/vortex2.png"));
		preloadImage("gameover", ResourceHelper.loadImagePath("images/gameover2.png"));
	}

	public void purgeCache ()
	{
		storage = new ConcurrentHashMap<CacheList, Cache>();
		storage.put(CacheList.IMAGE, new CacheImpl<String, Image>());
	}
}
