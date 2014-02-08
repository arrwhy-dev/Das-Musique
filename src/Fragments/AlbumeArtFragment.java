package Fragments;

import Models.Track;
import Tasks.GetAlbumArtTask;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uwaterloo.dasmusique.R;

public class AlbumeArtFragment extends Fragment {

	private ImageView mAlbumArtView;
	private ImageView playPause;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragmentlayout, container,
				false);
		mAlbumArtView = (ImageView) rootView.findViewById(R.id.albumArt);
		playPause = (ImageView) rootView.findViewById(R.id.playPause);

		return rootView;
	}

	public void updateAlbumArt(Track track) {
		new GetAlbumArtTask(mAlbumArtView).execute(track);

	}

	public void updatePause() {
		playPause.setImageResource(R.drawable.pause);
	}

	public void updatePlay() {
		playPause.setImageResource(R.drawable.play);

	}
}
