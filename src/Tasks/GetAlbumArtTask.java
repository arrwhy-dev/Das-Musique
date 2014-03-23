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
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.uwaterloo.dasmusique.R;

public class GetAlbumArtTask extends AsyncTask<Track, Void, Bitmap>
{

	private ImageView mImageView;

	public GetAlbumArtTask(ImageView iview)
	{
		mImageView = iview;
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
//			AlphaAnimation anim = new AlphaAnimation(0, 1);
//			anim.setDuration(1000);
//			anim.setRepeatCount(0);
//			anim.setRepeatMode(Animation.REVERSE);
//			anim.setAnimationListener(new AnimationListener()
//			{
//				
//				@Override
//				public void onAnimationStart(Animation animation)
//				{
//					//just run the animation
//				}
//				
//				@Override
//				public void onAnimationRepeat(Animation animation)
//				{
//					//do nothing here
//				}
//				
//				@Override
//				public void onAnimationEnd(Animation animation)
//				{
//					mImageView.setImageBitmap(artwork);
//					
//				}
//			});
//			
//			mImageView.startAnimation(anim);

			//fade in and out animations
			
			
			Animation fadeIn = new AlphaAnimation(0, 1);
			fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
			fadeIn.setDuration(1000);

//			Animation fadeOut = new AlphaAnimation(1, 0);
//			fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
//			fadeOut.setStartOffset(1000);
//			fadeOut.setDuration(1000);

//			AnimationSet animation = new AnimationSet(false); //change to false
//			animation.addAnimation(fadeIn);
//			animation.addAnimation(fadeOut);
			
			mImageView.setVisibility(View.INVISIBLE);
			mImageView.setImageBitmap(artwork);
			fadeIn.setAnimationListener(new AnimationListener()
			{
				
				@Override
				public void onAnimationStart(Animation animation)
				{
					// TODO Auto-generated method stub
					
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation)
				{
					mImageView.setVisibility(View.VISIBLE);
					mImageView.setImageBitmap(artwork);

				}
			});
			mImageView.startAnimation(fadeIn);
			
		}
		else
			mImageView.setImageResource(R.drawable.blank_album_art);
	}

}
