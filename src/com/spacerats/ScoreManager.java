package com.spacerats;

import com.spacerats.config.ConfigManager;
import com.spacerats.cache.CacheManager;

import java.awt.*;

/**
 * @author Staffan Wennberg
 */
public class ScoreManager
{
	public static int score = 0;
	public static int current = 0;

	private static ConfigManager config = ConfigManager.getInstance();

	public static void update() {
		if (current < score) current += 5;
	}

	public static void addScore(int term) {
		score += term;
	}

	public static void reset() {
		score = 0;
		current = 0;
	}

	public static String getScorePrint() {
		update();
		return "Score: " + Integer.toString(current);
	}

	public static Image getScoreImage(int index) {
		update();
		String convertedScore = Integer.toString(current);
		while (convertedScore.length() < 7) {
			convertedScore = "0" + convertedScore;
		}
		String ret = "score_" + convertedScore.substring(index,index+1);
		return CacheManager.getInstance().getPreloadedImage(ret);
	}

	public static String[] getHighscores() {
		String[] highscores = new String[20];
		for (int a = 0; a < 10; a++) {
			highscores[a * 2] = config.getProperty("highscore." + Integer.toString(a + 1) + ".name");
			highscores[a * 2 + 1] = config.getProperty("highscore." + Integer.toString(a + 1) + ".score");
		}
		return highscores;
	}

	public static void resetHighscores() {
		config.setProperty("highscore.1.name","YODA");
		config.setProperty("highscore.1.score","10000");

		config.setProperty("highscore.2.name","BOB");
		config.setProperty("highscore.2.score","9000");

		config.setProperty("highscore.3.name","BILL");
		config.setProperty("highscore.3.score","8000");

		config.setProperty("highscore.4.name","TUDOR");
		config.setProperty("highscore.4.score","7000");

		config.setProperty("highscore.5.name","FRITZ");
		config.setProperty("highscore.5.score","6000");

		config.setProperty("highscore.6.name","FOZZIE");
		config.setProperty("highscore.6.score","5000");

		config.setProperty("highscore.7.name","ZAPHOD");
		config.setProperty("highscore.7.score","4000");

		config.setProperty("highscore.8.name","CLARK");
		config.setProperty("highscore.8.score","3000");

		config.setProperty("highscore.9.name","DONATELLO");
		config.setProperty("highscore.9.score","2000");

		config.setProperty("highscore.10.name","ELVIS");
		config.setProperty("highscore.10.score","1000");
	}

	public static boolean highscoreOrNot() {
		int scoreToBeat = Integer.parseInt(config.getProperty("highscore.10.score"));
		return score >= scoreToBeat;
	}

	public static int updateHighScores(String name) {
			String[] highscores = getHighscores();
			int ret = -1;
			// insert new highscore
			for (int a = 0; a < 10; a++) {
				if (score >= Integer.parseInt(highscores[a * 2 + 1])) {
					if (ret == -1) ret = a;
					int scoreTmp = score;
					String nameTmp = name;
					score = Integer.parseInt(highscores[a * 2 + 1]);
					name = highscores[a * 2];

					// Are these two lines really necessary??
					highscores[a * 2 + 1] = Integer.toString(scoreTmp);
					highscores[a * 2] = nameTmp;

					// save the new setting at the same time
					config.setProperty("highscore." + Integer.toString(a+1) + ".score",scoreTmp);
					config.setProperty("highscore." + Integer.toString(a+1) + ".name",nameTmp);
				}
			}
		return ret;
	}
}
