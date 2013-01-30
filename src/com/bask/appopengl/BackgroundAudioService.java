package com.bask.appopengl;

import android.app.Service; 
import android.content.Intent; 
import android.media.MediaPlayer; 
import android.media.MediaPlayer.OnCompletionListener; 
import android.os.IBinder; 
import android.util.Log; 

public class BackgroundAudioService extends Service implements OnCompletionListener 
{ 
	MediaPlayer mediaPlayer; 
	 
    @Override 
    public IBinder onBind(Intent intent) { 
        return null; 
    } 
 
    @Override 
    public void onCreate() { 
        Log.i("PLAYERSERVICE","onCreate");
        mediaPlayer = MediaPlayer.create(this, R.raw.game_in_the_brain);
        mediaPlayer.setOnCompletionListener(this); 
        mediaPlayer.setLooping(true);
    }
    
    @Override 
    public int onStartCommand(Intent intent, int flags, int startId) { 
        Log.i("PLAYERSERVICE","onStartCommand"); 
 
        if (!mediaPlayer.isPlaying()) { 
            mediaPlayer.start(); 
        } 
        return START_STICKY; 
    } 
    
    public void onDestroy() { 
        if (mediaPlayer.isPlaying()) 
        { 
            mediaPlayer.stop(); 
        } 
        mediaPlayer.release(); 
        Log.i("SIMPLESERVICE","onDestroy"); 
    } 
    
    public void onCompletion(MediaPlayer _mediaPlayer) { 
        stopSelf(); 
    } 
}
