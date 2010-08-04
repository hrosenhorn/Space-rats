package com.spacerats.sound;

import javax.sound.sampled.AudioInputStream;
import java.io.InputStream;

/**
 * @author Håkan Larsson
 */
public interface SoundManager extends Thread.UncaughtExceptionHandler
{
	void playMusic(String name);

	void stopMusic();

	void close();

	void join();

	void setPaused(boolean paused);

	boolean isPaused();

	Sound getSound(String filename);

	Sound getSound(InputStream is);

	Sound getSound(AudioInputStream audioStream);

	AudioInputStream getAudioInputStream(String filename);

	AudioInputStream getAudioInputStream(InputStream is);

	InputStream play(Sound sound);

	InputStream play(Sound sound, SoundFilter filter, boolean loop);

	InputStream play(InputStream is);

	InputStream play(InputStream is, SoundFilter filter);

	void playSFX(String name);
	void muteMusicStart(boolean state);
	void muteMusic(boolean state);
	void muteSFX(boolean state);
	
}
