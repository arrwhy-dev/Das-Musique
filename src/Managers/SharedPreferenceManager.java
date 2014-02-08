package Managers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceManager {

	private static final class Keys {
		public static final String NUM_TWEETS = "Num_Tweets";
		public static final String TWEET_TYPE = "Tweet_Type";
		public static final String CACHING_STATE = "Enable_Caching";
		public static final String DEFAULT_HASHTAG = "Default_Hashtag";
	}

	private static SharedPreferenceManager sSharedPreferencesManager;
	private SharedPreferences mSharedPreferences;
	private static List<String> favArtists;
	private static List<String> favGenres;
	private static List<String> favSongNames;

	private SharedPreferenceManager(Context context) {
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	public static SharedPreferenceManager getInstance() {
		if (sSharedPreferencesManager == null) {
			throw new IllegalStateException(
					"SharedPreferencesManager has not been initialized");

		}
		return sSharedPreferencesManager;
	}

	public static synchronized void init(Context context) {
		if (sSharedPreferencesManager == null) {
			sSharedPreferencesManager = new SharedPreferenceManager(context);
			favArtists = new ArrayList<String>();
			favGenres = new ArrayList<String>();
			favSongNames = new ArrayList<String>();
		}
	}

	public void setCachingState(boolean state) {
		mSharedPreferences.edit().putBoolean(Keys.CACHING_STATE, state)
				.commit();
	}

	public void putArtist(String artist) {
		favArtists.add(artist);
	}

	public void putGenre(String genre) {
		favGenres.add(genre);
	}

	public boolean getCachingState() {
		return mSharedPreferences.getBoolean(Keys.CACHING_STATE, false);
	}

	public String getTweetNum() {
		return mSharedPreferences.getString(Keys.NUM_TWEETS, "15");
	}

	public String getTweetType() {
		return mSharedPreferences.getString(Keys.TWEET_TYPE, "mixed");
	}

	public String getDefaultHashTag() {
		return mSharedPreferences.getString(Keys.DEFAULT_HASHTAG, "android");
	}

	public void putSongName(String trackName) {
		favArtists.add(trackName);

	}

}
