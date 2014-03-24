package Tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import Models.Track;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.uwaterloo.dasmusique.R;

public class GetAlbumArtTask extends AsyncTask<Track, Void, Bitmap>
{

	private ImageView mImageView;
	private Track mTrack;
	private Context mContext;
	
	public GetAlbumArtTask(ImageView iview, Track track,Context context)
	{
		mImageView = iview;
		mTrack = track;
		mContext = context;
	}

	@Override
	protected Bitmap doInBackground(Track... params)
	{
		Track track = params[0];
		try
		{
			String artworkUrl = track.albumArt.replace("square-200",
					"square-600");

			Log.i("TAG", "Downloading album art: " + artworkUrl);

			Bitmap bm = null;
			try
			{
				URL aURL = new URL(artworkUrl);
				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				bm = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
			}
			catch (IOException e)
			{
				Log.e("TAG", "Error getting bitmap", e);
			}
			return bm;
		}
		catch (Exception e)
		{
			Log.e("TAG", "Error downloading artwork", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(final Bitmap artwork)
	{
		if (artwork != null)
		{
			Animation fadeIn = new AlphaAnimation(0, 1);
			fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
			fadeIn.setDuration(1000);
			
			mImageView.setVisibility(View.INVISIBLE);
			mImageView.setImageBitmap(artwork);
			fadeIn.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{}
				
				@Override
				public void onAnimationRepeat(Animation animation)
				{}
				
				@Override
				public void onAnimationEnd(Animation animation)
				{
					mImageView.setVisibility(View.VISIBLE);
					mImageView.setImageBitmap(artwork);

				}
			});
			mImageView.startAnimation(fadeIn);
			Notification n  = new Notification.Builder(mContext)
	        .setContentTitle(mTrack.trackName)
	        .setContentText(mTrack.albumName)
	        .setSmallIcon(R.drawable.greenthumb)
	        .setLargeIcon(artwork)
	        .addAction(R.drawable.gt_notification_icon, "Like", null)
			.addAction(R.drawable.rt_notification_icon,"DisLike",null)
	        .setAutoCancel(true).build();

	    
	  
	NotificationManager notificationManager = 
	  (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);

	notificationManager.notify(0, n);
			
		}
		else
			mImageView.setImageResource(R.drawable.blank_album_art);
	}

}
