package com.uwaterloo.dasmusique;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import Models.Track;
import Tasks.GetAlbumArtTask;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdio.android.api.OAuth1WebViewActivity;
import com.rdio.android.api.Rdio;
import com.rdio.android.api.RdioApiCallback;
import com.rdio.android.api.RdioListener;
import com.rdio.android.api.services.RdioAuthorisationException;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity implements RdioListener {

	private MediaPlayer player;
	private Queue<Track> trackQueue;
	private static Rdio rdio;
	private static String TAG = "YO SOMETHING WENT WRONG";
	private static final String appKey = "x9jpn8knsqqrxm3tskp9gdfr";
	private static final String appSecret = "F5Z6Qvu9Ea";
	private static String accessToken = null;
	private static String accessTokenSecret = null;
	private static final String PREF_ACCESSTOKEN = "prefs.accesstoken";
	private static final String PREF_ACCESSTOKENSECRET = "prefs.accesstokensecret";

	private static String collectionKey = null;

	private ImageView albumArt;
	private ImageView playPause;
	private TextView mSongName;

	private ImageView mgreenThumb;
	private ImageView mredThumb;

	private DialogFragment getUserDialog;
	private DialogFragment getCollectionDialog;

	// Our model for the metadata for a track that we care about
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mSongName = (TextView) findViewById(R.id.songTitle);
		mgreenThumb = (ImageView) findViewById(R.id.greenthumbview);
		mredThumb = (ImageView) findViewById(R.id.redthumbview);
		playPause = (ImageView) findViewById(R.id.playPause);
		albumArt = (ImageView) findViewById(R.id.albumArt);
		mgreenThumb.setOnClickListener(new GenericClickListener());
		mredThumb.setOnClickListener(new GenericClickListener());
		playPause.setOnClickListener(new GenericClickListener());
		albumArt.setOnClickListener(new GenericClickListener());

		trackQueue = new LinkedList<Track>();

		if (rdio == null) {
			SharedPreferences settings = getPreferences(MODE_PRIVATE);
			accessToken = settings.getString(PREF_ACCESSTOKEN, null);
			accessTokenSecret = settings
					.getString(PREF_ACCESSTOKENSECRET, null);

			rdio = new Rdio(appKey, appSecret, accessToken, accessTokenSecret,
					this, this);

			if (accessToken == null || accessTokenSecret == null) {
				// If either one is null, reset both of them
				accessToken = accessTokenSecret = null;
				Intent myIntent = new Intent(MainActivity.this,
						OAuth1WebViewActivity.class);
				myIntent.putExtra(OAuth1WebViewActivity.EXTRA_CONSUMER_KEY,
						appKey);
				myIntent.putExtra(OAuth1WebViewActivity.EXTRA_CONSUMER_SECRET,
						appSecret);
				MainActivity.this.startActivityForResult(myIntent, 1);

			} else {
				rdio.prepareForPlayback();
			}

		}

	}

	@Override
	public void onDestroy() {
		rdio.cleanup();
		if (player != null) {
			player.reset();
			player.release();
			player = null;
		}
		super.onDestroy();
	}

	/**
	 * Get the current user, and load their collection to start playback with.
	 * Requires authorization and the Rdio app to be installed.
	 */
	private void doSomething() {
		if (accessToken == null || accessTokenSecret == null) {
			Toast.makeText(MainActivity.this, "Can't find the app!",
					Toast.LENGTH_LONG).show();
			return;
		}

		showGetUserDialog();

		List<NameValuePair> args = new LinkedList<NameValuePair>();
		args.add(new BasicNameValuePair(
				"extras",
				"followingCount,followerCount,username,displayName,subscriptionType,trialEndDate,actualSubscriptionType"));
		rdio.apiCall("currentUser", args, new RdioApiCallback() {
			@Override
			public void onApiSuccess(JSONObject result) {
				dismissGetUserDialog();
				try {
					result = result.getJSONObject("result");
					Log.i("THIS IS THE COLLECTION KEY", result.toString(2));

					collectionKey = result.getString("key").replace('s', 'c');

					LoadMoreTracks();
				} catch (Exception e) {
					Log.e(TAG, "Failed to handle JSONObject: ", e);
				}
			}

			@Override
			public void onApiFailure(String methodName, Exception e) {
				dismissGetUserDialog();
				if (e instanceof RdioAuthorisationException) {
					Toast.makeText(MainActivity.this, "Don't have the app bro",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void LoadMoreTracks() {
		if (accessToken == null || accessTokenSecret == null) {

			Toast.makeText(this, getString(R.string.no_more_tracks),
					Toast.LENGTH_LONG).show();

			Intent installRdioIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://search?q=pname:com.rdio.android.ui"));
			installRdioIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(installRdioIntent);

			finish();
			return;
		}

		showGetCollectionDialog();
		List<NameValuePair> args = new LinkedList<NameValuePair>();
		args.add(new BasicNameValuePair("keys", collectionKey));
		args.add(new BasicNameValuePair("count", "50"));
		rdio.apiCall("get", args, new RdioApiCallback() {
			@Override
			public void onApiFailure(String methodName, Exception e) {
				dismissGetCollectionDialog();
				Log.e("API CALL HAS FAILED", methodName + " failed: ", e);
			}

			@Override
			public void onApiSuccess(JSONObject result) {
				try {
					result = result.getJSONObject("result");
					result = result.getJSONObject(collectionKey);

					List<Track> trackKeys = new LinkedList<Track>();
					JSONArray tracks = result.getJSONArray("tracks");

					for (int i = 0; i < tracks.length(); i++) {
						JSONObject trackObject = tracks.getJSONObject(i);
						String key = trackObject.getString("key");
						String name = trackObject.getString("name");
						String artist = trackObject.getString("artist");
						String album = trackObject.getString("album");
						String albumArt = trackObject.getString("icon");
						Log.d(TAG,
								"Found track: " + key + " => "
										+ trackObject.getString("name"));
						trackKeys.add(new Track(key, name, artist, album,
								albumArt));
					}
					if (trackKeys.size() > 1)
						trackQueue.addAll(trackKeys);
					dismissGetCollectionDialog();

					next(true);

				} catch (Exception e) {
					dismissGetCollectionDialog();
					Log.e(TAG, "Failed to handle JSONObject: ", e);
				}
			}
		});
	}

	private void next(final boolean manualPlay) {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}

		final Track track = trackQueue.poll();

		if (trackQueue.size() < 3) {
			Log.i(TAG, "Track queue depleted, loading more tracks");
			LoadMoreTracks();
		}

		if (track == null) {
			Log.e(TAG, "Track is null!  Size of queue: " + trackQueue.size());
			return;
		}

		// Load the next track in the background and prep the player (to start
		// buffering)
		// Do this in a bkg thread so it doesn't block the main thread in
		// .prepare()
		AsyncTask<Track, Void, Track> task = new AsyncTask<Track, Void, Track>() {
			@Override
			protected Track doInBackground(Track... params) {
				Track track = params[0];
				try {
					player = rdio
							.getPlayerForTrack(track.key, null, manualPlay);
					player.prepare();
					player.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							next(false);
						}
					});
					player.start();
				} catch (Exception e) {
					Log.e("Test", "Exception " + e);
				}
				return track;
			}

			@Override
			protected void onPostExecute(Track track) {
				updatePlayPause(true);
			}
		};
		task.execute(track);

		new GetAlbumArtTask(albumArt).execute(track);
		mSongName.setText(track.trackName + " " + track.albumName + " "
				+ track.artistName);
	}

	private void playPause() {
		if (player != null) {
			if (player.isPlaying()) {
				player.pause();
				updatePlayPause(false);
			} else {
				player.start();
				updatePlayPause(true);
			}
		} else {
			next(true);
		}
	}

	private void updatePlayPause(boolean playing) {
		if (playing) {
			playPause.setImageResource(R.drawable.pause);
		} else {
			playPause.setImageResource(R.drawable.play);
		}
	}

	@Override
	public void onRdioReadyForPlayback() {
		Log.i(TAG, "Rdio SDK is ready for playback");

		if (accessToken != null && accessTokenSecret != null) {
			doSomething();
		} else {
			Toast.makeText(MainActivity.this, "Can't find the app!",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onRdioUserPlayingElsewhere() {
		Log.w(TAG, "Tell the user that playback is stopping.");
	}

	@Override
	public void onRdioAuthorised(String accessToken, String accessTokenSecret) {
		Log.i(TAG, "Application authorised, saving access token & secret.");
		Log.d(TAG, "Access token: " + accessToken);
		Log.d(TAG, "Access token secret: " + accessTokenSecret);

		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		Editor editor = settings.edit();
		editor.putString(PREF_ACCESSTOKEN, accessToken);
		editor.putString(PREF_ACCESSTOKENSECRET, accessTokenSecret);
		editor.commit();
	}

	/*************************
	 * Activity overrides
	 *************************/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Log.v(TAG, "Login success");
				if (data != null) {
					accessToken = data.getStringExtra("token");
					accessTokenSecret = data.getStringExtra("tokenSecret");
					onRdioAuthorised(accessToken, accessTokenSecret);
					rdio.setTokenAndSecret(accessToken, accessTokenSecret);
				}
			} else if (resultCode == RESULT_CANCELED) {
				if (data != null) {
					String errorCode = data
							.getStringExtra(OAuth1WebViewActivity.EXTRA_ERROR_CODE);
					String errorDescription = data
							.getStringExtra(OAuth1WebViewActivity.EXTRA_ERROR_DESCRIPTION);
					Log.v(TAG, "ERROR: " + errorCode + " - " + errorDescription);
				}
				accessToken = null;
				accessTokenSecret = null;
			}
			rdio.prepareForPlayback();
		}
	}

	/*************************
	 * Dialog helpers
	 *************************/
	private void showGetUserDialog() {
		if (getUserDialog == null) {
			getUserDialog = new RdioProgress();
		}

		if (getUserDialog.isAdded()) {
			return;
		}

		Bundle args = new Bundle();
		args.putString("message",
				getResources().getString(R.string.getting_user));

		getUserDialog.setArguments(args);
		getUserDialog.show(getFragmentManager(), "getUserDialog");
	}

	private void dismissGetUserDialog() {
		if (getUserDialog != null) {
			getUserDialog.dismiss();
		}
	}

	private void showGetCollectionDialog() {
		if (getCollectionDialog == null) {
			getCollectionDialog = new RdioProgress();
		}

		if (getCollectionDialog.isAdded()) {
			return;
		}

		Bundle args = new Bundle();
		args.putString("message",
				getResources().getString(R.string.getting_collection));

		getCollectionDialog.setArguments(args);
		getCollectionDialog.show(getFragmentManager(), "getCollectionDialog");
	}

	private void dismissGetCollectionDialog() {
		if (getCollectionDialog != null) {
			getCollectionDialog.dismiss();
		}
	}

	private class GenericClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.greenthumbview:
				next(true);
				break;
			case R.id.redthumbview:
				next(true);
				break;
			case R.id.playPause:
				playPause();
				break;
			case R.id.albumArt:
				playPause();
				break;
			default:
				break;
			}
		}
	}

}
