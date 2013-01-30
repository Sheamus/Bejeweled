package com.bask.appopengl;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class MainMenu extends Activity {
	
	Intent playbackServiceIntent; 
	Intent playbackServiceIntent2; 
	
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);

	        SoundManager.getInstance();
	        SoundManager.initSounds(this);
	        SoundManager.loadSounds();
	        
	        SoundManager.playSound(1, 1);
	        
	        setContentView(R.layout.main);
	        playbackServiceIntent = new Intent(this, BackgroundAudioService.class);
	        playbackServiceIntent2 = new Intent(this, GameBgAudio01.class);
	        
	        startService(playbackServiceIntent); 
            //finish(); 
	        ImageButton startOpenGLapp = (ImageButton)findViewById(R.id.btnStart);
	        startOpenGLapp.getBackground().setAlpha(0);
	        startOpenGLapp.setHapticFeedbackEnabled(true);
	       
	        startOpenGLapp.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					stopService(playbackServiceIntent);
					//stopService(new Intent(this, MyService.class));
					
					Intent appOpenGLintent = new Intent(getApplicationContext(), MainActivity.class);
					MainMenu.this.startActivity(appOpenGLintent);
				}
	        });
	        
	        ImageButton startBejeweled = (ImageButton)findViewById(R.id.btnBejeweledStart);
	        startBejeweled.getBackground().setAlpha(0);
	        startBejeweled.setHapticFeedbackEnabled(true);
	       
	        startBejeweled.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					SoundManager.playSound(1, 1);
					
					Intent bejeweledIntent = new Intent(getApplicationContext(), BejeweledGameActivity.class);
					MainMenu.this.startActivity(bejeweledIntent);
				}
	        });

	        ImageButton startBubbles = (ImageButton)findViewById(R.id.btnAction);
	        startBubbles.getBackground().setAlpha(0);
	        startBubbles.setHapticFeedbackEnabled(true);
	       
	        startBubbles.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					stopService(playbackServiceIntent);

			        //startService(playbackServiceIntent2); 

					Intent bubblesIntent = new Intent(getApplicationContext(), BubblesActivity.class);
					MainMenu.this.startActivity(bubblesIntent);
				}
	        });
/*
	        ImageButton startBilliard = (ImageButton)findViewById(R.id.btnBilliardStart);
	        startBilliard.setHapticFeedbackEnabled(true);
	       
	        startBilliard.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					Intent billiardIntent = new Intent(getApplicationContext(), MainActivity.class);
					MainMenu.this.startActivity(billiardIntent);
				}
	        });
	        */
	        ImageButton exit = (ImageButton)findViewById(R.id.btnExit);
	        exit.getBackground().setAlpha(0); 
	        exit.setHapticFeedbackEnabled(true);
	        
	        exit.setOnClickListener(new OnClickListener(){ 
				public void onClick(View v) {
					boolean clean = false;
					stopService(playbackServiceIntent); 					
					stopService(playbackServiceIntent2);
					
					SoundManager.playSound(5, 1);
					//clean = engine.onExit(v);	
					//if (clean)
					{
						int pid= android.os.Process.myPid();
						android.os.Process.killProcess(pid);
					}
				}
	        	});
	    }

	    /*private void onBackPressed() {
	        stopService(playbackServiceIntent));
	   }*/
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        Log.i("MainMenu", "onResume");
			startService(playbackServiceIntent); 					
			//stopService(playbackServiceIntent2);
	    }
	    
	    @Override
	    protected void onStop() {
			//stopService(playbackServiceIntent); 					
			//stopService(playbackServiceIntent2);
	        super.onStop();
	    }     
}
