import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class RdioPlaybackService extends Service 

{

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		
		playMusic();
		return Service.START_STICKY;
	}
	
	
	private void playMusic()
	{
		
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				//do the music shit in here
				
			}
		});
	}

}
