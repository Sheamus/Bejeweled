/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/
package com.bask.appopengl;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class OpenGL extends Activity {
   GLView view;
   Rotator rotator;
   
   public OpenGL(Rotator rot)
   {
	   rotator = rot;
   }
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
      Log.d("OpenGL.java", "onCreate");
	  super.onCreate(savedInstanceState);
      view = new GLView(this, rotator);
      setContentView(view);
   }

   @Override
   protected void onPause() {
       super.onPause();
       view.onPause();
   }

   @Override
   protected void onResume() {
       super.onResume();
       view.onResume();
   }
}