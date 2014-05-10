package fragments;

import models.Track;
import tasks.GetAlbumArtTask;
import activites.MainActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uwaterloo.dasmusique.R;

public class MainFragment extends Fragment {

	private static ImageView albumArt;
	private ImageView playPause;
	private TextView mSongName;
	private ImageView mgreenThumb;
	private ImageView mredThumb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mainfragment, null);

		mSongName = (TextView) view.findViewById(R.id.songTitle);
		mgreenThumb = (ImageView) view.findViewById(R.id.greenthumbview);
		mredThumb = (ImageView) view.findViewById(R.id.redthumbview);
		playPause = (ImageView) view.findViewById(R.id.playPause);
		albumArt = (ImageView) view.findViewById(R.id.albumArt);

		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mgreenThumb.setOnClickListener(new GenericClickListener());
		mredThumb.setOnClickListener(new GenericClickListener());
		playPause.setOnClickListener(new GenericClickListener());
	}

	public void updateSongDetails(Track track) {

		new GetAlbumArtTask(albumArt, track,mSongName, getActivity()).execute(track);
		

	}

	public void updatePlayPause(boolean isPlaying) {
		if (isPlaying) {
			playPause.setImageResource(R.drawable.pause);
		} else {
			playPause.setImageResource(R.drawable.play);
		}
	}

	private class GenericClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.greenthumbview:
				((MainActivity) getActivity()).next(true);
				break;
			case R.id.redthumbview:
				((MainActivity) getActivity()).next(true);
				break;
			case R.id.playPause:
				((MainActivity) getActivity()).playPause();
				break;
			default:
				break;
			}
		}
	}

}
