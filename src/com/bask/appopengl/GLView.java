/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/

package com.bask.appopengl;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

class GLView extends GLSurfaceView implements OnTouchListener 
{
   private final GLRenderer renderer;
   public Rotator rotator;
   Point startPoint = new Point();
   
   GLView(Context context, Rotator rot) {
      super(context);
      
      setFocusable(true);
      setFocusableInTouchMode(true);

      this.setOnTouchListener(this);
      
      rotator = rot;
      // Uncomment this to turn on error-checking and logging
      //setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);

      renderer = new GLRenderer(context, rotator);
      setRenderer(renderer);
   }
   
   public boolean onTouch(View view, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			startPoint.x = (int) event.getX(0);
	        startPoint.y = (int) event.getY(0);
		}
	   
	   if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
		   rotator.deltaX = -(event.getX(0) - startPoint.x)/10; 
		   rotator.deltaY = (event.getY(0) - startPoint.y)/10;
			startPoint.x = (int) event.getX(0);
	        startPoint.y = (int) event.getY(0);
	   /*	AlertDialog alertDialog;
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("ACTION_UP!");
			alertDialog.setMessage("send data to nciku");
			alertDialog.show();
	*/
			//Toast.makeText(getApplicationContext(), "Sending data...", Toast.LENGTH_SHORT).show();
		}
	        //return super.onTouchEvent(event);
/*	        
		    Point point = new Point();
			point.first = (event.getAction() == MotionEvent.ACTION_DOWN);
	        point.x = event.getX();
	        point.y = event.getY();
	        points.add(point);
	        invalidate();
	        Log.d(TAG, "point: " + point);*/
		
	        return true;
	    }

}