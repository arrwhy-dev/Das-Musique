package Tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import Models.Track;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.uwaterloo.dasmusique.R;

public class GetAlbumArtTask extends AsyncTask<Track, Void, Bitmap> {

	private ImageView mImageView;

	public GetAlbumArtTask(ImageView iview) {
		mImageView = iview;
	}

	@Override
	protected Bitmap doInBackground(Track... params) {
		Track track = params[0];
		try {
			String artworkUrl = track.albumArt.replace("square-200",
					"square-600");

			Log.i("TAG", "Downloading album art: " + artworkUrl);

			Bitmap bm = null;
			try {
				URL aURL = new URL(artworkUrl);
				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				bm = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
			} catch (IOException e) {
				Log.e("TAG", "Error getting bitmap", e);
			}
			return bm;
		} catch (Exception e) {
			Log.e("TAG", "Error downloading artwork", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap artwork) {
		if (artwork != null) {
			mImageView.setImageBitmap(artwork);
		} else
			mImageView.setImageResource(R.drawable.blank_album_art);
	}

}
