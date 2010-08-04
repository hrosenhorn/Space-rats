package com.spacerats.sound;

import java.io.*;
import java.util.HashMap;
import javax.sound.sampled.*;

import com.spacerats.utils.*;
import libsidplay.SIDPlay2;
import libsidplay.components.sidtune.SidTune;
import libsidplay.common.ISID2Types;
import resid_builder.ReSIDBuilder;


/**
 * The SoundManager class manages sound playback. The
 * SoundManager is a ThreadPool, with each thread playing back
 * one sound at a time. This allows the SoundManager to
 * easily limit the number of simultaneous sounds being played.
 * <p>Possible ideas to extend this class:<ul>
 * <li>add a setMasterVolume() method, which uses Controls to
 * set the volume for each line.
 * <li>don't play a sound if more than, say, 500ms has passed
 * since the request to play
 * </ul>
 */
public class SoundManagerImpl extends ThreadPool implements SoundManager
{
	private AudioFormat playbackFormat;
	private ThreadLocal localLine;
	private ThreadLocal localBuffer;
	private Object pausedLock;
	private boolean paused;
	private SIDPlay2 m_engine;
	private MusicPlayer musicPlayer = null;
	private boolean mutemusic = false;
	private boolean mutesfx = false;
	private String currentMusic = "none";
	private String menuMusic = "sounds/glimrick_-_Emergency.sid";
	private String highscoreMusic = "sounds/glimrick_-_Know_Jack_Shit.sid";
	private boolean musicplaying = false;

	private HashMap<String, Sound> sfx = new HashMap<String, Sound>();

	public SoundManagerImpl()
	{
		this(new AudioFormat(44100, 16, 1, true, false));
		sfx.put("bump1", getSound("sounds/bump1.wav"));
		sfx.put("bump2", getSound("sounds/bump2.wav"));
		sfx.put("explode", getSound("sounds/explode.wav"));
		sfx.put("boop2", getSound("sounds/boop2.wav"));
		sfx.put("prize", getSound("sounds/prize.wav"));
		sfx.put("powerup", getSound("sounds/powerup.wav"));
		sfx.put("extralife", getSound("sounds/extralife.wav"));
		
		sfx.put("gun", getSound("sounds/weapons/gun.wav"));
		sfx.put("laser", getSound("sounds/weapons/laser.wav"));
		sfx.put("click", getSound("sounds/weapons/click.wav"));
		sfx.put("reload", getSound("sounds/weapons/reload.wav"));
		
		sfx.put("menu", getSound("sounds/menu/menu.wav"));
		sfx.put("input", getSound("sounds/menu/input.wav"));
		sfx.put("select", getSound("sounds/menu/select.wav"));
	}

	/**
	 * Creates a new SoundManager using the maximum number of
	 * simultaneous sounds.
	 */
	public SoundManagerImpl(AudioFormat playbackFormat)
	{
		this(playbackFormat, getMaxSimultaneousSounds(playbackFormat));
	}


	/**
	 * Creates a new SoundManager with the specified maximum
	 * number of simultaneous sounds.
	 */
	public SoundManagerImpl(AudioFormat playbackFormat, int maxSimultaneousSounds)
	{
		super(Math.min(maxSimultaneousSounds, getMaxSimultaneousSounds(playbackFormat)));
		this.playbackFormat = playbackFormat;
		localLine = new ThreadLocal();
		localBuffer = new ThreadLocal();
		pausedLock = new Object();
		// notify threads in pool it's ok to start
		synchronized (this)
		{
			notifyAll();
		}
	}

	public void playSFX(String name)
	{
		if (!mutesfx && sfx.containsKey(name)) play(sfx.get(name));
	}

	public synchronized void playMusic(String name)
	{
		if ( currentMusic == null ) return;
		currentMusic = null;

		stopMusic();

		currentMusic = name;

		if (name == "menu") name = menuMusic;		// hey just say menu and we got the music :)
		if (name == "highscore") name = highscoreMusic;		// hey just say highscore and we got the music for that too ;)
		if (name == "none") return;

		if (mutemusic) return;
		if (musicplaying) return;
		musicplaying = true;



		// get player and configuration
		m_engine = new SIDPlay2();
		ISID2Types.sid2_config_t m_engCfg = m_engine.config();

		// Setup the SID emulation (not part of the player)
		ReSIDBuilder rs = new ReSIDBuilder("resid");
		rs.create((m_engine.info()).maxsids);

		// configure the player with your wishes
		m_engCfg.sidEmulation = rs;
		m_engCfg.clockDefault = ISID2Types.sid2_clock_t.SID2_CLOCK_PAL;
		m_engCfg.clockSpeed = ISID2Types.sid2_clock_t.SID2_CLOCK_PAL;
		m_engCfg.sidModel = ISID2Types.sid2_model_t.SID2_MOS6581;
		m_engCfg.sidSamples = true;
		m_engCfg.frequency = (int) playbackFormat.getSampleRate();
		m_engCfg.precision = playbackFormat.getSampleSizeInBits();
		m_engine.config(m_engCfg);


		SidTune m_tune = new SidTune(name, null);
		m_tune.selectSong(1);

		m_engine.load(m_tune);
		musicPlayer = new MusicPlayer(m_engine);
		runTask(musicPlayer);
	}

