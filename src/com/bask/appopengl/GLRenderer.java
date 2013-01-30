/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
***/
package com.bask.appopengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

class GLRenderer implements GLSurfaceView.Renderer {
   private static final String TAG = "GLRenderer";
   private final Context context;
   private final GLCube cube = new GLCube();
   private long startTime;
   private long fpsStartTime;
   private long numFrames;
   public Rotator rotator;
   float deltaX = 0.0f;
   float deltaY = 0.0f;
   
   GLRenderer(Context context, Rotator rot) {
      this.context = context;
      rotator = rot;
   }

   public void onSurfaceCreated(GL10 gl, EGLConfig config) {
      // ...
      boolean SEE_THRU = true;
      startTime = System.currentTimeMillis();
      fpsStartTime = startTime;
      numFrames = 0;

      // Define the lighting
      float lightAmbient[] = new float[] { 0.2f, 0.2f, 0.2f, 1 };
      float lightDiffuse[] = new float[] { 1, 1, 1, 1 };
      float[] lightPos = new float[] { 1, 1, 1, 1 };
      gl.glEnable(GL10.GL_LIGHTING);
      gl.glEnable(GL10.GL_LIGHT0);
      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
      gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
      
      // What is the cube made of?
      float matAmbient[] = new float[] { 1, 1, 1, 1 };
      float matDiffuse[] = new float[] { 1, 1, 1, 1 };
      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
            matAmbient, 0);
      gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
            matDiffuse, 0);
      
      // Set up any OpenGL options we need
      gl.glEnable(GL10.GL_DEPTH_TEST); 
      gl.glDepthFunc(GL10.GL_LEQUAL);
      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

      // Optional: disable dither to boost performance
      // gl.glDisable(GL10.GL_DITHER);
      // ...
      if (SEE_THRU) {
         gl.glDisable(GL10.GL_DEPTH_TEST);
         gl.glEnable(GL10.GL_BLEND);
         gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
      }
      
      // Enable textures
      gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
      gl.glEnable(GL10.GL_TEXTURE_2D);

      // Load the cube's texture from a bitmap
      GLCube.loadTexture(gl, context, R.drawable.mashulka);
   }
   
   public void onSurfaceChanged(GL10 gl, int width, int height) {
      // ...
      // Define the view frustum
      gl.glViewport(0, 0, width, height);
      gl.glMatrixMode(GL10.GL_PROJECTION);
      gl.glLoadIdentity();
      float ratio = (float) width / height;
      GLU.gluPerspective(gl, 45.0f, ratio, 1, 100f); 
   }
   
   public void onDrawFrame(GL10 gl) {
      // ...
      // Clear the screen to black
      gl.glClear(GL10.GL_COLOR_BUFFER_BIT
            | GL10.GL_DEPTH_BUFFER_BIT);

      // Position model so we can see it
      gl.glMatrixMode(GL10.GL_MODELVIEW);
      gl.glLoadIdentity();
      gl.glTranslatef(0, 0, -3.0f);

      // Other drawing commands go here...
      
      // Set rotation angle based on the time
      long elapsed = System.currentTimeMillis() - startTime;
      //float deltaX = elapsed * (15f / 1000f);
      //float deltaY = elapsed * (30f / 1000f);
 
   	  deltaX += -rotator.deltaX*5;
      //if(Math.abs(deltaY+rotator.deltaY)<45.0f)
   	  deltaY += rotator.deltaY*5;      
 
     	rotator.deltaX = 990*rotator.deltaX/1000;
       	rotator.deltaY = 990*rotator.deltaY/1000;
   	  
     	//if(rotator.deltaX<0.000001) rotator.deltaX=0;
       	//if(rotator.deltaY<0.000001) rotator.deltaY=0;
       	
      gl.glRotatef(deltaX, 0, 1, 0);
      gl.glRotatef(deltaY, 1, 0, 0);
      
      // Draw the model
      cube.draw(gl);
      
      // Keep track of number of frames drawn
      numFrames++;
      long fpsElapsed = System.currentTimeMillis() - fpsStartTime;
      if (fpsElapsed > 5 * 1000) { // every 5 seconds
         float fps = (numFrames * 1000.0F) / fpsElapsed;
         //Log.d(TAG, "Frames per second: " + fps + " (" + numFrames
         //      + " frames in " + fpsElapsed + " ms)");
         fpsStartTime = System.currentTimeMillis();
         numFrames = 0;
      }
   }
}