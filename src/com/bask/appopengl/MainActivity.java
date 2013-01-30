package com.bask.appopengl;

//import com.jfdimarzio.PBGameVars;
//import com.jfdimarzio.PBMainMenu;
//import com.jfdimarzio.PrisonbreakActivity;
//import com.jfdimarzio.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView; 
import android.util.Log;
import android.view.Window;
import android.view.WindowManager; 
import android.hardware.SensorManager;
import android.hardware.SensorListener;

public class MainActivity extends Activity implements SensorListener  
{
	GLSurfaceView view;
	SensorManager sm = null;
    Rotator rotator = new Rotator();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	sm = (SensorManager) getSystemService(SENSOR_SERVICE); 
    	
    	setContentView(R.layout.splashscreen);
 	    view = new GLSurfaceView(this);  
 	    
 	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
 	    
 	    view = new GLView(this, rotator); 

 	    setContentView(view); 
/*
 	    new Handler().postDelayed(new Thread() {
        		@Override
        		public void run() {
        	    	Log.d("MainActivity", "4");
 
        	    	Intent mainMenu = new Intent(MainActivity.this, MainMenu.class);
        	    	Log.d("MainActivity", "5");
                    //MainActivity.this.startActivity(mainMenu);
        	    	Log.d("MainActivity", "6");
                    //MainActivity.this.finish();
        	    	
                    //Log.d("MainActivity", "7");
                    //overridePendingTransition(R.layout.fadein,R.layout.fadeout);
        		}
        	}, 3000);
    	Log.d("MainActivity", "8");
*/    	
    }

    public void onSensorChanged(int sensor, float[] values) {
        synchronized (this) {
            //Log.d(tag, "onSensorChanged: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);
            if (sensor == SensorManager.SENSOR_ORIENTATION) {
	            //rotator.deltaX = values[0];
	            //rotator.deltaY = values[1];
            	//drawView.zViewO = values[2];
            }
            if (sensor == SensorManager.SENSOR_ACCELEROMETER) {

            	//акселерометр
            	//rotator.deltaX = values[0];
	            //rotator.deltaY = values[1];
	            
	            //drawView.zViewA = values[2];
				//drawView.Paint();
            }            
        }
    }
    
    public void onAccuracyChanged(int sensor, int accuracy) {
    	//Log.d(tag,"onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }
 

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, 
                SensorManager.SENSOR_ORIENTATION |
        		SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onStop() {
        sm.unregisterListener(this);
        super.onStop();
    }     
}
