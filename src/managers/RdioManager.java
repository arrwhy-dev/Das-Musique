package managers;

import android.content.Context;
import android.content.Intent;
import api.ApiKeys;

import com.rdio.android.api.OAuth1WebViewActivity;
import com.rdio.android.api.Rdio;

public class RdioManager {

	private static Rdio rdio;

	private static String mAccessToken;
	private static String mAccessTokenSecret;

	private RdioManager() {

	}

	private static void init(Context context) {

		if (mAccessToken == null || mAccessTokenSecret == null) {
			mAccessToken = mAccessTokenSecret = null;
			Intent myIntent = new Intent(context, OAuth1WebViewActivity.class);
			myIntent.putExtra(OAuth1WebViewActivity.EXTRA_CONSUMER_KEY,
					ApiKeys.KEY_APPLICATION);
			myIntent.putExtra(OAuth1WebViewActivity.EXTRA_CONSUMER_SECRET,
					ApiKeys.KEY_APP_SECRET);
			// MainActivity.this.startActivityForResult(myIntent, 1);
		}
	}

	public static Rdio getInstance(Context context) {

		if (rdio == null) {
			init(context);
		}
		return rdio;
	}

}