	public synchronized void stopMusic()
	{
		if (musicPlayer != null) musicPlayer.stop();
		if (m_engine != null) m_engine.stop();

		musicPlayer = null;
		m_engine = null;
		musicplaying = false;
	}

	public void muteMusicStart(boolean state)
	{
		mutemusic = state;
	}

	public void muteMusic(boolean state)
	{
		mutemusic = state;
		if (state)
		{
			stopMusic();
		}
		else
		{
			playMusic(currentMusic);
		}
	}

	public void muteSFX(boolean state)
	{
		mutesfx = state;
	}

	/**
	 * Gets the maximum number of simultaneous sounds with the
	 * specified AudioFormat that the default mixer can play.
	 */
	public static int getMaxSimultaneousSounds(AudioFormat playbackFormat)
	{
		DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, playbackFormat);
		Mixer mixer = AudioSystem.getMixer(null);
		int maxLines = mixer.getMaxLines(lineInfo);
		if (maxLines == AudioSystem.NOT_SPECIFIED) maxLines = 32;
		return maxLines;
	}


	/**
	 * Does any clean up before closing.
	 */
	protected void cleanUp()
	{
		// signal to unpause
		setPaused(false);
		stopMusic();

		// close the mixer (stops any running sounds)
		Mixer mixer = AudioSystem.getMixer(null);
		if (mixer.isOpen()) mixer.close();
	}


	public void close()
	{
		cleanUp();
		super.close();
	}


	public void join()
	{
		cleanUp();
		super.join();
	}


	/**
	 * Sets the paused state. Sounds may not pause immediately.
	 */
	public void setPaused(boolean paused)
	{
		if (this.paused != paused)
		{
			synchronized (pausedLock)
			{
				this.paused = paused;
				if (!paused)
				{
					// restart sounds
					pausedLock.notifyAll();
				}
			}
		}
	}


	/**
	 * Returns the paused state.
	 */
	public boolean isPaused()
	{
		return paused;
	}


	/**
	 * Loads a Sound from the file system. Returns null if an
	 * error occurs.
	 */
	public Sound getSound(String filename)
	{
		return getSound(getAudioInputStream(filename));
	}


	/**
	 * Loads a Sound from an input stream. Returns null if an
	 * error occurs.
	 */
	public Sound getSound(InputStream is)
	{
		return getSound(getAudioInputStream(is));
	}


	/**
	 * Loads a Sound from an AudioInputStream.
	 */
	public Sound getSound(AudioInputStream audioStream)
	{
		if (audioStream == null) return null;

		// get the number of bytes to read
		int length = (int) (audioStream.getFrameLength() * audioStream.getFormat().getFrameSize());

		// read the entire stream
		byte[] samples = new byte[length];
		DataInputStream is = new DataInputStream(audioStream);
		try
		{
			is.readFully(samples);
			is.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		// return the samples
		return new Sound(samples);
	}


	/**
	 * Creates an AudioInputStream from a sound from the file
	 * system.
	 */
	public AudioInputStream getAudioInputStream(String filename)
	{
		try
		{
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);

			if (inputStream == null)
			{
				inputStream = new FileInputStream(filename);
			}

			return getAudioInputStream(inputStream);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}


	/**
	 * Creates an AudioInputStream from a sound from an input
	 * stream
	 */
	public AudioInputStream getAudioInputStream(InputStream is)
	{
		try
		{
			if (!is.markSupported()) is = new BufferedInputStream(is);
			// open the source stream
			AudioInputStream source = AudioSystem.getAudioInputStream(is);

			// convert to playback format
			return AudioSystem.getAudioInputStream(playbackFormat, source);
		}
		catch (UnsupportedAudioFileException ex)
		{
			ex.printStackTrace();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		catch (IllegalArgumentException ex)
		{
			ex.printStackTrace();
		}

		return null;
	}


	/**
	 * Plays a sound. This method returns immediately.
	 */
	public InputStream play(Sound sound)
	{
		return play(sound, null, false);
	}


	/**
	 * Plays a sound with an optional SoundFilter, and optionally
	 * looping. This method returns immediately.
	 */
	public InputStream play(Sound sound, SoundFilter filter, boolean loop)
	{
		InputStream is;
		if (sound != null)
		{
			if (loop) is = new LoopingByteInputStream(sound.getSamples());
			else is = new ByteArrayInputStream(sound.getSamples());
			return play(is, filter);
		}
		return null;
	}


	/**
	 * Plays a sound from an InputStream. This method
	 * returns immediately.
	 */
	public InputStream play(InputStream is)
	{
		return play(is, null);
	}


	/**
	 * Plays a sound from an InputStream with an optional
	 * sound filter. This method returns immediately.
	 */
	public InputStream play(InputStream is, SoundFilter filter)
	{
		if (is != null)
		{
			if (filter != null) is = new FilteredSoundStream(is, filter);
			runTask(new SoundPlayer(is));
		}
		return is;
	}


	/**
	 * Signals that a PooledThread has started. Creates the
	 * Thread's line and buffer.
	 */
	protected void threadStarted()
	{
		// wait for the SoundManager constructor to finish
		synchronized (this)
		{
			try
			{
				wait();
			}
			catch (InterruptedException ex)
			{
			}
		}

		// use a short, 100ms (1/10th sec) buffer for filters that
		// change in real-time
		int bufferSize = playbackFormat.getFrameSize() * Math.round(playbackFormat.getSampleRate() / 10);

		// create, open, and start the line
		SourceDataLine line;
		DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, playbackFormat);
		try
		{
			line = (SourceDataLine) AudioSystem.getLine(lineInfo);
			line.open(playbackFormat, bufferSize);
		}
		catch (LineUnavailableException ex)
		{
			// the line is unavailable - signal to end this thread
			Thread.currentThread().interrupt();
			return;
		}

		line.start();

		// create the buffer
		byte[] buffer = new byte[bufferSize];

		// set this thread's locals
		localLine.set(line);
		localBuffer.set(buffer);
	}


	/**
	 * Signals that a PooledThread has stopped. Drains and
	 * closes the Thread's Line.
	 */
	protected void threadStopped()
	{
		SourceDataLine line = (SourceDataLine) localLine.get();
		if (line != null)
		{
			line.drain();
			line.close();
		}
	}


	/**
	 * The SoundPlayer class is a task for the PooledThreads to
	 * run. It receives the threads's Line and byte buffer from
	 * the ThreadLocal variables and plays a sound from an
	 * InputStream.
	 * <p>This class only works when called from a PooledThread.
	 */
	protected class SoundPlayer implements Runnable
	{
		private InputStream source;

		public SoundPlayer(InputStream source)
		{
			this.source = source;
		}

		public void run()
		{
			// get line and buffer from ThreadLocals
			SourceDataLine line = (SourceDataLine) localLine.get();
			byte[] buffer = (byte[]) localBuffer.get();
			if (line == null || buffer == null) return;

			// copy data to the line
			try
			{
				int numBytesRead = 0;
				while (numBytesRead != -1)
				{
					// if paused, wait until unpaused
					synchronized (pausedLock)
					{
						if (paused)
						{
							try
							{
								pausedLock.wait();
							}
							catch (InterruptedException ex)
							{
								return;
							}
						}
					}
					// copy data
					numBytesRead = source.read(buffer, 0, buffer.length);
					if (numBytesRead != -1) line.write(buffer, 0, numBytesRead);
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}

		}
	}

	protected class MusicPlayer implements Runnable
	{
		private SIDPlay2 m_engine;
		private boolean running;

		public MusicPlayer(SIDPlay2 m_engine)
		{
			this.m_engine = m_engine;
		}

		public void run()
		{
			running = true;
			// get line and buffer from ThreadLocals
			SourceDataLine line = (SourceDataLine) localLine.get();
			byte[] soundBuffer = (byte[]) localBuffer.get();
			if (line == null || soundBuffer == null) return;
			short[] buffer = new short[soundBuffer.length];

			// copy data to the line
			while (running)
			{
				// if paused, wait until unpaused
				synchronized (pausedLock)
				{
					if (paused)
					{
						try
						{
							pausedLock.wait();
						}
						catch (InterruptedException ex)
						{
							return;
						}
					}
				}
				// Fill buffer
				long ret = m_engine.play(buffer, soundBuffer.length);
				for (int i = 0; i < buffer.length; i++) soundBuffer[i] = (byte) (buffer[i]);
				if (ret < soundBuffer.length) return;

				line.write(soundBuffer, 0, soundBuffer.length);
				buffer = new short[soundBuffer.length];

				try
				{
					Thread.sleep(20);
				}
				catch (InterruptedException e)
				{
					running = false;
				}
			}
		}

		public void stop()
		{
			running = false;
			m_engine.stop();
		}
	}

}
